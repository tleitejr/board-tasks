package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardDAOIntegrationTest {

    @Test
    void insert_cardPersistsCreationTimeAndInitialColumnHistory() throws Exception {
        var url = "jdbc:h2:mem:%s;MODE=MySQL".formatted(UUID.randomUUID().toString().replace("-", ""));
        try (var connection = DriverManager.getConnection(url); var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE BOARDS (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL)");
            statement.execute("CREATE TABLE BOARDS_COLUMNS (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, `order` INT NOT NULL, kind VARCHAR(16) NOT NULL, board_id BIGINT NOT NULL)");
            statement.execute("CREATE TABLE CARDS (id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255) NOT NULL, description VARCHAR(255) NOT NULL, created_at TIMESTAMP NOT NULL, board_column_id BIGINT NOT NULL)");
            statement.execute("CREATE TABLE BLOCKS (id BIGINT AUTO_INCREMENT PRIMARY KEY, blocked_at TIMESTAMP NOT NULL, block_reason VARCHAR(255) NOT NULL, unblocked_at TIMESTAMP NULL, unblock_reason VARCHAR(255) NULL, card_id BIGINT NOT NULL)");
            statement.execute("CREATE TABLE CARD_COLUMN_HISTORY (id BIGINT AUTO_INCREMENT PRIMARY KEY, card_id BIGINT NOT NULL, board_column_id BIGINT NOT NULL, entered_at TIMESTAMP NOT NULL, exited_at TIMESTAMP NULL)");

            var board = new BoardEntity();
            board.setName("Board de integração");
            new BoardDAO(connection).insert(board);

            var column = new BoardColumnEntity();
            column.setName("A fazer");
            column.setOrder(0);
            column.setKind(BoardColumnKindEnum.INITIAL);
            column.setBoard(board);
            new BoardColumnDAO(connection).insert(column);

            var createdAt = OffsetDateTime.of(2026, 10, 21, 12, 30, 0, 0, ZoneOffset.UTC);
            var card = new CardEntity();
            card.setTitle("Testar persistência");
            card.setDescription("Verificar DAO com H2");
            card.setCreatedAt(createdAt);
            card.setBoardColumn(column);
            new CardDAO(connection).insert(card);

            assertNotNull(card.getId());
            assertEquals(createdAt, new CardDAO(connection).findById(card.getId()).orElseThrow().createdAt());
            try (var result = statement.executeQuery("SELECT COUNT(*) FROM CARD_COLUMN_HISTORY WHERE card_id = " + card.getId())) {
                result.next();
                assertEquals(1, result.getInt(1));
            }
        }
    }
}