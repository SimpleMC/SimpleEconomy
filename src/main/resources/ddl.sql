CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) NOT NULL,
  `balance` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `accounts_id_uindex` (`id`),
  UNIQUE KEY `accounts_uuid_uindex` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1