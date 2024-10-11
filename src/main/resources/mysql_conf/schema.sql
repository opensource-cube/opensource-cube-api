create table `open_source`
(
    `id`         bigint                           NOT NULL AUTO_INCREMENT,
    `client_id`  varchar(36) COLLATE utf8mb4_bin  NOT NULL,
    `name`       varchar(255) COLLATE utf8mb4_bin NOT NULL,
    `origin_url` varchar(255) COLLATE utf8mb4_bin NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `open_source_unique_constraint` (`name`, `origin_url`),
    KEY `open_source_idx` (`client_id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET `utf8mb4`
  COLLATE `utf8mb4_bin`;

CREATE TABLE `open_source_version`
(
    `id`             bigint                           NOT NULL AUTO_INCREMENT,
    `open_source_id` bigint                           NOT NULL,
    `client_id`      varchar(36) COLLATE utf8mb4_bin  NOT NULL,
    `source_url`     varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `version`        varchar(255) COLLATE utf8mb4_bin NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `open_source_version_unique_constraint` (`open_source_id`, `version`),
    KEY `open_source_version_idx` (`client_id`),
    CONSTRAINT `fk_open_source_version` FOREIGN KEY (`open_source_id`) REFERENCES `open_source` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;

CREATE TABLE `license`
(
    `id`                     bigint       NOT NULL AUTO_INCREMENT,
    `open_source_version_id` bigint       NOT NULL,
    `client_id`              varchar(36)  NOT NULL,
    `path`                   varchar(255) NOT NULL,
    `type`                   varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `license_unique_constraint` (`open_source_version_id`, `type`),
    KEY `license_idx` (`client_id`),
    CONSTRAINT `fk_license` FOREIGN KEY (`open_source_version_id`) REFERENCES `open_source_version` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;
