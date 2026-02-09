package com.finance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.finance.model.Goal;
import com.finance.model.Transaction;

@Service
public class PromptBuilderService {

    public String buildPrompt(List<Transaction> transactions,
                              List<Goal> goals) {

        StringBuilder sb = new StringBuilder();

        sb.append("""
        You are a personal finance advisor.
        Use popular personal finance models such as:
        - 50/30/20 rule
        - Needs vs Wants
        - Expense optimization
        - Emergency fund planning

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
        Suggest optimizations.
        Give clear, actionable tips.
        """);

        return sb.toString();
    }
}
