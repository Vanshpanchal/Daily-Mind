package spring.project.Daily.Mind.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import spring.project.Daily.Mind.entity.JournalEntry;

import java.util.List;

public interface JournalRepository extends MongoRepository<JournalEntry, ObjectId> {

    Page<JournalEntry> findByIdIn(List<ObjectId> ids, Pageable pageable);

}

