package spring.project.Daily.Mind.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.project.Daily.Mind.DTO.GeminiAiRequestDTO;
import spring.project.Daily.Mind.entity.JournalEntry;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.repository.UserRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SchedulingService {

    @Autowired
    MailService mailService;

    @Autowired
    UserRepositoryImpl userRepositoryImpl;

    @Autowired
    SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    AiService aiService;



    //    @Scheduled(cron = "0 0/1 * 1/1 * ? ")
    public void SendWeeklySaMail() {
        List<Users> users = userRepositoryImpl.getUserFromSA();
        for (Users user : users) {

            List<String> filteredList = user.getJournalEntries().stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minusDays(11)))
                    .map(JournalEntry::getContent)
                    .toList();

            if (filteredList.isEmpty()) continue;

            String contentString = String.join("___", filteredList);

            String sentiment = sentimentAnalysisService.getSentimentAnalysis(contentString,user.getUsername());

            GeminiAiRequestDTO geminiAiRequestDTO = new GeminiAiRequestDTO();

            String prompt = """
                    You are an AI email-writing assistant. The overall sentiment of the user's last 1 days of journal entries is: \\"%s\\".\\n\\nGenerate a SHORT, clean HTML email body (NO markdown, ONLY pure HTML).\\n\\nRequirements:\\n- Keep it brief: 2–3 short paragraphs max.\\n- Start with a simple warm greeting.\\n- In 1–2 sentences, explain what the sentiment (\\"%s\\") suggests about their recent mood.\\n- In 2–3 bullet points, give gentle, practical suggestions.\\n- End with “Daily Mind Team”.\\n\\nStyle rules (dynamic based on sentiment):\\n- If sentiment is \\"positive\\": use a green accent (#4CAF50).\\n- If sentiment is \\"mixed\\": use a blue accent (#1E88E5).\\n- If sentiment is \\"negative\\": use a red accent (#E53935).\\n\\nGeneral:\\n- Use a simple container <div> with light background and padding.\\n- Use <p>, <ul>, <li>, and maybe a small <h2>.\\n- Do NOT mention AI or how the email was generated.\\n- Output ONLY valid HTML ready for email body.
                    """.formatted(sentiment, sentiment);

            geminiAiRequestDTO.getContents().get(0).getParts().get(0).setText(prompt);
            String text = geminiAiRequestDTO.getContents().toString();
            log.info(text);


            String htmlDraftMail = aiService.getAiEmailDraft(
                    geminiAiRequestDTO
            );
            mailService.sendMail1(
                    "Yesterday's Sentiment Analysis",
                    "acc.tempmail123@gmail.com",
                    htmlDraftMail
            );

        }
    }
}

