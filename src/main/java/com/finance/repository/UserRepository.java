package com.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
