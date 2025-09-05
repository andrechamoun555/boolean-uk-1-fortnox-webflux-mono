package org.booleanuk.demo.controller;

import org.booleanuk.demo.model.dto.UserDto;
import org.booleanuk.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Mono<List<UserDto>> getAllUsers() {
        return Mono.fromCallable(() -> userService.getAllUsers())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{username}")
    public Mono<ResponseEntity<UserDto>> getUserById(@PathVariable String username) {
        return Mono.fromCallable(() -> userService.getUserById(username))
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping
    public Mono<UserDto> createUser(@RequestBody UserDto userDto) {
        return Mono.fromCallable(() -> userService.createUser(userDto))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping("/{username}")
    public Mono<ResponseEntity<UserDto>> updateUser(@RequestBody UserDto userDto,
                                                    @PathVariable String username) {
        return Mono.fromCallable(() -> userService.updateUser(userDto, username))
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping("/{username}")
    public Mono<ResponseEntity<UserDto>> deleteUser(@PathVariable String username) {
        return Mono.fromCallable(() -> userService.deleteUser(username))
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
