CREATE DATABASE IF NOT EXISTS `user_base` ;

CREATE TABLE IF NOT EXISTS `player` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `image` varchar(45) DEFAULT NULL,
  `points` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `game` (
  `id` int NOT NULL AUTO_INCREMENT,
  `player1_id` int DEFAULT NULL,
  `player2_id` int DEFAULT NULL,
  `session_status` enum('inProgress','terminated','finished') DEFAULT NULL,
  `coordinates` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
