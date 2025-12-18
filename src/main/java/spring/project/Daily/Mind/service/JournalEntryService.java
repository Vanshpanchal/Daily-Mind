package spring.project.Daily.Mind.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spring.project.Daily.Mind.entity.JournalEntry;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.repository.JournalRepository;
import spring.project.Daily.Mind.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Component
public class JournalEntryService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    AutoTaggingService autoTaggingService;

    @Autowired
    MongoTemplate mongoTemplate;


    @Transactional
    public void saveEntry(JournalEntry myEntry, String userName) {
        myEntry.setDate(LocalDateTime.now());
        Users user = userRepository.findByusername(userName);
        List<String> strings = autoTaggingService.AutoTaggingJournalEntry(myEntry.getContent());
        myEntry.setTags(strings);
        JournalEntry save = journalRepository.save(myEntry);
        user.getJournalEntries().add(save);
        // Avoid re-encoding password unintentionally when updating user document
        userService.updateUserPreservePassword(user);
    }

    public Page<JournalEntry> findByUserName(String userName, Pageable pageable) {
        Users user = userRepository.findByusername(userName);
        if (user == null) {
            return Page.empty();
        }
        List<ObjectId> journalEntryIds = user.getJournalEntries().stream()
                .map(JournalEntry::getById)
                .toList();
        return journalRepository.findByIdIn(journalEntryIds, pageable);
    }

//    public List<JournalEntry> fetchEntry() {
//        return journalRepository.findAll();
//    }

    public Optional<JournalEntry> findById(ObjectId Id) {
        return journalRepository.findById(Id);
    }

    // Legacy delete without ownership check (kept for compatibility)
    public boolean deleteById(ObjectId Id) {
        journalRepository.deleteById(Id);
        return true;
    }

    // New delete that enforces ownership and keeps the user's journal list in sync
    @Transactional
    public boolean deleteById(ObjectId Id, String username) {
        Users user = userRepository.findByusername(username);
        if (user == null) return false;
        boolean existed = user.getJournalEntries().removeIf(x -> x.getById().equals(Id));
        if (!existed) {
            return false; // entry not owned by user or not found in user's list
        }
        userService.updateUserPreservePassword(user);
        journalRepository.deleteById(Id);
        return true;
    }

    @Transactional
    public JournalEntry UpdateById(ObjectId Id, JournalEntry Entry, String username) {
        Users user = userRepository.findByusername(username);
        if (user == null) return null;
        boolean owned = user.getJournalEntries().stream().anyMatch(e -> e.getById().equals(Id));
        if (!owned) {
            return null; // not owned by this user
        }

        JournalEntry oldEntry = journalRepository.findById(Id).orElse(null);

        if (oldEntry != null) {
            if (Entry.getContent() != null && !Entry.getContent().isBlank()) {
                oldEntry.setContent(Entry.getContent());
            }
            if (Entry.getTitle() != null && !Entry.getTitle().isBlank()) {
                oldEntry.setTitle(Entry.getTitle());
            }
            journalRepository.save(oldEntry);
        }

        return oldEntry;
    }

    @Transactional(readOnly = true)
    public JournalEntry getJournalContentById(ObjectId id, String username) {

        Users user = userRepository.findByusername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Check if the entry belongs to the logged-in user
        boolean owned = user.getJournalEntries().stream().anyMatch(e -> e.getById().equals(id));

        if (!owned) {
            throw new AccessDeniedException("You do not own this journal entry.");
        }

        return journalRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Journal entry not found."));
    }


    public List<JournalEntry> DateFilter(String username, String startDate, String endDate) {

        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        Query userQuery = new Query();

        userQuery.addCriteria(Criteria.where("username").is(username));

        Users user = mongoTemplate.findOne(userQuery, Users.class);

        List<ObjectId> entryIds = user.getJournalEntries().stream().map(JournalEntry::getById).toList();

        Query DateFilterQuery = new Query();

        DateFilterQuery.addCriteria(Criteria.where("date").gte(start).lte(end));
        DateFilterQuery.addCriteria(Criteria.where("id").in(entryIds));
        return mongoTemplate.find(DateFilterQuery, JournalEntry.class);


    }

    public List<JournalEntry> Search(String query,Users user) {


        List<JournalEntry> journalEntries = user.getJournalEntries();
        List<ObjectId> entryIds = journalEntries.stream().map(JournalEntry::getById).toList();

        Document searchStage = Document.parse("""
                {
                  "$search": {
                    "index": "journal_search",
                    "text": {
                      "query": "%s",
                      "path": ["title", "content", "tags"],
                      "fuzzy": { "maxEdits": 2 }
                    }
                  }
                }
                """.formatted(query));

        Aggregation aggregation = Aggregation.newAggregation(
                context -> searchStage,
                Aggregation.limit(10), Aggregation.sort(Sort.by(Sort.Direction.DESC, "date")
                )
        );


        return mongoTemplate
                .aggregate(aggregation, "journal_entries", JournalEntry.class)
                .getMappedResults().stream().filter(
                        entry -> entryIds.contains(entry.getById())
                ).toList();
    }
}
