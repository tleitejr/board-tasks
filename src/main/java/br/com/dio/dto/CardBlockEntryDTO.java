package br.com.dio.dto;

import java.time.OffsetDateTime;

public record CardBlockEntryDTO(Long cardId,
                                String cardTitle,
                                OffsetDateTime blockedAt,
                                OffsetDateTime unblockedAt,
                                String blockReason,
                                String unblockReason) {
}