package org.booleanuk.demo.service;

import jakarta.transaction.Transactional;
import org.booleanuk.demo.model.dto.MessageDto;
import org.booleanuk.demo.model.jpa.Message;
import org.booleanuk.demo.model.jpa.User;
import org.booleanuk.demo.model.repository.MessageRepository;
import org.booleanuk.demo.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<MessageDto> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        List<MessageDto> dtos = new ArrayList<>();
        for (Message m : messages) {
            dtos.add(toDto(m));
        }
        return dtos;
    }

    public MessageDto getMessageById(int id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found: " + id));
        return toDto(message);
    }

    @Transactional
    public MessageDto sendMessage(MessageDto dto) {
        User target = null;
        if (dto.targetUsername() != null) {
            target = userRepository.findById(dto.targetUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user not found: " + dto.targetUsername()));
        }

        Message message = new Message(
                dto.text(),
                LocalDateTime.now(),
                target
        );

        messageRepository.save(message);
        return toDto(message);
    }

    @Transactional
    public MessageDto deleteMessage(int id) {
        Message toDelete = messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found: " + id));
        messageRepository.delete(toDelete);
        return toDto(toDelete);
    }

    public Mono<MessageDto> pollUntilNewMessage(String username, LocalDateTime since) {
        return Mono.fromCallable(() -> {
                    List<Message> messages = messageRepository.findAll();
                    return messages.stream()
                            .filter(m -> m.getDate().isAfter(since))
                            .filter(m -> m.getTargetUser() != null && m.getTargetUser().getUsername().equals(username))
                            .findFirst();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> {
                    if (opt.isPresent()) {
                        return Mono.just(toDto(opt.get()));
                    } else {
                        return Mono.delay(Duration.ofSeconds(1))
                                .then(pollUntilNewMessage(username, since));
                    }
                });

        }

        public MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getText(),
                message.getDate(),
                message.getTargetUser() != null ? message.getTargetUser().getUsername() : null
        );
    }
}
