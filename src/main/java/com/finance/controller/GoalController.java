package com.finance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.model.Goal;
import com.finance.service.GoalService;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @PostMapping("/{userId}")
    public Goal createGoal(
            @PathVariable Long userId,
            @RequestBody Goal goal) {
        return service.createGoal(userId, goal);
    }

    @GetMapping("/{userId}")
    public List<Goal> getGoals(@PathVariable Long userId) {
        return service.getUserGoals(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Long id) {
        service.deleteGoal(id);
    }
}
