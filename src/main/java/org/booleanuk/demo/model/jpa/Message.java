package org.booleanuk.demo.model.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "target_name")
    private User targetUser;

    public Message(String text, LocalDateTime date, User targetUser) {
        this.text = text;
        this.date = date;
        this.targetUser = targetUser;
    }
}
