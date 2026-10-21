package br.com.dio.dto;

import java.time.OffsetDateTime;

public record CardColumnDurationDTO(Long cardId,
                                   String cardTitle,
                                   OffsetDateTime createdAt,
                                   String columnName,
                                   String columnKind,
                                   OffsetDateTime enteredAt,
                                   OffsetDateTime exitedAt) {
}