package br.com.dio.domain;

import br.com.dio.exception.BoardInvalidException;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardRulesTest {

    @Test
    void validate_boardWithRequiredColumnOrder_doesNotThrow() {
        assertDoesNotThrow(() -> BoardRules.validate("Board", validColumns()));
    }

    @Test
    void validate_finalColumnNotPenultimate_throwsBoardInvalidException() {
        var columns = new java.util.ArrayList<>(validColumns());
        columns.get(1).setKind(BoardColumnKindEnum.CANCEL);
        columns.get(2).setKind(BoardColumnKindEnum.FINAL);

        assertThrows(BoardInvalidException.class, () -> BoardRules.validate("Board", columns));
    }

    private List<BoardColumnEntity> validColumns() {
        return List.of(
                column(0, BoardColumnKindEnum.INITIAL),
                column(1, BoardColumnKindEnum.PENDING),
                column(2, BoardColumnKindEnum.FINAL),
                column(3, BoardColumnKindEnum.CANCEL)
        );
    }

    private BoardColumnEntity column(final int order, final BoardColumnKindEnum kind) {
        var column = new BoardColumnEntity();
        column.setName("Coluna %s".formatted(order));
        column.setOrder(order);
        column.setKind(kind);
        return column;
    }
}