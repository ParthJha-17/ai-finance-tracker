package com.finance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.finance.model.Goal;
import com.finance.model.User;
import com.finance.repository.GoalRepository;
import com.finance.repository.UserRepository;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository,
                       UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public Goal createGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        goal.setUser(user);
        return goalRepository.save(goal);
    }

    public List<Goal> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }
}
