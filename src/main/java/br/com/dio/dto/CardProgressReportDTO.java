package br.com.dio.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record CardProgressReportDTO(Long cardId,
                                    String cardTitle,
                                    OffsetDateTime createdAt,
                                    OffsetDateTime completedAt,
                                    Long totalCompletionSeconds,
                                    List<ColumnTimeDTO> columnTimes) {
}