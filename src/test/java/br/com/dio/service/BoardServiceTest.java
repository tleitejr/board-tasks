package br.com.dio.service;

import br.com.dio.exception.BoardInvalidException;
import br.com.dio.persistence.entity.BoardEntity;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class BoardServiceTest {

    @Test
    void insert_boardWithoutColumns_failsBeforeDatabaseAccess() {
        var connection = mock(Connection.class);
        var board = new BoardEntity();
        board.setName("Board inválido");

        assertThrows(BoardInvalidException.class, () -> new BoardService(connection).insert(board));
        verifyNoInteractions(connection);
    }
}