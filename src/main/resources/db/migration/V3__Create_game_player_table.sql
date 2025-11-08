CREATE TABLE game_players (
    game_id BIGINT UNSIGNED NOT NULL,
    player_id BIGINT UNSIGNED NOT NULL,
    final_status ENUM('win', 'lose', 'tie', 'abandoned') NOT NULL,
    finish_time INT UNSIGNED NULL DEFAULT NULL COMMENT '秒存储',
    score INT NOT NULL DEFAULT '0',
    joined_at DATETIME NOT NULL,
    finished_at DATETIME NULL DEFAULT NULL COMMENT '可能早于本局时间',
    PRIMARY KEY (game_id, player_id),
    KEY fk_playerid_idx (player_id),
    KEY idx_finish_time (finish_time) VISIBLE,
    CONSTRAINT fk_gameid FOREIGN KEY (game_id) REFERENCES game (`game_id`) ON DELETE CASCADE,
    CONSTRAINT fk_playerid FOREIGN KEY (player_id) REFERENCES player (`PLAYER_ID`)
)ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb3;