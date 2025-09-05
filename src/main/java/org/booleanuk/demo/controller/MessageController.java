package org.booleanuk.demo.controller;

import org.booleanuk.demo.model.dto.MessageDto;
import org.booleanuk.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public Mono<List<MessageDto>> getAllMessages() {
        return Mono.fromCallable(() -> messageService.getAllMessages())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MessageDto>> getMessageById(@PathVariable int id) {
        return Mono.fromCallable(() -> messageService.getMessageById(id))
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    public Mono<MessageDto> sendMessage(@RequestBody MessageDto dto) {
        return Mono.fromCallable(() -> messageService.sendMessage(dto))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<MessageDto>> deleteMessage(@PathVariable int id) {
        return Mono.fromCallable(() -> messageService.deleteMessage(id))
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/listen/{username}")
    public Mono<ResponseEntity<MessageDto>> listenForMessages(@PathVariable String username,
                                                              @RequestParam("since") String sinceIso) {
        LocalDateTime since = LocalDateTime.parse(sinceIso);

        return messageService.pollUntilNewMessage(username, since)
                .timeout(Duration.ofMinutes(2)) // avoid infinite wait
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.noContent().build()); // return 204 if timeout
    }

}