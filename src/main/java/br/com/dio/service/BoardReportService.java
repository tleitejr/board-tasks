package br.com.dio.service;

import br.com.dio.dto.CardBlockReportDTO;
import br.com.dio.dto.CardColumnDurationDTO;
import br.com.dio.dto.CardProgressReportDTO;
import br.com.dio.dto.ColumnTimeDTO;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.CardHistoryDAO;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BoardReportService {

    private final Connection connection;

    public List<CardProgressReportDTO> cardProgress(final Long boardId) throws SQLException {
        var rows = new CardHistoryDAO(connection).findDurationsByBoardId(boardId);
        Map<Long, List<CardColumnDurationDTO>> byCard = new LinkedHashMap<>();
        rows.forEach(row -> byCard.computeIfAbsent(row.cardId(), ignored -> new ArrayList<>()).add(row));
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        List<CardProgressReportDTO> reports = new ArrayList<>();
        byCard.values().forEach(cardRows -> {
            var first = cardRows.get(0);
            OffsetDateTime completedAt = cardRows.stream()
                    .filter(row -> "FINAL".equals(row.columnKind()))
                    .map(CardColumnDurationDTO::enteredAt)
                    .findFirst().orElse(null);
            var columns = cardRows.stream().map(row -> {
                var exitedAt = row.exitedAt() == null ? now : row.exitedAt();
                return new ColumnTimeDTO(row.columnName(), Duration.between(row.enteredAt(), exitedAt).getSeconds(),
                        row.enteredAt(), row.exitedAt());
            }).toList();
            Long totalSeconds = completedAt == null ? null
                    : Duration.between(first.createdAt(), completedAt).getSeconds();
            reports.add(new CardProgressReportDTO(first.cardId(), first.cardTitle(), first.createdAt(),
                    completedAt, totalSeconds, columns));
        });
        return reports;
    }

    public List<CardBlockReportDTO> cardBlocks(final Long boardId) throws SQLException {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        return new BlockDAO(connection).findByBoardId(boardId).stream()
                .map(block -> {
                    var end = block.unblockedAt() == null ? now : block.unblockedAt();
                    return new CardBlockReportDTO(block.cardId(), block.cardTitle(), block.blockedAt(),
                            block.unblockedAt(), Duration.between(block.blockedAt(), end).getSeconds(),
                            block.blockReason(), block.unblockReason());
                }).toList();
    }
}