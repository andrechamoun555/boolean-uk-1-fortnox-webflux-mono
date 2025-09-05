package org.booleanuk.demo.model.repository;

import org.booleanuk.demo.model.jpa.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
