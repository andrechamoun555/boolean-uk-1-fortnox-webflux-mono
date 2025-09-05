package org.booleanuk.demo.service;

import jakarta.transaction.Transactional;
import org.booleanuk.demo.model.dto.UserDto;
import org.booleanuk.demo.model.jpa.User;
import org.booleanuk.demo.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for (User u : users) {
            userDtos.add(toDto(u));
        }
        return userDtos;
    }

    public UserDto getUserById(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found: " + username));
        return toDto(user);
    }

    public UserDto createUser(UserDto user) {
        User toSave = new User(user.username());
        userRepository.save(toSave);
        return toDto(toSave);
    }

    @Transactional
    public UserDto updateUser(UserDto userDto, String username) {
        User existing = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found: " + username));
        existing.setUsername(userDto.username());
        userRepository.save(existing);
        return toDto(existing);
    }

    @Transactional
    public UserDto deleteUser(String username) {
        User toDelete = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found: " + username));
        userRepository.delete(toDelete);
        return toDto(toDelete);
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.getUsername()
        );
    }
}
