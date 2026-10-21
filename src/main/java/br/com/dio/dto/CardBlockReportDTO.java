package br.com.dio.dto;

import java.time.OffsetDateTime;

public record CardBlockReportDTO(Long cardId,
                                 String cardTitle,
                                 OffsetDateTime blockedAt,
                                 OffsetDateTime unblockedAt,
                                 long durationSeconds,
                                 String blockReason,
                                 String unblockReason) {
}