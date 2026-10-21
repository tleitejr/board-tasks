package br.com.dio.domain;

import br.com.dio.exception.BoardInvalidException;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;

import java.util.Comparator;
import java.util.List;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.INITIAL;

public final class BoardRules {

    private BoardRules() {
    }

    public static void validate(final String boardName, final List<BoardColumnEntity> columns) {
        if (boardName == null || boardName.isBlank()) {
            throw new BoardInvalidException("O board precisa ter um nome");
        }
        if (columns == null || columns.size() < 3) {
            throw new BoardInvalidException("O board precisa ter pelo menos 3 colunas");
        }

        var orderedColumns = columns.stream()
                .sorted(Comparator.comparingInt(BoardColumnEntity::getOrder))
                .toList();
        if (orderedColumns.stream().anyMatch(column -> column.getName() == null || column.getName().isBlank())) {
            throw new BoardInvalidException("Todas as colunas precisam ter um nome");
        }
        for (var index = 0; index < orderedColumns.size(); index++) {
            if (orderedColumns.get(index).getOrder() != index) {
                throw new BoardInvalidException("A ordem das colunas deve ser contínua e começar em zero");
            }
        }

        var initialCount = countKind(orderedColumns, INITIAL);
        var finalCount = countKind(orderedColumns, FINAL);
        var cancelCount = countKind(orderedColumns, CANCEL);
        if (initialCount != 1 || finalCount != 1 || cancelCount != 1) {
            throw new BoardInvalidException("O board deve ter exatamente uma coluna inicial, final e de cancelamento");
        }
        var lastIndex = orderedColumns.size() - 1;
        if (orderedColumns.get(0).getKind() != INITIAL
                || orderedColumns.get(lastIndex - 1).getKind() != FINAL
                || orderedColumns.get(lastIndex).getKind() != CANCEL) {
            throw new BoardInvalidException("As colunas inicial, final e cancelamento devem ser a primeira, penúltima e última");
        }
        if (orderedColumns.subList(1, lastIndex - 1).stream()
                .anyMatch(column -> column.getKind() != BoardColumnKindEnum.PENDING)) {
            throw new BoardInvalidException("As colunas entre inicial e final devem ser do tipo pendente");
        }
    }

    private static long countKind(final List<BoardColumnEntity> columns, final BoardColumnKindEnum kind) {
        return columns.stream().filter(column -> column.getKind() == kind).count();
    }
}