drop database if exists identifiers;

create database identifiers;

use identifiers;

DROP TABLE IF EXISTS `identifier_values`;
CREATE TABLE `identifier_values` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `data` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);
