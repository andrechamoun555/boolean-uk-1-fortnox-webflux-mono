package org.booleanuk.demo.model.repository;

import org.booleanuk.demo.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}