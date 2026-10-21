package br.com.dio.dto;

import java.time.OffsetDateTime;

public record ColumnTimeDTO(String columnName,
                            long durationSeconds,
                            OffsetDateTime enteredAt,
                            OffsetDateTime exitedAt) {
}