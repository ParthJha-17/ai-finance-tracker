package com.finance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.model.Goal;
import com.finance.model.Transaction;
import com.finance.service.AiInsightService;
import com.finance.service.GoalService;
import com.finance.service.PromptBuilderService;
import com.finance.service.TransactionService;

@RestController
@RequestMapping("/api/insights")
public class AiInsightController {

    private final TransactionService transactionService;
    private final GoalService goalService;
    private final PromptBuilderService promptBuilder;
    private final AiInsightService aiService;

    public AiInsightController(TransactionService transactionService,
                               GoalService goalService,
                               PromptBuilderService promptBuilder,
                               AiInsightService aiService) {
        this.transactionService = transactionService;
        this.goalService = goalService;
        this.promptBuilder = promptBuilder;
        this.aiService = aiService;
    }

    @GetMapping("/{userId}")
    public String getInsights(@PathVariable Long userId) {

        List<Transaction> transactions =
                transactionService.getUserTransactions(userId);

        List<Goal> goals =
                goalService.getUserGoals(userId);

        String prompt =
                promptBuilder.buildPrompt(transactions, goals);

        return aiService.getInsights(prompt);
    }
}
