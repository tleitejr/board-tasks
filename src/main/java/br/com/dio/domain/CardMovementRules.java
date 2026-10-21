package br.com.dio.domain;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.MovementInvalidException;

import java.util.List;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;

public final class CardMovementRules {

    private CardMovementRules() {
    }

    public static void requireUnblocked(final boolean blocked) {
        if (blocked) {
            throw new CardBlockedException("O card está bloqueado e não pode ser movido");
        }
    }

    public static BoardColumnInfoDTO nextColumn(final Long currentColumnId,
                                                final List<BoardColumnInfoDTO> columns) {
        var current = findColumn(currentColumnId, columns);
        if (current.kind() == FINAL || current.kind() == CANCEL) {
            throw new MovementInvalidException("Cards em colunas finais não podem avançar");
        }
        return columns.stream()
                .filter(column -> column.order() == current.order() + 1)
                .filter(column -> column.kind() != CANCEL)
                .findFirst()
                .orElseThrow(() -> new MovementInvalidException("Não existe próxima etapa válida para este card"));
    }

    public static BoardColumnInfoDTO cancellationColumn(final Long currentColumnId,
                                                         final Long cancellationColumnId,
                                                         final List<BoardColumnInfoDTO> columns) {
        var current = findColumn(currentColumnId, columns);
        var cancellation = findColumn(cancellationColumnId, columns);
        if (current.kind() == FINAL || current.kind() == CANCEL) {
            throw new MovementInvalidException("Cards finalizados ou cancelados não podem ser cancelados");
        }
        if (cancellation.kind() != CANCEL) {
            throw new MovementInvalidException("A coluna de destino não é uma coluna de cancelamento");
        }
        return cancellation;
    }

    private static BoardColumnInfoDTO findColumn(final Long columnId,
                                                  final List<BoardColumnInfoDTO> columns) {
        return columns.stream()
                .filter(column -> column.id().equals(columnId))
                .findFirst()
                .orElseThrow(() -> new MovementInvalidException("O card não pertence ao board selecionado"));
    }
}