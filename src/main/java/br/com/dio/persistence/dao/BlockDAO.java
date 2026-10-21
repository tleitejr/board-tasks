package br.com.dio.persistence.dao;

import br.com.dio.dto.CardBlockEntryDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void block(final String reason, final Long cardId) throws SQLException {
        var sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public void unblock(final String reason, final Long cardId) throws SQLException{
        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public List<CardBlockEntryDTO> findByBoardId(final Long boardId) throws SQLException {
        var sql = """
                SELECT c.id AS card_id, c.title, b.blocked_at, b.unblocked_at,
                       b.block_reason, b.unblock_reason
                  FROM BLOCKS b
                 INNER JOIN CARDS c ON c.id = b.card_id
                 INNER JOIN BOARDS_COLUMNS bc ON bc.id = c.board_column_id
                 WHERE bc.board_id = ?
                 ORDER BY b.blocked_at, c.id
                """;
        List<CardBlockEntryDTO> blocks = new ArrayList<>();
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    blocks.add(new CardBlockEntryDTO(
                            resultSet.getLong("card_id"),
                            resultSet.getString("title"),
                            toOffsetDateTime(resultSet.getTimestamp("blocked_at")),
                            toOffsetDateTime(resultSet.getTimestamp("unblocked_at")),
                            resultSet.getString("block_reason"),
                            resultSet.getString("unblock_reason")
                    ));
                }
            }
        }
        return blocks;
    }

}
