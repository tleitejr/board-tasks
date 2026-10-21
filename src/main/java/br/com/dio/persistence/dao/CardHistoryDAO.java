package br.com.dio.persistence.dao;

import br.com.dio.dto.CardColumnDurationDTO;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@RequiredArgsConstructor
public class CardHistoryDAO {

    private final Connection connection;

    public void enterColumn(final Long cardId, final Long columnId, final OffsetDateTime enteredAt) throws SQLException {
        var sql = "INSERT INTO CARD_COLUMN_HISTORY (card_id, board_column_id, entered_at) VALUES (?, ?, ?);";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);
            statement.setLong(2, columnId);
            statement.setTimestamp(3, toTimestamp(enteredAt));
            statement.executeUpdate();
        }
    }

    public void moveToColumn(final Long cardId, final Long columnId, final OffsetDateTime movedAt) throws SQLException {
        var closeSql = "UPDATE CARD_COLUMN_HISTORY SET exited_at = ? WHERE card_id = ? AND exited_at IS NULL;";
        try (var statement = connection.prepareStatement(closeSql)) {
            statement.setTimestamp(1, toTimestamp(movedAt));
            statement.setLong(2, cardId);
            statement.executeUpdate();
        }
        enterColumn(cardId, columnId, movedAt);
    }

    public List<CardColumnDurationDTO> findDurationsByBoardId(final Long boardId) throws SQLException {
        var sql = """
                SELECT c.id AS card_id, c.title, c.created_at, bc.name AS column_name,
                       bc.kind, h.entered_at, h.exited_at
                  FROM CARDS c
                 INNER JOIN BOARDS_COLUMNS current_column ON current_column.id = c.board_column_id
                 INNER JOIN BOARDS b ON b.id = current_column.board_id
                 INNER JOIN CARD_COLUMN_HISTORY h ON h.card_id = c.id
                 INNER JOIN BOARDS_COLUMNS bc ON bc.id = h.board_column_id
                 WHERE b.id = ?
                 ORDER BY c.id, h.entered_at
                """;
        List<CardColumnDurationDTO> rows = new ArrayList<>();
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new CardColumnDurationDTO(
                            resultSet.getLong("card_id"),
                            resultSet.getString("title"),
                            resultSet.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC),
                            resultSet.getString("column_name"),
                            resultSet.getString("kind"),
                            resultSet.getTimestamp("entered_at").toInstant().atOffset(java.time.ZoneOffset.UTC),
                            resultSet.getTimestamp("exited_at") == null ? null
                                    : resultSet.getTimestamp("exited_at").toInstant().atOffset(java.time.ZoneOffset.UTC)
                    ));
                }
            }
        }
        return rows;
    }
}