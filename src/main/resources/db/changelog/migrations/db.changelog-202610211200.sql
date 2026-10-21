--liquibase formatted sql
--changeset copilot:202610211200
--comment: persist card creation and column transition history

ALTER TABLE CARDS
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE CARD_COLUMN_HISTORY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT NOT NULL,
    board_column_id BIGINT NOT NULL,
    entered_at TIMESTAMP NOT NULL,
    exited_at TIMESTAMP NULL,
    CONSTRAINT card_history__cards_fk FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE,
    CONSTRAINT card_history__columns_fk FOREIGN KEY (board_column_id) REFERENCES BOARDS_COLUMNS(id) ON DELETE CASCADE,
    INDEX card_column_history_card_idx (card_id),
    INDEX card_column_history_column_idx (board_column_id)
) ENGINE=InnoDB;

INSERT INTO CARD_COLUMN_HISTORY (card_id, board_column_id, entered_at)
SELECT id, board_column_id, created_at FROM CARDS;

--rollback DROP TABLE CARD_COLUMN_HISTORY;
--rollback ALTER TABLE CARDS DROP COLUMN created_at;