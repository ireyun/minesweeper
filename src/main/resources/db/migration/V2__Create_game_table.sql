CREATE TABLE game (
    game_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    mode VARCHAR(20) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    `rows` TINYINT NOT NULL,
    cols TINYINT NOT NULL,
    status ENUM('finished') NOT NULL DEFAULT 'finished' COMMENT '对局状态，完成是finished，没有完成时NULL',
    mine_count SMALLINT NOT NULL,
    started_at DATETIME NOT NULL,
    ended_at DATETIME NOT NULL,
    is_tie TINYINT NOT NULL DEFAULT 0,
    winner BIGINT UNSIGNED NULL DEFAULT NULL,
    PRIMARY KEY (game_id),
    KEY idx_mode_diff (mode, difficulty),
    KEY idx_started_at (`started_at`),
    KEY idx_winner (`winner`),
    CONSTRAINT fk_winner FOREIGN KEY (winner) REFERENCES player (PLAYER_ID) ON DELETE SET NULL
)ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb3;