package org.booleanuk.demo.model.dto;

import java.time.LocalDateTime;

public record MessageDto(
        int id,
        String text,
        LocalDateTime date,
        String targetUsername
) { }
