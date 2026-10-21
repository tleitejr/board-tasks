package br.com.dio.domain;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.MovementInvalidException;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardMovementRulesTest {

    private final List<BoardColumnInfoDTO> columns = List.of(
            new BoardColumnInfoDTO(1L, 0, BoardColumnKindEnum.INITIAL),
            new BoardColumnInfoDTO(2L, 1, BoardColumnKindEnum.PENDING),
            new BoardColumnInfoDTO(3L, 2, BoardColumnKindEnum.FINAL),
            new BoardColumnInfoDTO(4L, 3, BoardColumnKindEnum.CANCEL)
    );

    @Test
    void nextColumn_validTransition_returnsImmediateNextColumn() {
        assertEquals(2L, CardMovementRules.nextColumn(1L, columns).id());
    }

    @Test
    void nextColumn_finalColumn_throwsMovementInvalidException() {
        assertThrows(MovementInvalidException.class, () -> CardMovementRules.nextColumn(3L, columns));
    }

    @Test
    void cancellationColumn_finalCard_throwsMovementInvalidException() {
        assertThrows(MovementInvalidException.class,
                () -> CardMovementRules.cancellationColumn(3L, 4L, columns));
    }

    @Test
    void cancellationColumn_anyNonFinalCard_returnsCancellationColumn() {
        assertEquals(4L, CardMovementRules.cancellationColumn(1L, 4L, columns).id());
    }

    @Test
    void requireUnblocked_blockedCard_throwsCardBlockedException() {
        assertThrows(CardBlockedException.class, () -> CardMovementRules.requireUnblocked(true));
    }
}