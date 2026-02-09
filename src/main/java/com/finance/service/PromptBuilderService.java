package com.finance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.finance.model.Goal;
import com.finance.model.Transaction;

@Service
public class PromptBuilderService {

    private final RagService ragService;

    public PromptBuilderService(RagService ragService) {
        this.ragService = ragService;
    }

    public String buildPrompt(List<Transaction> transactions,
                              List<Goal> goals) {

        StringBuilder sb = new StringBuilder();

        String categorizationQuery = buildCategorizationQuery(transactions);
        String recommendationsQuery = buildRecommendationsQuery(goals, transactions);

        List<String> categorizationContext =
                ragService.retrieve("categorization", categorizationQuery, 4);
        List<String> recommendationsContext =
                ragService.retrieve("recommendations", recommendationsQuery, 4);

        sb.append("""
        You are a personal finance advisor.
        Use the retrieved knowledge sections for categorization and recommendations.

        RAG - CATEGORIZATION KNOWLEDGE:
        """);

        for (String snippet : categorizationContext) {
            sb.append("- ").append(snippet).append("\n");
        }

        sb.append("""

        RAG - RECOMMENDATION KNOWLEDGE:
        """);

        for (String snippet : recommendationsContext) {
            sb.append("- ").append(snippet).append("\n");
        }

        sb.append("""

        USER GOALS:
        """);

        for (Goal goal : goals) {
            sb.append("- ")
              .append(goal.getTitle())
              .append(": ")
              .append(goal.getDescription())
              .append("\n");
        }

        sb.append("\nUSER TRANSACTIONS:\n");

        for (Transaction t : transactions) {
            sb.append(String.format(
                "%s | %s | %.2f | %s\n",
                t.getType(),
                t.getCategory(),
                t.getAmount(),
                t.getDescription()
            ));
        }

        sb.append("""
        Analyze spending behavior.
        Use the categorization knowledge to map transactions into categories.
        Apply the recommendation knowledge when suggesting actions for goals.
        Suggest optimizations.
        Give clear, actionable tips.
        """);

        return sb.toString();
    }

    private String buildCategorizationQuery(List<Transaction> transactions) {
        StringBuilder query = new StringBuilder();
        for (Transaction transaction : transactions) {
            query.append(transaction.getCategory()).append(' ')
                 .append(transaction.getDescription()).append(' ')
                 .append(transaction.getType()).append(' ');
        }
        return query.toString();
    }

    private String buildRecommendationsQuery(List<Goal> goals, List<Transaction> transactions) {
        StringBuilder query = new StringBuilder();
        for (Goal goal : goals) {
            query.append(goal.getTitle()).append(' ')
                 .append(goal.getDescription()).append(' ')
                 .append(goal.getTargetAmount()).append(' ');
        }
        for (Transaction transaction : transactions) {
            query.append(transaction.getCategory()).append(' ')
                 .append(transaction.getAmount()).append(' ');
        }
        return query.toString();
    }
}
