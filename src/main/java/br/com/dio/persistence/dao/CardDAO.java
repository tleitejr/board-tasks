package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toTimestamp;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (title, description, created_at, board_column_id) values (?, ?, ?, ?);";
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC));
        }
        try(var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            var i = 1;
            statement.setString(i ++, entity.getTitle());
            statement.setString(i ++, entity.getDescription());
            statement.setTimestamp(i ++, toTimestamp(entity.getCreatedAt()));
            statement.setLong(i, entity.getBoardColumn().getId());
            statement.executeUpdate();
            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }
        }
        new CardHistoryDAO(connection).enterColumn(entity.getId(), entity.getBoardColumn().getId(), entity.getCreatedAt());
        return entity;
    }

    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException{
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i ++, columnId);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql =
                """
                  SELECT c.id AS card_id,
                      c.title AS card_title,
                      c.description AS card_description,
                      c.created_at AS created_at,
                      b.blocked_at AS blocked_at,
                      b.block_reason AS block_reason,
                      c.board_column_id AS column_id,
                      bc.name AS column_name,
                       (SELECT COUNT(sub_b.id)
                               FROM BLOCKS sub_b
                              WHERE sub_b.card_id = c.id) blocks_amount
                  FROM CARDS c
                  LEFT JOIN BLOCKS b
                    ON c.id = b.card_id
                   AND b.unblocked_at IS NULL
                 INNER JOIN BOARDS_COLUMNS bc
                    ON bc.id = c.board_column_id
                  WHERE c.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                var dto = new CardDetailsDTO(
                        resultSet.getLong("card_id"),
                        resultSet.getString("card_title"),
                        resultSet.getString("card_description"),
                        toOffsetDateTime(resultSet.getTimestamp("created_at")),
                        nonNull(resultSet.getString("block_reason")),
                        toOffsetDateTime(resultSet.getTimestamp("blocked_at")),
                        resultSet.getString("block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("column_id"),
                        resultSet.getString("column_name")
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }

}
