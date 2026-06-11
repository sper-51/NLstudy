-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: exam_platform
-- ------------------------------------------------------
-- Server version	8.0.46-0ubuntu0.24.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `answer_record`
--

DROP TABLE IF EXISTS `answer_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `answer_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_record_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `exam_paper_question_id` bigint DEFAULT NULL,
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_correct` tinyint DEFAULT NULL,
  `score` decimal(6,2) DEFAULT NULL,
  `is_marked` tinyint NOT NULL DEFAULT '0',
  `answer_order` json DEFAULT NULL,
  `first_answer_time` datetime DEFAULT NULL,
  `last_answer_time` datetime DEFAULT NULL,
  `answer_times` int NOT NULL DEFAULT '0',
  `time_spent` int DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answer_record`
--

LOCK TABLES `answer_record` WRITE;
/*!40000 ALTER TABLE `answer_record` DISABLE KEYS */;
INSERT INTO `answer_record` VALUES (1,1,18,NULL,'D',1,5.00,0,NULL,NULL,NULL,1,30,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(2,1,19,NULL,'A,B,C',0,3.00,0,NULL,NULL,NULL,2,90,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(3,1,20,NULL,'true',1,2.00,0,NULL,NULL,NULL,1,15,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(4,1,21,NULL,'5,1-126',1,5.00,0,NULL,NULL,NULL,1,60,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(5,1,11,NULL,'答案内容存在编码异常，请联系教师复核',NULL,5.00,0,NULL,NULL,NULL,1,1800,'2026-06-04 17:47:03','2026-06-08 20:28:18'),(6,2,18,NULL,'B',0,0.00,0,NULL,NULL,NULL,1,20,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(7,2,19,NULL,'A,B',0,2.00,0,NULL,NULL,NULL,1,60,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(8,2,20,NULL,'false',1,2.00,0,NULL,NULL,NULL,1,10,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(9,2,21,NULL,'5,1-127',0,0.00,0,NULL,NULL,NULL,1,30,'2026-06-04 17:47:03','2026-06-04 17:47:03'),(10,2,11,NULL,'答案内容存在编码异常，请联系教师复核',NULL,5.00,0,NULL,NULL,NULL,2,1500,'2026-06-04 17:47:03','2026-06-08 20:28:18'),(11,171,51,111,'B',0,0.00,1,NULL,'2026-06-06 18:03:00','2026-06-06 18:11:00',1,90,'2026-06-06 18:00:00','2026-06-06 18:12:00'),(12,172,51,111,'A',1,5.00,1,NULL,'2026-06-06 18:06:00','2026-06-06 18:15:00',1,105,'2026-06-06 18:03:00','2026-06-06 18:16:00'),(13,173,51,111,'B',0,0.00,1,NULL,'2026-06-06 18:09:00','2026-06-06 18:19:00',1,120,'2026-06-06 18:06:00','2026-06-06 18:20:00'),(14,174,51,111,'A',1,5.00,1,NULL,'2026-06-06 18:12:00','2026-06-06 18:23:00',1,135,'2026-06-06 18:09:00','2026-06-06 18:24:00'),(15,175,51,111,'B',0,0.00,1,NULL,'2026-06-06 18:15:00','2026-06-06 18:27:00',1,150,'2026-06-06 18:12:00','2026-06-06 18:28:00'),(16,176,51,111,'B',0,0.00,1,NULL,'2026-06-06 18:18:00','2026-06-06 18:31:00',1,165,'2026-06-06 18:15:00','2026-06-06 18:32:00'),(17,177,51,111,'A',1,5.00,1,NULL,'2026-06-06 18:21:00','2026-06-06 18:35:00',1,180,'2026-06-06 18:18:00','2026-06-06 18:36:00'),(18,214,43,112,'A',1,5.00,1,NULL,'2026-06-05 20:03:00','2026-06-05 20:05:00',1,120,'2026-06-05 20:00:00','2026-06-08 20:28:18'),(19,214,44,113,'B',1,5.00,1,NULL,'2026-06-05 20:08:00','2026-06-05 20:13:00',1,180,'2026-06-05 20:00:00','2026-06-08 20:28:18'),(20,214,45,114,'A',1,5.00,1,NULL,'2026-06-05 20:13:00','2026-06-05 20:21:00',1,240,'2026-06-05 20:00:00','2026-06-08 20:28:18'),(21,214,46,115,'true',1,10.00,1,NULL,'2026-06-05 20:18:00','2026-06-05 20:29:00',1,300,'2026-06-05 20:00:00','2026-06-05 21:08:00'),(22,214,47,116,'age',1,9.00,1,NULL,'2026-06-05 20:23:00','2026-06-05 20:37:00',1,360,'2026-06-05 20:00:00','2026-06-05 21:08:00'),(23,214,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,17.00,1,NULL,'2026-06-05 20:28:00','2026-06-05 20:45:00',1,420,'2026-06-05 20:00:00','2026-06-07 00:52:31'),(24,215,43,112,'A',1,5.00,1,NULL,'2026-06-05 20:05:00','2026-06-05 20:07:00',1,125,'2026-06-05 20:02:00','2026-06-08 20:28:18'),(25,215,44,113,'B',1,5.00,1,NULL,'2026-06-05 20:10:00','2026-06-05 20:15:00',1,185,'2026-06-05 20:02:00','2026-06-08 20:28:18'),(26,215,45,114,'A',1,5.00,1,NULL,'2026-06-05 20:15:00','2026-06-05 20:23:00',1,245,'2026-06-05 20:02:00','2026-06-08 20:28:18'),(27,215,46,115,'true',1,9.00,1,NULL,'2026-06-05 20:20:00','2026-06-05 20:31:00',1,305,'2026-06-05 20:02:00','2026-06-05 21:11:00'),(28,215,47,116,'age',1,8.00,1,NULL,'2026-06-05 20:25:00','2026-06-05 20:39:00',1,365,'2026-06-05 20:02:00','2026-06-05 21:11:00'),(29,215,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,19.00,1,NULL,'2026-06-05 20:30:00','2026-06-05 20:47:00',1,425,'2026-06-05 20:02:00','2026-06-07 00:52:31'),(30,216,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:07:00','2026-06-05 20:09:00',1,130,'2026-06-05 20:04:00','2026-06-08 20:28:18'),(31,216,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:12:00','2026-06-05 20:17:00',1,190,'2026-06-05 20:04:00','2026-06-08 20:28:18'),(32,216,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:17:00','2026-06-05 20:25:00',1,250,'2026-06-05 20:04:00','2026-06-08 20:28:18'),(33,216,46,115,'true',1,8.00,1,NULL,'2026-06-05 20:22:00','2026-06-05 20:33:00',1,310,'2026-06-05 20:04:00','2026-06-05 21:14:00'),(34,216,47,116,'num',0,7.00,1,NULL,'2026-06-05 20:27:00','2026-06-05 20:41:00',1,370,'2026-06-05 20:04:00','2026-06-05 21:14:00'),(35,216,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,19.00,1,NULL,'2026-06-05 20:32:00','2026-06-05 20:49:00',1,430,'2026-06-05 20:04:00','2026-06-07 00:52:31'),(36,217,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:09:00','2026-06-05 20:11:00',1,135,'2026-06-05 20:06:00','2026-06-08 20:28:18'),(37,217,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:14:00','2026-06-05 20:19:00',1,195,'2026-06-05 20:06:00','2026-06-08 20:28:18'),(38,217,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:19:00','2026-06-05 20:27:00',1,255,'2026-06-05 20:06:00','2026-06-08 20:28:18'),(39,217,46,115,'false',0,6.00,1,NULL,'2026-06-05 20:24:00','2026-06-05 20:35:00',1,315,'2026-06-05 20:06:00','2026-06-05 21:17:00'),(40,217,47,116,'num',0,6.00,1,NULL,'2026-06-05 20:29:00','2026-06-05 20:43:00',1,375,'2026-06-05 20:06:00','2026-06-05 21:17:00'),(41,217,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,16.00,1,NULL,'2026-06-05 20:34:00','2026-06-05 20:51:00',1,435,'2026-06-05 20:06:00','2026-06-07 00:52:31'),(42,218,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:11:00','2026-06-05 20:13:00',1,140,'2026-06-05 20:08:00','2026-06-08 20:28:18'),(43,218,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:16:00','2026-06-05 20:21:00',1,200,'2026-06-05 20:08:00','2026-06-08 20:28:18'),(44,218,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:21:00','2026-06-05 20:29:00',1,260,'2026-06-05 20:08:00','2026-06-08 20:28:18'),(45,218,46,115,'false',0,6.00,1,NULL,'2026-06-05 20:26:00','2026-06-05 20:37:00',1,320,'2026-06-05 20:08:00','2026-06-05 21:20:00'),(46,218,47,116,'num',0,5.00,1,NULL,'2026-06-05 20:31:00','2026-06-05 20:45:00',1,380,'2026-06-05 20:08:00','2026-06-05 21:20:00'),(47,218,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,15.00,1,NULL,'2026-06-05 20:36:00','2026-06-05 20:53:00',1,440,'2026-06-05 20:08:00','2026-06-07 00:52:31'),(48,219,43,112,'A',1,5.00,1,NULL,'2026-06-05 20:13:00','2026-06-05 20:15:00',1,145,'2026-06-05 20:10:00','2026-06-08 20:28:18'),(49,219,44,113,'B',1,5.00,1,NULL,'2026-06-05 20:18:00','2026-06-05 20:23:00',1,205,'2026-06-05 20:10:00','2026-06-08 20:28:18'),(50,219,45,114,'A',1,5.00,1,NULL,'2026-06-05 20:23:00','2026-06-05 20:31:00',1,265,'2026-06-05 20:10:00','2026-06-08 20:28:18'),(51,219,46,115,'true',1,9.00,1,NULL,'2026-06-05 20:28:00','2026-06-05 20:39:00',1,325,'2026-06-05 20:10:00','2026-06-05 21:23:00'),(52,219,47,116,'age',1,8.00,1,NULL,'2026-06-05 20:33:00','2026-06-05 20:47:00',1,385,'2026-06-05 20:10:00','2026-06-05 21:23:00'),(53,219,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,16.00,1,NULL,'2026-06-05 20:38:00','2026-06-05 20:55:00',1,445,'2026-06-05 20:10:00','2026-06-07 00:52:31'),(54,220,43,112,'A',1,5.00,1,NULL,'2026-06-05 20:15:00','2026-06-05 20:17:00',1,150,'2026-06-05 20:12:00','2026-06-08 20:28:18'),(55,220,44,113,'B',1,5.00,1,NULL,'2026-06-05 20:20:00','2026-06-05 20:25:00',1,210,'2026-06-05 20:12:00','2026-06-08 20:28:18'),(56,220,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:25:00','2026-06-05 20:33:00',1,270,'2026-06-05 20:12:00','2026-06-08 20:28:18'),(57,220,46,115,'true',1,8.00,1,NULL,'2026-06-05 20:30:00','2026-06-05 20:41:00',1,330,'2026-06-05 20:12:00','2026-06-05 21:26:00'),(58,220,47,116,'num',0,7.00,1,NULL,'2026-06-05 20:35:00','2026-06-05 20:49:00',1,390,'2026-06-05 20:12:00','2026-06-05 21:26:00'),(59,220,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,19.00,1,NULL,'2026-06-05 20:40:00','2026-06-05 20:57:00',1,450,'2026-06-05 20:12:00','2026-06-07 00:52:31'),(60,221,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:17:00','2026-06-05 20:19:00',1,155,'2026-06-05 20:14:00','2026-06-08 20:28:18'),(61,221,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:22:00','2026-06-05 20:27:00',1,215,'2026-06-05 20:14:00','2026-06-08 20:28:18'),(62,221,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:27:00','2026-06-05 20:35:00',1,275,'2026-06-05 20:14:00','2026-06-08 20:28:18'),(63,221,46,115,'false',0,7.00,1,NULL,'2026-06-05 20:32:00','2026-06-05 20:43:00',1,335,'2026-06-05 20:14:00','2026-06-05 21:29:00'),(64,221,47,116,'num',0,6.00,1,NULL,'2026-06-05 20:37:00','2026-06-05 20:51:00',1,395,'2026-06-05 20:14:00','2026-06-05 21:29:00'),(65,221,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,19.00,1,NULL,'2026-06-05 20:42:00','2026-06-05 20:59:00',1,455,'2026-06-05 20:14:00','2026-06-07 00:52:31'),(66,222,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:19:00','2026-06-05 20:21:00',1,160,'2026-06-05 20:16:00','2026-06-08 20:28:18'),(67,222,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:24:00','2026-06-05 20:29:00',1,220,'2026-06-05 20:16:00','2026-06-08 20:28:18'),(68,222,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:29:00','2026-06-05 20:37:00',1,280,'2026-06-05 20:16:00','2026-06-08 20:28:18'),(69,222,46,115,'false',0,7.00,1,NULL,'2026-06-05 20:34:00','2026-06-05 20:45:00',1,340,'2026-06-05 20:16:00','2026-06-05 21:32:00'),(70,222,47,116,'num',0,6.00,1,NULL,'2026-06-05 20:39:00','2026-06-05 20:53:00',1,400,'2026-06-05 20:16:00','2026-06-05 21:32:00'),(71,222,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,16.00,1,NULL,'2026-06-05 20:44:00','2026-06-05 21:01:00',1,460,'2026-06-05 20:16:00','2026-06-07 00:52:31'),(72,223,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:21:00','2026-06-05 20:23:00',1,165,'2026-06-05 20:18:00','2026-06-08 20:28:18'),(73,223,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:26:00','2026-06-05 20:31:00',1,225,'2026-06-05 20:18:00','2026-06-08 20:28:18'),(74,223,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:31:00','2026-06-05 20:39:00',1,285,'2026-06-05 20:18:00','2026-06-08 20:28:18'),(75,223,46,115,'false',0,5.00,1,NULL,'2026-06-05 20:36:00','2026-06-05 20:47:00',1,345,'2026-06-05 20:18:00','2026-06-05 21:35:00'),(76,223,47,116,'num',0,5.00,1,NULL,'2026-06-05 20:41:00','2026-06-05 20:55:00',1,405,'2026-06-05 20:18:00','2026-06-05 21:35:00'),(77,223,48,117,'两者底层结构不同，适用场景也不同。',0,14.00,1,NULL,'2026-06-05 20:46:00','2026-06-05 21:03:00',1,465,'2026-06-05 20:18:00','2026-06-07 00:52:31'),(78,224,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:23:00','2026-06-05 20:25:00',1,170,'2026-06-05 20:20:00','2026-06-08 20:28:18'),(79,224,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:28:00','2026-06-05 20:33:00',1,230,'2026-06-05 20:20:00','2026-06-08 20:28:18'),(80,224,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:33:00','2026-06-05 20:41:00',1,290,'2026-06-05 20:20:00','2026-06-08 20:28:18'),(81,224,46,115,'true',1,8.00,1,NULL,'2026-06-05 20:38:00','2026-06-05 20:49:00',1,350,'2026-06-05 20:20:00','2026-06-05 21:38:00'),(82,224,47,116,'num',0,7.00,1,NULL,'2026-06-05 20:43:00','2026-06-05 20:57:00',1,410,'2026-06-05 20:20:00','2026-06-05 21:38:00'),(83,224,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',1,20.00,1,NULL,'2026-06-05 20:48:00','2026-06-05 21:05:00',1,470,'2026-06-05 20:20:00','2026-06-07 00:52:31'),(84,225,43,112,'B',0,5.00,1,NULL,'2026-06-05 20:25:00','2026-06-05 20:27:00',1,175,'2026-06-05 20:22:00','2026-06-08 20:28:18'),(85,225,44,113,'C',0,5.00,1,NULL,'2026-06-05 20:30:00','2026-06-05 20:35:00',1,235,'2026-06-05 20:22:00','2026-06-08 20:28:18'),(86,225,45,114,'B',0,5.00,1,NULL,'2026-06-05 20:35:00','2026-06-05 20:43:00',1,295,'2026-06-05 20:22:00','2026-06-08 20:28:18'),(87,225,46,115,'false',0,6.00,1,NULL,'2026-06-05 20:40:00','2026-06-05 20:51:00',1,355,'2026-06-05 20:22:00','2026-06-05 21:41:00'),(88,225,47,116,'num',0,5.00,1,NULL,'2026-06-05 20:45:00','2026-06-05 20:59:00',1,415,'2026-06-05 20:22:00','2026-06-05 21:41:00'),(89,225,48,117,'ArrayList适合随机访问和尾部追加；LinkedList适合频繁插入和删除。',0,15.00,1,NULL,'2026-06-05 20:50:00','2026-06-05 21:07:00',1,475,'2026-06-05 20:22:00','2026-06-07 00:52:31'),(90,227,49,NULL,'A',0,0.00,0,NULL,'2026-06-07 17:31:01','2026-06-07 17:31:01',1,NULL,'2026-06-07 17:31:00','2026-06-07 17:31:00'),(91,233,46,NULL,'true',1,5.00,0,NULL,'2026-06-08 09:51:18','2026-06-08 09:51:18',1,NULL,'2026-06-08 09:51:18','2026-06-08 09:51:18'),(92,233,47,NULL,'age',1,5.00,0,NULL,'2026-06-08 09:51:18','2026-06-08 09:51:18',1,NULL,'2026-06-08 09:51:18','2026-06-08 09:51:18'),(93,233,48,NULL,'ArrayList 查询快，LinkedList 插入删除更灵活',1,18.00,0,NULL,'2026-06-08 09:51:18','2026-06-08 09:51:30',1,NULL,'2026-06-08 09:51:18','2026-06-08 21:55:17'),(94,226,49,NULL,'A',0,0.00,0,NULL,'2026-06-08 11:24:09','2026-06-08 11:24:09',1,NULL,'2026-06-08 11:24:09','2026-06-08 11:24:09'),(98,235,40,NULL,'A',1,5.00,1,NULL,'2026-06-01 09:10:00','2026-06-01 09:30:00',1,NULL,'2026-06-08 21:41:41','2026-06-08 21:41:41'),(99,235,41,NULL,'A',1,5.00,1,NULL,'2026-06-01 09:10:00','2026-06-01 09:30:00',1,NULL,'2026-06-08 21:41:41','2026-06-08 21:41:41'),(100,235,42,NULL,'B',0,2.00,1,NULL,'2026-06-01 09:10:00','2026-06-01 09:30:00',1,NULL,'2026-06-08 21:41:41','2026-06-08 21:41:41'),(101,236,107,NULL,'A',0,0.00,0,NULL,'2026-06-09 15:45:43','2026-06-09 15:45:43',1,NULL,'2026-06-09 15:45:43','2026-06-09 15:45:43'),(102,236,108,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:44',1,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:43'),(103,236,109,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:44',1,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:43'),(104,236,110,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:44',1,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:43'),(105,236,111,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:44',1,NULL,'2026-06-09 15:45:44','2026-06-09 15:45:43'),(106,237,107,NULL,'A',0,0.00,0,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50',2,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50'),(107,237,108,NULL,'A',0,0.00,0,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50',2,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50'),(108,237,109,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50',2,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50'),(109,237,110,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50',2,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50'),(110,237,111,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50',2,NULL,'2026-06-09 15:52:39','2026-06-09 15:52:50'),(111,238,107,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:47',1,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:46'),(112,238,108,NULL,'B',1,5.00,0,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:47',1,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:46'),(113,238,109,NULL,'C',0,0.00,0,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:47',1,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:46'),(114,238,110,NULL,'C',0,0.00,0,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:47',1,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:46'),(115,238,111,NULL,'C',0,0.00,0,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:47',1,NULL,'2026-06-09 15:54:47','2026-06-09 15:54:46');
/*!40000 ALTER TABLE `answer_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `answer_snapshot`
--

DROP TABLE IF EXISTS `answer_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `answer_snapshot` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_record_id` bigint NOT NULL,
  `snapshot_data` json NOT NULL,
  `save_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'auto',
  `network_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answer_snapshot`
--

LOCK TABLES `answer_snapshot` WRITE;
/*!40000 ALTER TABLE `answer_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `answer_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `backup_cloud_cleanup_20260609_course`
--

DROP TABLE IF EXISTS `backup_cloud_cleanup_20260609_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_cloud_cleanup_20260609_course` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `teacher_id` bigint NOT NULL,
  `semester_id` bigint DEFAULT NULL,
  `credits` decimal(3,1) DEFAULT NULL,
  `hours` int DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` tinyint NOT NULL DEFAULT '1',
  `is_shared` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backup_cloud_cleanup_20260609_course`
--

LOCK TABLES `backup_cloud_cleanup_20260609_course` WRITE;
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_course` DISABLE KEYS */;
INSERT INTO `backup_cloud_cleanup_20260609_course` VALUES (14,'ddd','CRSMMN7NV',208,NULL,3.0,64,'\n\nl',1,0,'2026-06-05 14:56:11','2026-06-05 14:56:11',0),(15,'ddd','8FE21D07',103,NULL,3.0,64,'1111',1,0,'2026-06-05 16:35:50','2026-06-05 16:35:50',0),(19,'fff','CRSGFLV6A',103,NULL,3.0,64,'fff',1,0,'2026-06-09 15:44:09','2026-06-09 15:44:09',0),(20,'ggg','CRSNHKJ5U',103,NULL,3.0,64,'',1,0,'2026-06-09 15:53:34','2026-06-09 15:53:34',0);
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `backup_cloud_cleanup_20260609_exam`
--

DROP TABLE IF EXISTS `backup_cloud_cleanup_20260609_exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_cloud_cleanup_20260609_exam` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `exam_paper_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `semester_id` bigint DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `duration` int NOT NULL,
  `total_score` decimal(6,2) NOT NULL,
  `pass_score` decimal(6,2) DEFAULT NULL,
  `allow_times` int NOT NULL DEFAULT '1',
  `is_random_order` tinyint NOT NULL DEFAULT '1',
  `is_random_options` tinyint NOT NULL DEFAULT '1',
  `exam_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'formal',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `student_count` int NOT NULL DEFAULT '0',
  `submit_count` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backup_cloud_cleanup_20260609_exam`
--

LOCK TABLES `backup_cloud_cleanup_20260609_exam` WRITE;
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_exam` DISABLE KEYS */;
INSERT INTO `backup_cloud_cleanup_20260609_exam` VALUES (7,'111',17,15,103,NULL,'2026-06-05 00:00:00','2026-06-30 00:00:00',120,100.00,60.00,1,1,1,'formal','published',0,0,'2026-06-05 16:57:04','2026-06-05 16:57:04',0),(16,'111',17,15,103,NULL,'2026-06-01 00:00:00','2026-06-08 11:40:05',120,100.00,60.00,1,1,1,'formal','finished',0,0,'2026-06-07 17:22:12','2026-06-07 17:22:12',0),(17,'222',19,15,103,NULL,'2026-06-07 00:00:00','2026-06-08 09:53:12',120,100.00,60.00,1,1,1,'formal','finished',0,0,'2026-06-08 08:43:35','2026-06-08 08:43:35',0),(20,'fff',21,19,103,NULL,'2026-06-08 00:00:00','2026-06-10 00:00:00',120,100.00,60.00,1,1,1,'formal','cancelled',0,0,'2026-06-09 15:45:12','2026-06-09 15:51:42',0),(21,'ggg',21,19,103,NULL,'2026-06-08 00:00:00','2026-06-10 00:00:00',120,100.00,60.00,1,1,1,'formal','published',0,0,'2026-06-09 15:52:03','2026-06-09 15:52:03',0),(19,'codex_accept_Web前端开发-展示测验',23,11,103,NULL,'2026-06-01 09:00:00','2026-06-01 09:45:00',45,15.00,9.00,1,0,0,'formal','ended',1,1,'2026-06-08 21:41:41','2026-06-08 21:41:41',0),(22,'gggg',21,20,103,NULL,'2026-06-08 00:00:00','2026-06-10 00:00:00',120,100.00,60.00,1,1,1,'formal','published',0,0,'2026-06-09 15:54:25','2026-06-09 15:54:25',0);
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `backup_cloud_cleanup_20260609_exam_paper`
--

DROP TABLE IF EXISTS `backup_cloud_cleanup_20260609_exam_paper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_cloud_cleanup_20260609_exam_paper` (
  `id` bigint NOT NULL DEFAULT '0',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `course_id` bigint DEFAULT NULL,
  `teacher_id` bigint NOT NULL,
  `total_score` decimal(6,2) NOT NULL DEFAULT '100.00',
  `pass_score` decimal(6,2) DEFAULT NULL,
  `duration` int NOT NULL DEFAULT '120',
  `question_count` int NOT NULL DEFAULT '0',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'manual',
  `rule_config` json DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backup_cloud_cleanup_20260609_exam_paper`
--

LOCK TABLES `backup_cloud_cleanup_20260609_exam_paper` WRITE;
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_exam_paper` DISABLE KEYS */;
INSERT INTO `backup_cloud_cleanup_20260609_exam_paper` VALUES (17,'111',15,103,2.00,60.00,120,1,'manual',NULL,NULL,1,'2026-06-05 16:36:26','2026-06-05 16:36:26',0),(19,'222',15,103,2.00,60.00,120,1,'manual',NULL,NULL,1,'2026-06-08 08:42:56','2026-06-08 08:42:56',0),(20,'333',15,103,30.00,60.00,120,3,'manual',NULL,NULL,1,'2026-06-08 09:02:23','2026-06-08 09:02:23',0),(21,'123',15,103,25.00,60.00,120,0,'manual',NULL,NULL,0,'2026-06-08 15:19:08','2026-06-08 15:19:08',0),(15,'发发发',11,103,5.00,60.00,120,1,'manual',NULL,NULL,1,'2026-06-05 15:31:06','2026-06-05 15:31:06',0),(23,'codex_accept_Web前端开发-展示试卷',11,103,15.00,9.00,45,3,'manual',NULL,'验收展示用试卷，可按 codex_accept 标记清理',1,'2026-06-08 21:41:41','2026-06-08 21:41:41',0),(16,'242',14,208,2.00,60.00,120,1,'manual',NULL,NULL,1,'2026-06-05 15:52:29','2026-06-05 15:52:29',0);
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_exam_paper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `backup_cloud_cleanup_20260609_exam_record`
--

DROP TABLE IF EXISTS `backup_cloud_cleanup_20260609_exam_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_cloud_cleanup_20260609_exam_record` (
  `id` bigint NOT NULL DEFAULT '0',
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `exam_student_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `submit_time` datetime DEFAULT NULL,
  `total_score` decimal(6,2) DEFAULT NULL,
  `objective_score` decimal(6,2) DEFAULT NULL,
  `subjective_score` decimal(6,2) DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ongoing',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'exam',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `extra_data` json DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `answered_count` int DEFAULT '0',
  `total_questions` int DEFAULT '0',
  `switch_screen_count` int DEFAULT '0',
  `network_interrupt_count` int DEFAULT '0',
  `duration_seconds` bigint DEFAULT '0',
  `deleted` tinyint NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backup_cloud_cleanup_20260609_exam_record`
--

LOCK TABLES `backup_cloud_cleanup_20260609_exam_record` WRITE;
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_exam_record` DISABLE KEYS */;
INSERT INTO `backup_cloud_cleanup_20260609_exam_record` VALUES (227,16,103,103,'2026-06-07 17:28:41','2026-06-07 17:31:01',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-07 17:28:41','2026-06-07 17:28:41',1,0,0,0,139,0),(228,17,103,103,'2026-06-08 08:43:40','2026-06-08 08:43:42',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 08:43:40','2026-06-08 08:43:40',0,0,0,0,2,0),(229,7,103,103,'2026-06-08 09:07:25','2026-06-08 09:07:28',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:07:25','2026-06-08 09:07:25',1,0,0,0,2,0),(230,8,103,103,'2026-06-08 09:07:31','2026-06-08 09:07:34',5.00,5.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:07:31','2026-06-08 09:07:31',1,0,0,0,2,0),(231,7,103,103,'2026-06-08 09:07:35','2026-06-08 09:36:32',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:07:35','2026-06-08 09:07:35',1,0,0,0,1737,0),(236,20,103,103,'2026-06-09 15:45:19','2026-06-09 15:45:44',20.00,20.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-09 15:45:19','2026-06-09 15:45:19',5,5,0,0,24,0);
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_exam_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `backup_cloud_cleanup_20260609_grade`
--

DROP TABLE IF EXISTS `backup_cloud_cleanup_20260609_grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_cloud_cleanup_20260609_grade` (
  `id` bigint NOT NULL DEFAULT '0',
  `exam_record_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `total_score` decimal(6,2) NOT NULL,
  `objective_score` decimal(6,2) DEFAULT NULL,
  `subjective_score` decimal(6,2) DEFAULT NULL,
  `rank` int DEFAULT NULL,
  `percentile_rank` decimal(5,2) DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'published',
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backup_cloud_cleanup_20260609_grade`
--

LOCK TABLES `backup_cloud_cleanup_20260609_grade` WRITE;
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_grade` DISABLE KEYS */;
INSERT INTO `backup_cloud_cleanup_20260609_grade` VALUES (109,227,16,103,0.00,0.00,0.00,NULL,NULL,'published','2026-06-08 11:40:05','2026-06-07 17:31:00','2026-06-08 11:40:05',0),(111,228,17,103,0.00,0.00,0.00,NULL,NULL,'published','2026-06-08 09:53:12','2026-06-08 09:53:12','2026-06-08 09:53:12',0),(117,236,20,103,20.00,20.00,0.00,NULL,NULL,'published','2026-06-09 15:45:44','2026-06-09 15:45:43','2026-06-09 15:45:43',0);
/*!40000 ALTER TABLE `backup_cloud_cleanup_20260609_grade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `teacher_id` bigint NOT NULL,
  `semester_id` bigint DEFAULT NULL,
  `credits` decimal(3,1) DEFAULT NULL,
  `hours` int DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` tinyint NOT NULL DEFAULT '1',
  `is_shared` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_semester_id` (`semester_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'计算机网络',NULL,101,1,NULL,NULL,'计算机网络原理课程',1,0,'2026-06-03 17:08:07','2026-06-03 17:08:07',0),(2,'Java程序设计',NULL,101,1,NULL,NULL,'Java语言程序设计',1,0,'2026-06-03 17:08:07','2026-06-03 17:08:07',0),(3,'数据结构与算法',NULL,102,1,NULL,NULL,'数据结构与算法分析',1,0,'2026-06-03 17:08:07','2026-06-03 17:08:07',0),(4,'课程 4',NULL,101,NULL,NULL,NULL,'课程信息存在编码异常，已临时修复为可读占位。',1,0,'2026-06-04 15:45:02','2026-06-08 20:28:18',0),(5,'课程 5',NULL,101,NULL,NULL,NULL,'课程信息存在编码异常，已临时修复为可读占位。',1,0,'2026-06-04 15:51:17','2026-06-08 20:28:18',0),(6,'课程 6',NULL,101,NULL,NULL,NULL,'课程信息存在编码异常，已临时修复为可读占位。',1,0,'2026-06-04 15:54:35','2026-06-08 20:28:18',0),(7,'课程 7',NULL,101,NULL,NULL,NULL,'课程信息存在编码异常，已临时修复为可读占位。',1,0,'2026-06-04 15:58:04','2026-06-08 20:28:18',0),(8,'课程 8',NULL,101,NULL,NULL,NULL,'课程信息存在编码异常，已临时修复为可读占位。',1,0,'2026-06-04 16:00:52','2026-06-08 20:28:18',0),(9,'课程 9',NULL,101,NULL,NULL,NULL,'课程信息存在编码异常，已临时修复为可读占位。',1,0,'2026-06-04 16:06:52','2026-06-08 20:28:18',0),(10,'课程 10',NULL,101,NULL,NULL,NULL,'课程信息存在编码异常，已临时修复为可读占位。',1,0,'2026-06-04 16:14:14','2026-06-08 20:28:18',0),(11,'Web前端开发技术','419AB2AE',103,NULL,3.0,64,'Web前端开发课程，涵盖HTML/CSS/JavaScript/Vue框架等核心技术',1,0,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(12,'Java程序设计','JAVA2024001',103,NULL,4.0,56,'Java语言基础、面向对象编程、集合框架、IO与多线程网络编程',1,0,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(13,'Python数据分析','PYTH2024001',103,NULL,2.0,32,'Python基础语法、NumPy/Pandas数据处理与Matplotlib数据可视化',1,0,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(14,'网络设备配置实训','CRSMMN7NV',208,NULL,3.0,64,'用于联调展示的网络设备配置与交换机基础课程',1,0,'2026-06-05 14:56:11','2026-06-09 16:23:22',0),(15,'网络工程综合实训','8FE21D07',103,NULL,3.0,64,'用于联调展示的网络工程综合实训课程',1,0,'2026-06-05 16:35:50','2026-06-09 16:23:22',0),(17,'计算机网络原理','DDD5BEFD',103,NULL,3.0,48,'用于联调展示的原创计算机网络题库课程',1,0,'2026-06-08 09:58:04','2026-06-08 10:00:05',0),(18,'高等数学','CRSS9PV7J',207,NULL,3.0,32,'高等的数学',1,0,'2026-06-09 08:37:49','2026-06-09 08:37:49',0),(19,'数据库系统实践','CRSGFLV6A',103,NULL,3.0,64,'用于联调展示的数据库系统实践课程',1,0,'2026-06-09 15:44:09','2026-06-09 16:23:22',0),(20,'软件测试基础','CRSNHKJ5U',103,NULL,3.0,64,'用于联调展示的软件测试基础课程',1,0,'2026-06-09 15:53:34','2026-06-09 16:23:22',0);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_code`
--

DROP TABLE IF EXISTS `course_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_code` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `expire_time` datetime DEFAULT NULL,
  `max_uses` int DEFAULT NULL,
  `used_count` int NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_code`
--

LOCK TABLES `course_code` WRITE;
/*!40000 ALTER TABLE `course_code` DISABLE KEYS */;
INSERT INTO `course_code` VALUES (1,1,'CS2024NET',NULL,NULL,6,1,'2026-06-03 17:08:07'),(2,2,'CS2024JAVA',NULL,NULL,6,1,'2026-06-03 17:08:07'),(3,3,'CS2024DSA',NULL,NULL,0,1,'2026-06-03 17:08:07');
/*!40000 ALTER TABLE `course_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_selection`
--

DROP TABLE IF EXISTS `course_selection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_selection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `select_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_student_course` (`student_id`,`course_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_selection`
--

LOCK TABLES `course_selection` WRITE;
/*!40000 ALTER TABLE `course_selection` DISABLE KEYS */;
INSERT INTO `course_selection` VALUES (1,201,1,'2025-03-01 09:00:00',1,'2026-06-03 17:08:07'),(2,202,1,'2025-03-01 09:05:00',1,'2026-06-03 17:08:07'),(3,203,1,'2025-03-01 09:10:00',1,'2026-06-03 17:08:07'),(4,204,1,'2025-03-01 09:15:00',1,'2026-06-03 17:08:07'),(5,205,1,'2025-03-01 09:20:00',1,'2026-06-03 17:08:07'),(6,206,1,'2025-03-01 09:25:00',1,'2026-06-03 17:08:07'),(7,201,2,'2025-03-02 10:00:00',1,'2026-06-03 17:08:07'),(8,202,2,'2025-03-02 10:05:00',1,'2026-06-03 17:08:07'),(9,203,2,'2025-03-02 10:10:00',1,'2026-06-03 17:08:07'),(10,204,2,'2025-03-02 10:15:00',1,'2026-06-03 17:08:07'),(11,205,2,'2025-03-02 10:20:00',1,'2026-06-03 17:08:07'),(12,206,2,'2025-03-02 10:25:00',1,'2026-06-03 17:08:07'),(52,306,12,'2026-01-15 10:00:00',1,'2026-06-05 10:28:42'),(53,306,13,'2026-01-15 10:00:00',1,'2026-06-05 10:28:42'),(55,307,12,'2026-01-15 10:00:00',1,'2026-06-05 10:28:42'),(56,308,11,'2026-01-15 10:00:00',1,'2026-06-05 10:28:42'),(57,308,13,'2026-01-15 10:00:00',1,'2026-06-05 10:28:42'),(59,400,11,'2024-03-01 10:00:00',1,'2026-06-05 15:20:03'),(60,401,11,'2024-03-01 10:05:00',1,'2026-06-05 15:20:03'),(61,402,11,'2024-03-01 10:10:00',1,'2026-06-05 15:20:03'),(62,403,11,'2024-03-02 09:00:00',1,'2026-06-05 15:20:03'),(63,404,11,'2024-03-02 09:15:00',1,'2026-06-05 15:20:03'),(64,405,11,'2024-03-02 09:30:00',1,'2026-06-05 15:20:03'),(65,406,11,'2024-03-03 08:00:00',1,'2026-06-05 15:20:03'),(66,407,11,'2024-03-03 08:20:00',1,'2026-06-05 15:20:03'),(67,408,11,'2024-03-03 08:40:00',1,'2026-06-05 15:20:03'),(68,409,11,'2024-03-04 10:00:00',1,'2026-06-05 15:20:03'),(69,410,11,'2024-03-04 10:30:00',1,'2026-06-05 15:20:03'),(70,411,11,'2024-03-04 11:00:00',1,'2026-06-05 15:20:03'),(71,412,11,'2024-03-05 09:00:00',1,'2026-06-05 15:20:03'),(72,413,11,'2024-03-05 09:30:00',1,'2026-06-05 15:20:03'),(73,414,11,'2024-03-05 10:00:00',1,'2026-06-05 15:20:03'),(74,400,12,'2024-03-01 11:00:00',1,'2026-06-05 15:20:03'),(75,401,12,'2024-03-01 11:10:00',1,'2026-06-05 15:20:03'),(76,402,12,'2024-03-01 11:20:00',1,'2026-06-05 15:20:03'),(77,403,12,'2024-03-02 10:00:00',1,'2026-06-05 15:20:03'),(78,404,12,'2024-03-02 10:15:00',1,'2026-06-05 15:20:03'),(79,405,12,'2024-03-02 10:30:00',1,'2026-06-05 15:20:03'),(80,406,12,'2024-03-03 09:00:00',1,'2026-06-05 15:20:03'),(81,407,12,'2024-03-03 09:20:00',1,'2026-06-05 15:20:03'),(82,408,12,'2024-03-03 09:40:00',1,'2026-06-05 15:20:03'),(83,409,12,'2024-03-04 11:00:00',1,'2026-06-05 15:20:03'),(84,410,12,'2024-03-04 11:30:00',1,'2026-06-05 15:20:03'),(85,411,12,'2024-03-04 12:00:00',1,'2026-06-05 15:20:03'),(86,400,13,'2024-03-01 12:00:00',1,'2026-06-05 15:20:03'),(87,401,13,'2024-03-01 12:10:00',1,'2026-06-05 15:20:03'),(88,402,13,'2024-03-01 12:20:00',1,'2026-06-05 15:20:03'),(89,403,13,'2024-03-02 11:00:00',1,'2026-06-05 15:20:03'),(90,404,13,'2024-03-02 11:15:00',1,'2026-06-05 15:20:03'),(91,405,13,'2024-03-02 11:30:00',1,'2026-06-05 15:20:03'),(92,406,13,'2024-03-03 10:00:00',1,'2026-06-05 15:20:03'),(93,407,13,'2024-03-03 10:20:00',1,'2026-06-05 15:20:03'),(94,408,13,'2024-03-03 10:40:00',1,'2026-06-05 15:20:03'),(95,409,13,'2024-03-04 13:00:00',1,'2026-06-05 15:20:03'),(96,208,14,'2026-06-05 15:52:46',1,'2026-06-05 15:52:46'),(97,415,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(98,415,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(99,415,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(100,416,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(101,416,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(102,416,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(103,417,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(104,417,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(105,417,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(106,418,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(107,418,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(108,419,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(109,419,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(110,419,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(111,420,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(112,420,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(113,420,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(114,421,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(115,421,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(116,421,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(117,422,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(118,422,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(119,422,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(120,423,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(121,423,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(122,424,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(123,424,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(124,425,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(125,425,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(126,426,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(127,426,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(128,427,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(129,427,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(130,428,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(131,428,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(132,428,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(133,429,13,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(134,429,12,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(135,429,11,'2026-06-05 16:03:16',1,'2026-06-05 16:03:16'),(136,400,15,'2026-06-05 16:37:15',1,'2026-06-05 16:37:15'),(137,401,15,'2026-06-06 17:38:25',1,'2026-06-06 17:38:25'),(148,402,15,'2026-06-06 19:58:37',1,'2026-06-06 19:58:37'),(149,403,15,'2026-06-06 19:58:37',1,'2026-06-06 19:58:37'),(150,404,15,'2026-06-06 19:58:37',1,'2026-06-06 19:58:37'),(151,405,15,'2026-06-06 19:58:37',1,'2026-06-06 19:58:37'),(153,103,19,'2026-06-09 15:44:30',1,'2026-06-09 15:44:30'),(154,400,20,'2026-06-09 15:54:06',1,'2026-06-09 15:54:06');
/*!40000 ALTER TABLE `course_selection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `exam_paper_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `semester_id` bigint DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `duration` int NOT NULL,
  `total_score` decimal(6,2) NOT NULL,
  `pass_score` decimal(6,2) DEFAULT NULL,
  `allow_times` int NOT NULL DEFAULT '1',
  `is_random_order` tinyint NOT NULL DEFAULT '1',
  `is_random_options` tinyint NOT NULL DEFAULT '1',
  `exam_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'formal',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `student_count` int NOT NULL DEFAULT '0',
  `submit_count` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_exam_paper_id` (`exam_paper_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam`
--

LOCK TABLES `exam` WRITE;
/*!40000 ALTER TABLE `exam` DISABLE KEYS */;
INSERT INTO `exam` VALUES (1,'计算机网络第一章测验',1,1,101,1,'2026-06-04 17:08:08','2026-06-04 17:38:08',30,70.00,42.00,1,1,1,'formal','published',6,3,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(2,'计算机网络期中考试',2,1,101,1,'2026-06-10 17:08:08','2026-06-10 18:38:08',90,100.00,60.00,1,1,1,'formal','cancelled',6,0,'2026-06-03 17:08:08','2026-06-05 09:46:22',0),(3,'Java第一章测验',3,2,101,1,'2026-06-06 17:08:08','2026-06-06 17:53:08',45,50.00,30.00,1,1,1,'formal','cancelled',6,0,'2026-06-03 17:08:08','2026-06-05 09:57:39',0),(4,'Web前端开发-期末考试',12,11,103,NULL,'2026-02-01 09:00:00','2026-02-01 11:00:00',120,15.00,9.00,1,1,1,'formal','cancelled',3,0,'2026-01-15 10:00:00','2026-06-05 15:31:33',0),(5,'Java程序设计-期中考试',13,12,103,NULL,'2026-06-05 20:00:00','2026-06-05 21:30:00',90,100.00,60.00,1,1,1,'formal','published',12,12,'2026-01-15 10:00:00','2026-06-06 20:16:32',0),(6,'Python数据分析-单元测验',14,13,103,NULL,'2026-02-10 19:00:00','2026-02-10 20:00:00',60,15.00,9.00,1,1,1,'formal','ongoing',2,0,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(7,'网络工程综合实训-课堂测验一',17,15,103,NULL,'2026-06-05 00:00:00','2026-06-30 00:00:00',120,100.00,60.00,1,1,1,'formal','published',0,0,'2026-06-05 16:57:04','2026-06-09 16:23:22',0),(8,'课程15联调考试A',18,15,103,NULL,'2026-06-06 00:00:00','2026-06-30 23:59:59',30,5.00,3.00,1,0,0,'formal','published',7,7,'2026-06-06 17:51:34','2026-06-06 19:58:37',0),(14,'Java程序设计-第一次单元测验',13,12,103,1,'2026-03-10 14:00:00','2026-03-10 15:20:00',80,100.00,60.00,1,1,1,'quiz','published',12,12,'2026-06-06 20:38:44','2026-06-06 20:43:23',0),(15,'Java程序设计-第二次单元测验',13,12,103,1,'2026-04-15 14:00:00','2026-04-15 15:20:00',80,100.00,60.00,1,1,1,'quiz','published',12,12,'2026-06-06 20:38:44','2026-06-06 20:43:23',0),(16,'网络工程综合实训-课堂测验一',17,15,103,NULL,'2026-06-01 00:00:00','2026-06-08 11:40:05',120,100.00,60.00,1,1,1,'formal','finished',0,0,'2026-06-07 17:22:12','2026-06-09 16:23:22',0),(17,'网络工程综合实训-阶段测验',19,15,103,NULL,'2026-06-07 00:00:00','2026-06-08 09:53:12',120,100.00,60.00,1,1,1,'formal','finished',0,0,'2026-06-08 08:43:35','2026-06-09 16:23:22',0),(19,'Web前端开发技术-展示测验',23,11,103,NULL,'2026-06-01 09:00:00','2026-06-01 09:45:00',45,15.00,9.00,1,0,0,'formal','ended',1,1,'2026-06-08 21:41:41','2026-06-09 16:27:54',0),(20,'数据库系统实践-课堂练习',21,19,103,NULL,'2026-06-08 00:00:00','2026-06-10 00:00:00',120,100.00,60.00,1,1,1,'formal','cancelled',0,0,'2026-06-09 15:45:12','2026-06-09 16:23:22',0),(21,'软件测试基础-随堂测验',21,19,103,NULL,'2026-06-08 00:00:00','2026-06-10 00:00:00',120,100.00,60.00,1,1,1,'formal','published',0,0,'2026-06-09 15:52:03','2026-06-09 16:23:22',0),(22,'软件测试基础-阶段测验',21,20,103,NULL,'2026-06-08 00:00:00','2026-06-10 00:00:00',120,100.00,60.00,1,1,1,'formal','published',0,0,'2026-06-09 15:54:25','2026-06-09 16:27:54',0);
/*!40000 ALTER TABLE `exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_paper`
--

DROP TABLE IF EXISTS `exam_paper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_paper` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `course_id` bigint DEFAULT NULL,
  `teacher_id` bigint NOT NULL,
  `total_score` decimal(6,2) NOT NULL DEFAULT '100.00',
  `pass_score` decimal(6,2) DEFAULT NULL,
  `duration` int NOT NULL DEFAULT '120',
  `question_count` int NOT NULL DEFAULT '0',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'manual',
  `rule_config` json DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_paper`
--

LOCK TABLES `exam_paper` WRITE;
/*!40000 ALTER TABLE `exam_paper` DISABLE KEYS */;
INSERT INTO `exam_paper` VALUES (1,'计算机网络第一章测验',1,101,70.00,42.00,30,14,'manual',NULL,NULL,1,'2026-06-03 17:08:08','2026-06-04 10:41:58',1),(2,'计算机网络期中考试',1,101,100.00,60.00,90,20,'manual',NULL,NULL,1,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(3,'Java程序设计测验',2,101,50.00,30.00,45,10,'manual',NULL,NULL,1,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(4,'试卷 4',1,101,100.00,60.00,90,3,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 15:30:24','2026-06-08 20:28:18',0),(5,'试卷 5',1,101,100.00,60.00,90,0,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 15:45:02','2026-06-08 20:28:18',1),(6,'试卷 6',1,101,100.00,60.00,90,0,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 15:51:17','2026-06-08 20:28:18',0),(7,'试卷 7',1,101,100.00,60.00,90,0,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 15:54:35','2026-06-08 20:28:18',0),(8,'试卷 8',1,101,100.00,60.00,90,0,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 15:58:04','2026-06-08 20:28:18',1),(9,'试卷 9',1,101,100.00,60.00,90,0,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 16:00:52','2026-06-08 20:28:18',1),(10,'试卷 10',1,101,100.00,60.00,90,0,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 16:06:52','2026-06-08 20:28:18',1),(11,'试卷 11',1,101,100.00,60.00,90,0,'manual',NULL,'试卷信息存在编码异常，已临时修复为可读占位。',0,'2026-06-04 16:14:14','2026-06-08 20:28:18',1),(12,'Web前端开发-期末试卷',11,103,15.00,9.00,90,3,'manual',NULL,'期末综合测试',1,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(13,'Java程序设计-期中试卷',12,103,100.00,60.00,75,3,'manual',NULL,'期中考核',1,'2026-01-15 10:00:00','2026-06-06 20:16:32',0),(14,'Python数据分析-随堂测验',13,103,15.00,9.00,60,3,'manual',NULL,'单元小测',1,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(15,'Web前端开发技术-随堂练习',11,103,5.00,60.00,120,1,'manual',NULL,'用于联调展示的 Web 前端开发随堂练习试卷',1,'2026-06-05 15:31:06','2026-06-09 16:27:54',0),(16,'网络设备配置实训-基础测验',14,208,2.00,60.00,120,1,'manual',NULL,'用于联调展示的网络设备配置基础测验试卷',1,'2026-06-05 15:52:29','2026-06-09 16:28:22',0),(17,'网络工程综合实训-课堂测验一',15,103,2.00,60.00,120,1,'manual',NULL,'用于联调展示的网络工程课堂测验试卷',1,'2026-06-05 16:36:26','2026-06-09 16:23:22',0),(18,'课程15联调试卷A',15,103,5.00,60.00,30,1,'manual',NULL,NULL,1,'2026-06-06 17:50:19','2026-06-06 19:11:16',0),(19,'网络工程综合实训-阶段测验',15,103,2.00,60.00,120,1,'manual',NULL,'用于联调展示的网络工程阶段测验试卷',1,'2026-06-08 08:42:56','2026-06-09 16:23:22',0),(20,'网络工程综合实训-期末练习',15,103,30.00,60.00,120,3,'manual',NULL,'用于联调展示的网络工程期末练习试卷',1,'2026-06-08 09:02:23','2026-06-09 16:23:22',0),(21,'数据库系统实践-课堂练习草稿',15,103,25.00,60.00,120,0,'manual',NULL,'用于联调展示的数据库系统实践课堂练习草稿',0,'2026-06-08 15:19:08','2026-06-09 16:23:22',0),(23,'Web前端开发技术-展示试卷',11,103,15.00,9.00,45,3,'manual',NULL,'用于学生端成绩报告和错题辅导验收的展示试卷',1,'2026-06-08 21:41:41','2026-06-09 16:27:54',0),(24,'test',11,103,10.00,60.00,120,5,'manual',NULL,NULL,1,'2026-06-09 08:52:09','2026-06-09 08:53:17',1);
/*!40000 ALTER TABLE `exam_paper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_paper_question`
--

DROP TABLE IF EXISTS `exam_paper_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_paper_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_paper_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `score` decimal(6,2) NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_paper_question` (`exam_paper_id`,`question_id`),
  KEY `idx_exam_paper_id` (`exam_paper_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=139 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_paper_question`
--

LOCK TABLES `exam_paper_question` WRITE;
/*!40000 ALTER TABLE `exam_paper_question` DISABLE KEYS */;
INSERT INTO `exam_paper_question` VALUES (1,1,1,5.00,1,'2026-06-03 17:08:08'),(2,1,2,5.00,2,'2026-06-03 17:08:08'),(3,1,3,5.00,3,'2026-06-03 17:08:08'),(4,1,4,5.00,4,'2026-06-03 17:08:08'),(5,1,5,5.00,5,'2026-06-03 17:08:08'),(6,1,6,5.00,6,'2026-06-03 17:08:08'),(7,1,7,5.00,7,'2026-06-03 17:08:08'),(8,1,8,5.00,8,'2026-06-03 17:08:08'),(9,1,9,5.00,9,'2026-06-03 17:08:08'),(10,1,10,5.00,10,'2026-06-03 17:08:08'),(11,1,11,5.00,11,'2026-06-03 17:08:08'),(12,1,12,5.00,12,'2026-06-03 17:08:08'),(13,1,20,8.00,13,'2026-06-03 17:08:08'),(14,1,21,8.00,14,'2026-06-03 17:08:08'),(15,2,1,5.00,1,'2026-06-03 17:08:08'),(16,2,2,5.00,2,'2026-06-03 17:08:08'),(17,2,3,5.00,3,'2026-06-03 17:08:08'),(18,2,4,5.00,4,'2026-06-03 17:08:08'),(19,2,5,5.00,5,'2026-06-03 17:08:08'),(20,2,6,5.00,6,'2026-06-03 17:08:08'),(21,2,7,5.00,7,'2026-06-03 17:08:08'),(22,2,8,5.00,8,'2026-06-03 17:08:08'),(23,2,9,5.00,9,'2026-06-03 17:08:08'),(24,2,10,5.00,10,'2026-06-03 17:08:08'),(25,2,11,5.00,11,'2026-06-03 17:08:08'),(26,2,12,5.00,12,'2026-06-03 17:08:08'),(27,2,13,5.00,13,'2026-06-03 17:08:08'),(28,2,14,5.00,14,'2026-06-03 17:08:08'),(29,2,15,5.00,15,'2026-06-03 17:08:08'),(30,2,16,5.00,16,'2026-06-03 17:08:08'),(31,2,17,5.00,17,'2026-06-03 17:08:08'),(32,2,18,5.00,18,'2026-06-03 17:08:08'),(33,2,19,5.00,19,'2026-06-03 17:08:08'),(34,2,20,5.00,20,'2026-06-03 17:08:08'),(59,4,1,10.00,1,'2026-06-04 16:14:14'),(60,4,2,20.00,2,'2026-06-04 16:14:14'),(61,4,3,15.00,3,'2026-06-04 16:14:14'),(98,12,40,5.00,1,'2026-06-05 10:28:42'),(99,12,41,5.00,2,'2026-06-05 10:28:42'),(100,12,42,5.00,3,'2026-06-05 10:28:42'),(104,14,46,5.00,1,'2026-06-05 10:28:42'),(105,14,47,5.00,2,'2026-06-05 10:28:42'),(106,14,48,5.00,3,'2026-06-05 10:28:42'),(107,15,42,5.00,1,'2026-06-05 15:31:06'),(108,16,50,2.00,1,'2026-06-05 15:52:29'),(109,17,49,2.00,1,'2026-06-05 16:36:26'),(110,54932,51,5.00,1,'2026-06-06 17:50:19'),(111,18,51,5.00,1,'2026-06-06 17:51:14'),(112,13,43,20.00,1,'2026-06-06 21:02:37'),(113,13,44,20.00,2,'2026-06-06 21:02:37'),(114,13,45,20.00,3,'2026-06-06 21:02:37'),(115,13,46,10.00,4,'2026-06-06 21:02:37'),(116,13,47,10.00,5,'2026-06-06 21:02:37'),(117,13,48,20.00,6,'2026-06-06 21:02:37'),(119,19,49,2.00,1,'2026-06-08 08:43:00'),(120,20,51,5.00,1,'2026-06-08 09:02:23'),(121,20,40,5.00,2,'2026-06-08 09:02:23'),(122,20,48,20.00,3,'2026-06-08 09:02:23'),(123,21,107,5.00,1,'2026-06-08 15:19:09'),(124,21,108,5.00,2,'2026-06-08 15:19:09'),(125,21,109,5.00,3,'2026-06-08 15:19:09'),(126,21,110,5.00,4,'2026-06-08 15:19:09'),(127,21,111,5.00,5,'2026-06-08 15:19:09'),(131,23,40,5.00,1,'2026-06-08 21:41:41'),(132,23,41,5.00,2,'2026-06-08 21:41:41'),(133,23,42,5.00,3,'2026-06-08 21:41:41');
/*!40000 ALTER TABLE `exam_paper_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_paper_template`
--

DROP TABLE IF EXISTS `exam_paper_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_paper_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `course_id` bigint DEFAULT NULL,
  `teacher_id` bigint NOT NULL,
  `template_config` json NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usage_count` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_paper_template`
--

LOCK TABLES `exam_paper_template` WRITE;
/*!40000 ALTER TABLE `exam_paper_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `exam_paper_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_record`
--

DROP TABLE IF EXISTS `exam_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `exam_student_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `submit_time` datetime DEFAULT NULL,
  `total_score` decimal(6,2) DEFAULT NULL,
  `objective_score` decimal(6,2) DEFAULT NULL,
  `subjective_score` decimal(6,2) DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ongoing',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'exam',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `extra_data` json DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `answered_count` int DEFAULT '0',
  `total_questions` int DEFAULT '0',
  `switch_screen_count` int DEFAULT '0',
  `network_interrupt_count` int DEFAULT '0',
  `duration_seconds` bigint DEFAULT '0',
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_exam_student_id` (`exam_student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=239 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_record`
--

LOCK TABLES `exam_record` WRITE;
/*!40000 ALTER TABLE `exam_record` DISABLE KEYS */;
INSERT INTO `exam_record` VALUES (1,1,201,1,'2025-06-01 14:00:00','2025-06-01 14:22:30',62.00,62.00,0.00,'submitted','exam','192.168.1.100','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120',NULL,'2026-06-03 17:08:09','2026-06-03 17:08:09',0,0,0,0,0,0),(2,1,202,2,'2025-06-01 14:01:00','2025-06-01 14:25:10',55.00,55.00,0.00,'submitted','exam','192.168.1.101','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120',NULL,'2026-06-03 17:08:09','2026-06-03 17:08:09',0,0,0,0,0,0),(3,1,203,3,'2025-06-01 14:02:00','2025-06-01 14:28:45',48.00,48.00,0.00,'submitted','exam','192.168.1.102','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605',NULL,'2026-06-03 17:08:09','2026-06-03 17:08:09',0,0,0,0,0,0),(101,4,306,0,'2026-02-01 09:05:00','2026-02-01 10:30:00',12.00,9.00,3.00,'submitted','exam','127.0.0.1',NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',3,3,0,0,4500,0),(102,4,307,0,'2026-02-01 09:05:00','2026-02-01 10:30:00',12.00,9.00,3.00,'submitted','exam','127.0.0.1',NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',3,3,0,0,4500,0),(105,6,306,0,'2026-02-10 19:05:00',NULL,NULL,NULL,NULL,'ongoing','exam','192.168.1.100',NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',1,3,0,0,300,0),(106,6,307,0,'2026-02-10 19:05:00',NULL,NULL,NULL,NULL,'ongoing','exam','192.168.1.100',NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',1,3,0,0,300,0),(109,6,400,400,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',6,13,0,0,NULL,0),(111,6,401,401,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',11,16,5,1,NULL,0),(113,6,402,402,'2026-02-10 19:00:00','2026-06-05 15:01:52',22.80,15.96,6.84,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',5,12,2,2,3412,0),(115,6,403,403,'2026-02-10 19:00:00','2026-06-05 14:01:52',41.10,28.77,12.33,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',13,16,1,1,2425,0),(117,6,404,404,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',8,23,4,0,NULL,0),(119,6,405,405,'2026-02-10 19:00:00','2026-06-05 12:01:52',28.70,20.09,8.61,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',10,18,1,2,3159,0),(121,6,406,406,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',5,20,5,0,NULL,0),(123,6,407,407,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',20,15,4,2,NULL,0),(125,6,408,408,'2026-02-10 19:00:00','2026-06-05 14:01:52',41.90,29.33,12.57,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',9,15,4,1,3819,0),(127,6,409,409,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',6,18,5,0,NULL,0),(129,6,410,410,'2026-02-10 19:00:00','2026-06-05 11:01:52',58.20,40.74,17.46,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',13,17,4,1,4155,0),(131,6,411,411,'2026-02-10 19:00:00','2026-06-05 13:01:52',58.10,40.67,17.43,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',13,25,1,2,2386,0),(133,6,412,412,'2026-02-10 19:00:00','2026-06-05 14:01:52',54.90,38.43,16.47,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',5,13,1,0,3618,0),(135,6,413,413,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',18,10,1,1,NULL,0),(137,6,414,414,'2026-02-10 19:00:00','2026-06-05 14:01:52',39.80,27.86,11.94,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:01:52','2026-06-05 16:01:52',14,24,5,0,4420,0),(139,6,415,415,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',18,14,0,1,NULL,0),(141,6,416,416,'2026-02-10 19:00:00','2026-06-05 14:03:31',18.60,13.02,5.58,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',20,22,1,1,5135,0),(143,6,417,417,'2026-02-10 19:00:00','2026-06-05 14:03:31',53.50,37.45,16.05,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',13,22,4,1,2183,0),(145,6,419,419,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',15,14,4,1,NULL,0),(147,6,420,420,'2026-02-10 19:00:00','2026-06-05 12:03:31',30.10,21.07,9.03,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',5,21,3,2,3379,0),(149,6,421,421,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',16,15,1,2,NULL,0),(151,6,422,422,'2026-02-10 19:00:00','2026-06-05 13:03:31',59.40,41.58,17.82,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',20,19,1,2,4452,0),(153,6,423,423,'2026-02-10 19:00:00',NULL,NULL,0.00,0.00,'ongoing','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',9,20,2,0,NULL,0),(155,6,424,424,'2026-02-10 19:00:00','2026-06-05 15:03:31',42.10,29.47,12.63,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',11,23,5,0,4278,0),(157,6,425,425,'2026-02-10 19:00:00','2026-06-05 13:03:31',28.20,19.74,8.46,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',9,20,3,2,4965,0),(159,6,428,428,'2026-02-10 19:00:00','2026-06-05 15:03:31',21.70,15.19,6.51,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',12,25,1,1,4753,0),(161,6,429,429,'2026-02-10 19:00:00','2026-06-05 11:03:31',30.40,21.28,9.12,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',6,18,3,2,2695,0),(163,6,418,418,'2026-02-10 19:00:00','2026-06-05 11:03:31',54.00,37.80,16.20,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',12,23,0,1,4485,0),(165,6,426,426,'2026-02-10 19:00:00','2026-06-05 14:03:31',44.20,30.94,13.26,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',12,14,2,2,4536,0),(167,6,427,427,'2026-02-10 19:00:00','2026-06-05 15:03:31',18.00,12.60,5.40,'submitted','exam',NULL,NULL,NULL,'2026-06-05 16:03:31','2026-06-05 16:03:31',14,25,1,2,3593,0),(168,7,400,400,'2026-06-05 17:36:24','2026-06-05 17:47:29',1.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-05 17:36:24','2026-06-05 17:36:24',0,0,0,0,665,0),(169,7,401,401,'2026-06-06 17:42:01','2026-06-06 17:42:20',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-06 17:42:01','2026-06-06 17:42:01',1,0,0,0,19,0),(170,7,401,401,'2026-06-06 17:47:00','2026-06-06 17:47:01',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-06 17:47:00','2026-06-06 17:47:00',1,0,0,0,0,0),(171,8,400,19,'2026-06-06 18:00:00','2026-06-06 18:12:00',3.00,0.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-06-06 18:00:00','2026-06-06 18:12:00',1,1,0,0,720,0),(172,8,401,20,'2026-06-06 18:03:00','2026-06-06 18:16:00',5.00,5.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-06-06 18:03:00','2026-06-06 18:16:00',1,1,1,0,780,0),(173,8,402,21,'2026-06-06 18:06:00','2026-06-06 18:20:00',0.00,0.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-06-06 18:06:00','2026-06-06 18:20:00',1,1,2,1,840,0),(174,8,403,22,'2026-06-06 18:09:00','2026-06-06 18:24:00',5.00,5.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-06-06 18:09:00','2026-06-06 18:24:00',1,1,0,0,900,0),(175,8,404,23,'2026-06-06 18:12:00','2026-06-06 18:28:00',0.00,0.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-06-06 18:12:00','2026-06-06 18:28:00',1,1,1,0,960,0),(176,8,405,24,'2026-06-06 18:15:00','2026-06-06 18:32:00',0.00,0.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-06-06 18:15:00','2026-06-06 18:32:00',1,1,2,1,1020,0),(177,8,406,25,'2026-06-06 18:18:00','2026-06-06 18:36:00',5.00,5.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-06-06 18:18:00','2026-06-06 18:36:00',1,1,0,0,1080,0),(190,14,306,38,'2026-03-10 14:00:00','2026-03-10 14:55:00',72.00,72.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:00:00','2026-03-10 14:55:00',3,3,0,0,3000,0),(191,14,307,39,'2026-03-10 14:01:00','2026-03-10 14:57:00',65.00,65.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:01:00','2026-03-10 14:57:00',3,3,1,0,3040,0),(192,14,400,40,'2026-03-10 14:02:00','2026-03-10 14:59:00',53.00,53.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:02:00','2026-03-10 14:59:00',3,3,0,0,3080,0),(193,14,401,41,'2026-03-10 14:03:00','2026-03-10 15:01:00',41.00,41.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:03:00','2026-03-10 15:01:00',3,3,1,0,3120,0),(194,14,402,42,'2026-03-10 14:04:00','2026-03-10 15:03:00',35.00,35.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:04:00','2026-03-10 15:03:00',3,3,0,0,3160,0),(195,14,403,43,'2026-03-10 14:05:00','2026-03-10 15:05:00',69.00,69.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:05:00','2026-03-10 15:05:00',3,3,1,0,3200,0),(196,14,404,44,'2026-03-10 14:06:00','2026-03-10 15:07:00',60.00,60.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:06:00','2026-03-10 15:07:00',3,3,0,0,3240,0),(197,14,405,45,'2026-03-10 14:07:00','2026-03-10 15:09:00',48.00,48.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:07:00','2026-03-10 15:09:00',3,3,1,0,3280,0),(198,14,406,46,'2026-03-10 14:08:00','2026-03-10 15:11:00',44.00,44.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:08:00','2026-03-10 15:11:00',3,3,0,0,3320,0),(199,14,407,47,'2026-03-10 14:09:00','2026-03-10 15:13:00',35.00,35.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:09:00','2026-03-10 15:13:00',3,3,1,0,3360,0),(200,14,408,48,'2026-03-10 14:10:00','2026-03-10 15:15:00',56.00,56.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:10:00','2026-03-10 15:15:00',3,3,0,0,3400,0),(201,14,409,49,'2026-03-10 14:11:00','2026-03-10 15:17:00',38.00,38.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-03-10 14:11:00','2026-03-10 15:17:00',3,3,1,0,3440,0),(202,15,306,50,'2026-04-15 14:00:00','2026-04-15 14:55:00',84.00,84.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:00:00','2026-04-15 14:55:00',3,3,0,0,3000,0),(203,15,307,51,'2026-04-15 14:01:00','2026-04-15 14:57:00',77.00,77.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:01:00','2026-04-15 14:57:00',3,3,1,0,3040,0),(204,15,400,52,'2026-04-15 14:02:00','2026-04-15 14:59:00',67.00,66.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:02:00','2026-04-15 14:59:00',3,3,0,0,3080,0),(205,15,401,53,'2026-04-15 14:03:00','2026-04-15 15:01:00',54.00,54.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:03:00','2026-04-15 15:01:00',3,3,1,0,3120,0),(206,15,402,54,'2026-04-15 14:04:00','2026-04-15 15:03:00',48.00,48.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:04:00','2026-04-15 15:03:00',3,3,0,0,3160,0),(207,15,403,55,'2026-04-15 14:05:00','2026-04-15 15:05:00',81.00,81.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:05:00','2026-04-15 15:05:00',3,3,1,0,3200,0),(208,15,404,56,'2026-04-15 14:06:00','2026-04-15 15:07:00',72.00,72.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:06:00','2026-04-15 15:07:00',3,3,0,0,3240,0),(209,15,405,57,'2026-04-15 14:07:00','2026-04-15 15:09:00',60.00,60.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:07:00','2026-04-15 15:09:00',3,3,1,0,3280,0),(210,15,406,58,'2026-04-15 14:08:00','2026-04-15 15:11:00',56.00,56.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:08:00','2026-04-15 15:11:00',3,3,0,0,3320,0),(211,15,407,59,'2026-04-15 14:09:00','2026-04-15 15:13:00',43.00,43.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:09:00','2026-04-15 15:13:00',3,3,1,0,3360,0),(212,15,408,60,'2026-04-15 14:10:00','2026-04-15 15:15:00',68.00,68.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:10:00','2026-04-15 15:15:00',3,3,0,0,3400,0),(213,15,409,61,'2026-04-15 14:11:00','2026-04-15 15:17:00',50.00,50.00,0.00,'submitted','normal',NULL,NULL,NULL,'2026-04-15 14:11:00','2026-04-15 15:17:00',3,3,1,0,3440,0),(214,5,306,62,'2026-06-05 20:00:00','2026-06-05 21:08:00',95.00,78.00,17.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:00:00','2026-06-05 21:08:00',6,6,0,0,3600,0),(215,5,307,63,'2026-06-05 20:02:00','2026-06-05 21:11:00',88.00,69.00,19.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:02:00','2026-06-05 21:11:00',6,6,1,0,3645,0),(216,5,400,64,'2026-06-05 20:04:00','2026-06-05 21:14:00',76.00,57.00,19.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:04:00','2026-06-05 21:14:00',6,6,0,0,3690,0),(217,5,401,65,'2026-06-05 20:06:00','2026-06-05 21:17:00',64.00,48.00,16.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:06:00','2026-06-05 21:17:00',6,6,1,1,3735,0),(218,5,402,66,'2026-06-05 20:08:00','2026-06-05 21:20:00',58.00,43.00,15.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:08:00','2026-06-05 21:20:00',6,6,0,0,3780,0),(219,5,403,67,'2026-06-05 20:10:00','2026-06-05 21:23:00',92.00,76.00,16.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:10:00','2026-06-05 21:23:00',6,6,1,0,3825,0),(220,5,404,68,'2026-06-05 20:12:00','2026-06-05 21:26:00',83.00,64.00,19.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:12:00','2026-06-05 21:26:00',6,6,0,0,3870,0),(221,5,405,69,'2026-06-05 20:14:00','2026-06-05 21:29:00',71.00,52.00,19.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:14:00','2026-06-05 21:29:00',6,6,1,0,3915,0),(222,5,406,70,'2026-06-05 20:16:00','2026-06-05 21:32:00',67.00,51.00,16.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:16:00','2026-06-05 21:32:00',6,6,0,1,3960,0),(223,5,407,71,'2026-06-05 20:18:00','2026-06-05 21:35:00',54.00,40.00,14.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:18:00','2026-06-05 21:35:00',6,6,1,0,4005,0),(224,5,408,72,'2026-06-05 20:20:00','2026-06-05 21:38:00',79.00,59.00,20.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:20:00','2026-06-05 21:38:00',6,6,0,0,4050,0),(225,5,409,73,'2026-06-05 20:22:00','2026-06-05 21:41:00',61.00,46.00,15.00,'submitted','normal',NULL,NULL,NULL,'2026-06-05 20:22:00','2026-06-05 21:41:00',6,6,1,0,4095,0),(226,16,400,400,'2026-06-07 17:22:59','2026-06-08 11:24:09',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-07 17:22:59','2026-06-07 17:22:59',1,1,0,0,64870,0),(227,16,103,103,'2026-06-07 17:28:41','2026-06-07 17:31:01',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-07 17:28:41','2026-06-09 16:23:22',1,0,0,0,139,1),(228,17,103,103,'2026-06-08 08:43:40','2026-06-08 08:43:42',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 08:43:40','2026-06-09 16:23:22',0,0,0,0,2,1),(229,7,103,103,'2026-06-08 09:07:25','2026-06-08 09:07:28',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:07:25','2026-06-09 16:23:22',1,0,0,0,2,1),(230,8,103,103,'2026-06-08 09:07:31','2026-06-08 09:07:34',5.00,5.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:07:31','2026-06-09 16:23:22',1,0,0,0,2,1),(231,7,103,103,'2026-06-08 09:07:35','2026-06-08 09:36:32',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:07:35','2026-06-09 16:23:22',1,0,0,0,1737,1),(232,17,400,400,'2026-06-08 09:37:25','2026-06-08 09:37:28',0.00,0.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:37:25','2026-06-08 09:37:25',0,0,0,0,3,0),(233,6,201,201,'2026-06-08 09:51:18','2026-06-08 09:51:18',28.00,10.00,18.00,'submitted','exam',NULL,NULL,NULL,'2026-06-08 09:51:18','2026-06-08 09:51:18',3,3,0,0,0,0),(235,19,400,75,'2026-06-01 09:03:00','2026-06-01 09:32:00',12.00,12.00,0.00,'submitted','exam','127.0.0.1','codex_acceptance_seed',NULL,'2026-06-08 21:41:41','2026-06-08 21:41:41',0,0,0,0,0,0),(236,20,103,103,'2026-06-09 15:45:19','2026-06-09 15:45:44',20.00,20.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-09 15:45:19','2026-06-09 16:23:22',5,5,0,0,24,1),(237,21,400,400,'2026-06-09 15:52:09','2026-06-09 15:52:50',15.00,15.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-09 15:52:09','2026-06-09 15:52:09',5,5,0,0,41,0),(238,22,400,400,'2026-06-09 15:54:31','2026-06-09 15:54:47',20.00,10.00,0.00,'submitted','exam',NULL,NULL,NULL,'2026-06-09 15:54:31','2026-06-09 15:54:31',5,5,0,0,15,0);
/*!40000 ALTER TABLE `exam_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_student`
--

DROP TABLE IF EXISTS `exam_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_student` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `course_selection_id` bigint DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `exam_times` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_exam_student` (`exam_id`,`student_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_student`
--

LOCK TABLES `exam_student` WRITE;
/*!40000 ALTER TABLE `exam_student` DISABLE KEYS */;
INSERT INTO `exam_student` VALUES (1,1,201,NULL,'submitted',1,'2026-06-03 17:08:08'),(2,1,202,NULL,'submitted',1,'2026-06-03 17:08:08'),(3,1,203,NULL,'submitted',1,'2026-06-03 17:08:08'),(4,1,204,NULL,'pending',0,'2026-06-03 17:08:08'),(5,1,205,NULL,'pending',0,'2026-06-03 17:08:08'),(6,1,206,NULL,'pending',0,'2026-06-03 17:08:08'),(7,2,201,NULL,'pending',0,'2026-06-03 17:08:08'),(8,2,202,NULL,'pending',0,'2026-06-03 17:08:08'),(9,2,203,NULL,'pending',0,'2026-06-03 17:08:08'),(10,2,204,NULL,'pending',0,'2026-06-03 17:08:08'),(11,2,205,NULL,'pending',0,'2026-06-03 17:08:08'),(12,2,206,NULL,'pending',0,'2026-06-03 17:08:08'),(13,3,201,NULL,'pending',0,'2026-06-03 17:08:08'),(14,3,202,NULL,'pending',0,'2026-06-03 17:08:08'),(15,3,203,NULL,'pending',0,'2026-06-03 17:08:08'),(16,3,204,NULL,'pending',0,'2026-06-03 17:08:08'),(17,3,205,NULL,'pending',0,'2026-06-03 17:08:08'),(18,3,206,NULL,'pending',0,'2026-06-03 17:08:08'),(19,8,400,136,'submitted',1,'2026-06-06 19:58:37'),(20,8,401,137,'submitted',1,'2026-06-06 19:58:37'),(21,8,402,148,'submitted',1,'2026-06-06 19:58:37'),(22,8,403,149,'submitted',1,'2026-06-06 19:58:37'),(23,8,404,150,'submitted',1,'2026-06-06 19:58:37'),(24,8,405,151,'submitted',1,'2026-06-06 19:58:37'),(25,8,406,152,'submitted',1,'2026-06-06 19:58:37'),(38,14,306,52,'submitted',1,'2026-06-06 20:38:44'),(39,14,307,55,'submitted',1,'2026-06-06 20:38:44'),(40,14,400,74,'submitted',1,'2026-06-06 20:38:44'),(41,14,401,75,'submitted',1,'2026-06-06 20:38:44'),(42,14,402,76,'submitted',1,'2026-06-06 20:38:44'),(43,14,403,77,'submitted',1,'2026-06-06 20:38:44'),(44,14,404,78,'submitted',1,'2026-06-06 20:38:44'),(45,14,405,79,'submitted',1,'2026-06-06 20:38:44'),(46,14,406,80,'submitted',1,'2026-06-06 20:38:44'),(47,14,407,81,'submitted',1,'2026-06-06 20:38:44'),(48,14,408,82,'submitted',1,'2026-06-06 20:38:44'),(49,14,409,83,'submitted',1,'2026-06-06 20:38:44'),(50,15,306,52,'submitted',1,'2026-06-06 20:38:44'),(51,15,307,55,'submitted',1,'2026-06-06 20:38:44'),(52,15,400,74,'submitted',1,'2026-06-06 20:38:44'),(53,15,401,75,'submitted',1,'2026-06-06 20:38:44'),(54,15,402,76,'submitted',1,'2026-06-06 20:38:44'),(55,15,403,77,'submitted',1,'2026-06-06 20:38:44'),(56,15,404,78,'submitted',1,'2026-06-06 20:38:44'),(57,15,405,79,'submitted',1,'2026-06-06 20:38:44'),(58,15,406,80,'submitted',1,'2026-06-06 20:38:44'),(59,15,407,81,'submitted',1,'2026-06-06 20:38:44'),(60,15,408,82,'submitted',1,'2026-06-06 20:38:44'),(61,15,409,83,'submitted',1,'2026-06-06 20:38:44'),(62,5,306,52,'submitted',1,'2026-06-06 21:03:15'),(63,5,307,55,'submitted',1,'2026-06-06 21:03:15'),(64,5,400,74,'submitted',1,'2026-06-06 21:03:15'),(65,5,401,75,'submitted',1,'2026-06-06 21:03:15'),(66,5,402,76,'submitted',1,'2026-06-06 21:03:15'),(67,5,403,77,'submitted',1,'2026-06-06 21:03:15'),(68,5,404,78,'submitted',1,'2026-06-06 21:03:15'),(69,5,405,79,'submitted',1,'2026-06-06 21:03:15'),(70,5,406,80,'submitted',1,'2026-06-06 21:03:15'),(71,5,407,81,'submitted',1,'2026-06-06 21:03:15'),(72,5,408,82,'submitted',1,'2026-06-06 21:03:15'),(73,5,409,83,'submitted',1,'2026-06-06 21:03:15'),(75,19,400,NULL,'1',0,'2026-06-08 21:41:41');
/*!40000 ALTER TABLE `exam_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade`
--

DROP TABLE IF EXISTS `grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_record_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `total_score` decimal(6,2) NOT NULL,
  `objective_score` decimal(6,2) DEFAULT NULL,
  `subjective_score` decimal(6,2) DEFAULT NULL,
  `rank` int DEFAULT NULL,
  `percentile_rank` decimal(5,2) DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'published',
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_exam_record` (`exam_record_id`),
  UNIQUE KEY `idx_exam_student` (`exam_id`,`student_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_total_score` (`total_score`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grade`
--

LOCK TABLES `grade` WRITE;
/*!40000 ALTER TABLE `grade` DISABLE KEYS */;
INSERT INTO `grade` VALUES (1,1,1,201,62.00,62.00,0.00,1,NULL,'published','2025-06-01 15:00:00','2026-06-03 17:08:09','2026-06-03 17:08:09',0),(2,2,1,202,55.00,55.00,0.00,2,NULL,'published','2025-06-01 15:00:00','2026-06-03 17:08:09','2026-06-03 17:08:09',0),(3,3,1,203,48.00,48.00,0.00,3,NULL,'published','2025-06-01 15:00:00','2026-06-03 17:08:09','2026-06-03 17:08:09',0),(7,113,6,402,22.80,15.96,6.84,3,72.30,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(9,115,6,403,41.10,28.77,12.33,15,70.40,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(12,119,6,405,28.70,20.09,8.61,2,78.80,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(16,125,6,408,41.90,29.33,12.57,13,93.80,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(19,129,6,410,58.20,40.74,17.46,10,66.50,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(21,131,6,411,58.10,40.67,17.43,15,61.50,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(23,133,6,412,54.90,38.43,16.47,2,72.50,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(26,137,6,414,39.80,27.86,11.94,12,64.30,'published','2026-06-04 16:01:52','2026-06-05 16:01:52','2026-06-05 16:01:52',0),(29,141,6,416,18.60,13.02,5.58,30,65.10,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(31,143,6,417,53.50,37.45,16.05,22,80.10,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(34,147,6,420,30.10,21.07,9.03,4,95.00,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(37,151,6,422,59.40,41.58,17.82,8,99.50,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(40,155,6,424,42.10,29.47,12.63,27,74.80,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(42,157,6,425,28.20,19.74,8.46,26,66.00,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(44,159,6,428,21.70,15.19,6.51,21,89.20,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(46,161,6,429,30.40,21.28,9.12,9,83.40,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(48,163,6,418,54.00,37.80,16.20,24,73.20,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(50,165,6,426,44.20,30.94,13.26,4,77.10,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(52,167,6,427,18.00,12.60,5.40,17,63.50,'published','2026-06-04 16:03:31','2026-06-05 16:03:31','2026-06-05 16:03:31',0),(53,170,7,401,0.00,0.00,0.00,NULL,NULL,'published','2026-06-06 17:47:01','2026-06-06 17:47:00','2026-06-06 17:47:00',0),(54,171,8,400,3.00,0.00,0.00,NULL,NULL,'reviewed','2026-06-08 11:44:09','2026-06-06 18:12:00','2026-06-08 11:44:09',0),(55,172,8,401,5.00,5.00,0.00,NULL,NULL,'published','2026-06-06 18:16:00','2026-06-06 18:16:00','2026-06-06 18:16:00',0),(56,173,8,402,0.00,0.00,0.00,NULL,NULL,'published','2026-06-06 18:20:00','2026-06-06 18:20:00','2026-06-06 18:20:00',0),(57,174,8,403,5.00,5.00,0.00,NULL,NULL,'published','2026-06-06 18:24:00','2026-06-06 18:24:00','2026-06-06 18:24:00',0),(58,175,8,404,0.00,0.00,0.00,NULL,NULL,'published','2026-06-06 18:28:00','2026-06-06 18:28:00','2026-06-06 18:28:00',0),(59,176,8,405,0.00,0.00,0.00,NULL,NULL,'published','2026-06-06 18:32:00','2026-06-06 18:32:00','2026-06-06 18:32:00',0),(60,177,8,406,5.00,5.00,0.00,NULL,NULL,'published','2026-06-06 18:36:00','2026-06-06 18:36:00','2026-06-06 18:36:00',0),(73,190,14,306,72.00,72.00,0.00,NULL,NULL,'published','2026-03-10 14:55:00','2026-03-10 14:55:00','2026-03-10 14:55:00',0),(74,191,14,307,65.00,65.00,0.00,NULL,NULL,'published','2026-03-10 14:57:00','2026-03-10 14:57:00','2026-03-10 14:57:00',0),(75,192,14,400,53.00,53.00,0.00,NULL,NULL,'published','2026-03-10 14:59:00','2026-03-10 14:59:00','2026-03-10 14:59:00',0),(76,193,14,401,41.00,41.00,0.00,NULL,NULL,'published','2026-03-10 15:01:00','2026-03-10 15:01:00','2026-03-10 15:01:00',0),(77,194,14,402,35.00,35.00,0.00,NULL,NULL,'published','2026-03-10 15:03:00','2026-03-10 15:03:00','2026-03-10 15:03:00',0),(78,195,14,403,69.00,69.00,0.00,NULL,NULL,'published','2026-03-10 15:05:00','2026-03-10 15:05:00','2026-03-10 15:05:00',0),(79,196,14,404,60.00,60.00,0.00,NULL,NULL,'published','2026-03-10 15:07:00','2026-03-10 15:07:00','2026-03-10 15:07:00',0),(80,197,14,405,48.00,48.00,0.00,NULL,NULL,'published','2026-03-10 15:09:00','2026-03-10 15:09:00','2026-03-10 15:09:00',0),(81,198,14,406,44.00,44.00,0.00,NULL,NULL,'published','2026-03-10 15:11:00','2026-03-10 15:11:00','2026-03-10 15:11:00',0),(82,199,14,407,35.00,35.00,0.00,NULL,NULL,'published','2026-03-10 15:13:00','2026-03-10 15:13:00','2026-03-10 15:13:00',0),(83,200,14,408,56.00,56.00,0.00,NULL,NULL,'published','2026-03-10 15:15:00','2026-03-10 15:15:00','2026-03-10 15:15:00',0),(84,201,14,409,38.00,38.00,0.00,NULL,NULL,'published','2026-03-10 15:17:00','2026-03-10 15:17:00','2026-03-10 15:17:00',0),(85,202,15,306,84.00,84.00,0.00,NULL,NULL,'published','2026-04-15 14:55:00','2026-04-15 14:55:00','2026-04-15 14:55:00',0),(86,203,15,307,77.00,77.00,0.00,NULL,NULL,'published','2026-04-15 14:57:00','2026-04-15 14:57:00','2026-04-15 14:57:00',0),(87,204,15,400,67.00,66.00,0.00,NULL,NULL,'reviewed','2026-06-08 11:33:26','2026-04-15 14:59:00','2026-06-08 11:33:26',0),(88,205,15,401,54.00,54.00,0.00,NULL,NULL,'published','2026-04-15 15:01:00','2026-04-15 15:01:00','2026-04-15 15:01:00',0),(89,206,15,402,48.00,48.00,0.00,NULL,NULL,'published','2026-04-15 15:03:00','2026-04-15 15:03:00','2026-04-15 15:03:00',0),(90,207,15,403,81.00,81.00,0.00,NULL,NULL,'published','2026-04-15 15:05:00','2026-04-15 15:05:00','2026-04-15 15:05:00',0),(91,208,15,404,72.00,72.00,0.00,NULL,NULL,'published','2026-04-15 15:07:00','2026-04-15 15:07:00','2026-04-15 15:07:00',0),(92,209,15,405,60.00,60.00,0.00,NULL,NULL,'published','2026-04-15 15:09:00','2026-04-15 15:09:00','2026-04-15 15:09:00',0),(93,210,15,406,56.00,56.00,0.00,NULL,NULL,'published','2026-04-15 15:11:00','2026-04-15 15:11:00','2026-04-15 15:11:00',0),(94,211,15,407,43.00,43.00,0.00,NULL,NULL,'published','2026-04-15 15:13:00','2026-04-15 15:13:00','2026-04-15 15:13:00',0),(95,212,15,408,68.00,68.00,0.00,NULL,NULL,'published','2026-04-15 15:15:00','2026-04-15 15:15:00','2026-04-15 15:15:00',0),(96,213,15,409,50.00,50.00,0.00,NULL,NULL,'published','2026-04-15 15:17:00','2026-04-15 15:17:00','2026-04-15 15:17:00',0),(97,214,5,306,95.00,78.00,17.00,NULL,NULL,'published','2026-06-05 21:08:00','2026-06-05 21:08:00','2026-06-05 21:08:00',0),(98,215,5,307,88.00,69.00,19.00,NULL,NULL,'published','2026-06-05 21:11:00','2026-06-05 21:11:00','2026-06-05 21:11:00',0),(99,216,5,400,76.00,57.00,19.00,NULL,NULL,'published','2026-06-05 21:14:00','2026-06-05 21:14:00','2026-06-05 21:14:00',0),(100,217,5,401,64.00,48.00,16.00,NULL,NULL,'published','2026-06-05 21:17:00','2026-06-05 21:17:00','2026-06-05 21:17:00',0),(101,218,5,402,58.00,43.00,15.00,NULL,NULL,'published','2026-06-05 21:20:00','2026-06-05 21:20:00','2026-06-05 21:20:00',0),(102,219,5,403,92.00,76.00,16.00,NULL,NULL,'published','2026-06-05 21:23:00','2026-06-05 21:23:00','2026-06-05 21:23:00',0),(103,220,5,404,83.00,64.00,19.00,NULL,NULL,'published','2026-06-05 21:26:00','2026-06-05 21:26:00','2026-06-05 21:26:00',0),(104,221,5,405,71.00,52.00,19.00,NULL,NULL,'published','2026-06-05 21:29:00','2026-06-05 21:29:00','2026-06-05 21:29:00',0),(105,222,5,406,67.00,51.00,16.00,NULL,NULL,'published','2026-06-05 21:32:00','2026-06-05 21:32:00','2026-06-05 21:32:00',0),(106,223,5,407,54.00,40.00,14.00,NULL,NULL,'published','2026-06-05 21:35:00','2026-06-05 21:35:00','2026-06-05 21:35:00',0),(107,224,5,408,79.00,59.00,20.00,NULL,NULL,'published','2026-06-05 21:38:00','2026-06-05 21:38:00','2026-06-05 21:38:00',0),(108,225,5,409,61.00,46.00,15.00,NULL,NULL,'published','2026-06-05 21:41:00','2026-06-05 21:41:00','2026-06-05 21:41:00',0),(109,227,16,103,0.00,0.00,0.00,NULL,NULL,'published','2026-06-08 11:40:05','2026-06-07 17:31:00','2026-06-09 16:23:22',1),(110,233,6,201,28.00,10.00,18.00,NULL,NULL,'published','2026-06-08 09:51:30','2026-06-08 09:51:18','2026-06-08 09:51:29',0),(111,228,17,103,0.00,0.00,0.00,NULL,NULL,'published','2026-06-08 09:53:12','2026-06-08 09:53:12','2026-06-09 16:23:22',1),(112,232,17,400,0.00,0.00,0.00,NULL,NULL,'published','2026-06-08 09:53:12','2026-06-08 09:53:12','2026-06-08 09:53:12',0),(113,226,16,400,0.00,0.00,0.00,NULL,NULL,'published','2026-06-08 11:40:05','2026-06-08 11:24:09','2026-06-08 11:40:05',0),(114,168,7,400,1.00,0.00,0.00,NULL,NULL,'reviewed','2026-06-08 11:39:10','2026-06-08 11:25:22','2026-06-08 11:39:10',0),(116,235,19,400,12.00,12.00,0.00,1,100.00,'published','2026-06-01 10:00:00','2026-06-08 21:41:41','2026-06-08 21:41:41',0),(117,236,20,103,20.00,20.00,0.00,NULL,NULL,'published','2026-06-09 15:45:44','2026-06-09 15:45:43','2026-06-09 16:23:22',1),(118,237,21,400,15.00,15.00,0.00,NULL,NULL,'published','2026-06-09 15:52:50','2026-06-09 15:52:50','2026-06-09 15:52:50',0),(119,238,22,400,20.00,10.00,0.00,NULL,NULL,'reviewed','2026-06-09 15:58:05','2026-06-09 15:54:46','2026-06-09 15:58:05',0);
/*!40000 ALTER TABLE `grade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade_review`
--

DROP TABLE IF EXISTS `grade_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `grade_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `question_id` bigint DEFAULT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `teacher_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `original_score` decimal(6,2) DEFAULT NULL,
  `new_score` decimal(6,2) DEFAULT NULL,
  `review_teacher_id` bigint DEFAULT NULL,
  `review_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_grade_id` (`grade_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grade_review`
--

LOCK TABLES `grade_review` WRITE;
/*!40000 ALTER TABLE `grade_review` DISABLE KEYS */;
INSERT INTO `grade_review` VALUES (1,1,1,201,NULL,'TCP三次握手涉及序列号同步',NULL,'pending',NULL,8.00,NULL,NULL,NULL,'2026-06-04 11:21:39','2026-06-04 11:21:39'),(2,1,1,201,NULL,'觉得简答题评分标准过严，答案要点基本覆盖了','我查阅了教材第5章相关内容，认为我的回答已经涵盖了主要知识点','pending',NULL,8.00,NULL,NULL,NULL,'2026-06-04 11:32:14','2026-06-04 11:32:14'),(3,2,1,202,18,'第18题答案有争议，我认为B选项也是正确的','请老师重新评估B选项的合理性','pending',NULL,5.00,NULL,NULL,NULL,'2026-06-04 11:32:14','2026-06-04 11:32:14'),(4,3,1,203,NULL,'分数计算有误，客观题得分与实际不符',NULL,'pending',NULL,6.00,NULL,NULL,NULL,'2026-06-04 11:32:14','2026-06-04 11:32:14'),(5,4,1,204,20,'第20题评分过严','经复核确实存在评分偏严','approved',NULL,7.00,8.50,101,'2026-06-03 11:32:14','2026-06-02 11:32:14','2026-06-04 11:32:14'),(6,2,1,202,18,'第18题答案有争议，我认为B选项也是正确的','请老师重新评估B选项的合理性','approved','不行',5.00,5.00,103,'2026-06-05 09:44:34','2026-06-04 11:32:28','2026-06-05 09:44:33'),(7,3,1,203,NULL,'分数计算有误，客观题得分与实际不符',NULL,'approved','复核已处理',6.00,85.00,1,'2026-06-05 09:07:56','2026-06-04 11:32:36','2026-06-08 21:55:17'),(8,4,1,204,20,'第20题评分过严','经复核确实存在评分偏严','approved',NULL,7.00,8.50,101,'2026-06-03 11:32:43','2026-06-02 11:32:43','2026-06-04 11:32:43'),(9,55,8,400,NULL,'申请复核：请核对成绩与答题详情是否一致',NULL,'approved',NULL,0.00,NULL,103,'2026-06-06 18:01:35','2026-06-06 18:01:21','2026-06-08 21:55:17'),(10,97,5,306,48,'主观题要点基本覆盖，建议复核是否可补给过程分。','答案中写出了核心区别，但可能表述不够完整。','pending',NULL,95.00,NULL,NULL,NULL,'2026-06-08 10:34:08','2026-06-08 10:38:14'),(11,98,5,307,48,'总分与答题详情显示不一致，请老师核对本题得分。','报告页显示的单题分数与预期不同。','pending',NULL,88.00,NULL,NULL,NULL,'2026-06-08 10:26:08','2026-06-08 10:38:14'),(12,99,5,400,48,'申请重新计算客观题分数。','怀疑系统判分有误。','rejected','经复核，客观题答案与标准答案不一致，维持原分。',76.00,NULL,103,'2026-06-08 10:09:08','2026-06-08 10:18:08','2026-06-08 10:38:14'),(13,100,5,401,48,'简答题评分偏严，申请补充人工复核。','答案包含查询效率与增删效率两个维度。','approved','复核通过，答案覆盖主要要点，适当补分。',64.00,19.00,103,'2026-06-08 10:19:08','2026-06-08 10:10:08','2026-06-08 10:38:14'),(14,110,6,400,46,'自动化联调复核申请 2026-06-08T03-17-12-734Z：核查本题得分与参考答案匹配情况。',NULL,'pending',NULL,28.00,NULL,NULL,NULL,'2026-06-08 11:17:12','2026-06-08 11:17:12'),(15,112,17,400,NULL,'自动化联调复核申请 2026-06-08T03-20-04-305Z：核查得分与参考答案匹配情况。',NULL,'pending',NULL,0.00,NULL,NULL,NULL,'2026-06-08 11:20:04','2026-06-08 11:20:04'),(16,87,15,400,NULL,'闭环测试复核申请 2026-06-08T03:33:26.303Z',NULL,'approved','自动化闭环测试：同意复核并调整总分。',66.00,67.00,103,'2026-06-08 11:33:26','2026-06-08 11:33:26','2026-06-08 11:33:26'),(17,114,7,400,NULL,'闭环测试复核申请 2026-06-08T03:39:09.914Z',NULL,'approved','自动化闭环测试：同意复核并调整总分。',0.00,1.00,103,'2026-06-08 11:39:10','2026-06-08 11:39:09','2026-06-08 11:39:10'),(18,113,16,400,NULL,'申请复核：请核对成绩与答题详情是否一致',NULL,'rejected','复核已处理',0.00,NULL,103,'2026-06-08 11:40:06','2026-06-08 11:40:05','2026-06-08 21:55:17'),(19,54,8,400,NULL,'我想要5分.',NULL,'approved','复核通过，分数已调整。',0.00,3.00,103,'2026-06-08 11:44:09','2026-06-08 11:43:43','2026-06-08 11:44:09'),(20,119,22,400,NULL,'老师球球了再给10分吧。。。。',NULL,'approved','彳亍',10.00,20.00,103,'2026-06-09 15:58:05','2026-06-09 15:57:50','2026-06-09 15:58:05');
/*!40000 ALTER TABLE `grade_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grading_task`
--

DROP TABLE IF EXISTS `grading_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grading_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_record_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `teacher_id` bigint DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `score` decimal(6,2) DEFAULT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `grading_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grading_task`
--

LOCK TABLES `grading_task` WRITE;
/*!40000 ALTER TABLE `grading_task` DISABLE KEYS */;
INSERT INTO `grading_task` VALUES (1,1,18,201,103,'completed',1.00,'','2026-06-05 09:44:43','2026-06-04 11:21:43','2026-06-05 09:44:43'),(2,1,18,201,103,'completed',1.00,'','2026-06-05 09:44:55','2026-06-04 11:32:20','2026-06-05 09:44:54'),(3,1,19,201,103,'completed',0.00,'','2026-06-05 09:45:42','2026-06-04 11:32:20','2026-06-05 09:45:42'),(4,2,18,202,103,'completed',0.00,'','2026-06-05 09:45:51','2026-06-04 11:32:20','2026-06-05 09:45:50'),(5,2,19,202,NULL,'pending',NULL,NULL,NULL,'2026-06-04 11:32:20','2026-06-04 11:32:20'),(6,3,18,203,NULL,'pending',NULL,NULL,NULL,'2026-06-04 11:32:20','2026-06-04 11:32:20'),(7,1,20,201,101,'grading',NULL,NULL,NULL,'2026-06-04 11:32:20','2026-06-04 11:32:20'),(8,1,21,201,101,'completed',9.00,'回答完整，思路清晰','2026-06-03 11:32:20','2026-06-03 11:32:20','2026-06-04 11:32:20'),(9,1,18,201,NULL,'pending',NULL,NULL,NULL,'2026-06-04 11:33:12','2026-06-04 11:33:12'),(10,1,20,201,101,'grading',NULL,NULL,NULL,'2026-06-04 11:33:19','2026-06-04 11:33:19'),(11,1,21,201,101,'completed',9.00,'回答完整，思路清晰','2026-06-03 11:33:25','2026-06-03 11:33:25','2026-06-04 11:33:25'),(12,1,19,201,NULL,'pending',NULL,NULL,NULL,'2026-06-04 11:33:33','2026-06-04 11:33:33'),(13,1,18,201,NULL,'pending',NULL,NULL,NULL,'2026-06-04 11:33:35','2026-06-04 11:33:35'),(14,101,0,306,103,'completed',1.00,'','2026-06-05 15:31:45','2026-01-15 10:00:00','2026-06-05 15:31:44'),(15,102,0,307,103,'completed',1.00,'','2026-06-08 08:44:01','2026-01-15 10:00:00','2026-06-08 08:44:00'),(16,103,0,306,103,'pending',NULL,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00'),(17,104,0,308,103,'pending',NULL,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00'),(18,1,10,400,103,'completed',5.50,NULL,NULL,'2026-06-05 15:20:03','2026-06-05 15:20:03'),(19,2,10,401,103,'completed',7.00,NULL,NULL,'2026-06-05 15:20:03','2026-06-05 15:20:03'),(20,3,10,402,103,'completed',4.50,NULL,NULL,'2026-06-05 15:20:03','2026-06-05 15:20:03'),(21,4,10,403,103,'completed',6.00,NULL,NULL,'2026-06-05 15:20:03','2026-06-05 15:20:03'),(22,5,10,404,103,'completed',8.00,NULL,NULL,'2026-06-05 15:20:03','2026-06-05 15:20:03'),(23,6,10,405,103,'completed',NULL,'批改意见：答案要点已核对',NULL,'2026-06-05 15:20:03','2026-06-08 21:55:17'),(24,7,10,406,103,'completed',NULL,'批改意见：答案要点已核对',NULL,'2026-06-05 15:20:03','2026-06-08 21:55:17'),(25,8,10,407,103,'completed',NULL,'批改意见：答案要点已核对',NULL,'2026-06-05 15:20:03','2026-06-08 21:55:17'),(26,9,10,408,103,'completed',NULL,'批改意见：答案要点已核对',NULL,'2026-06-05 15:20:03','2026-06-08 21:55:17'),(27,10,10,409,103,'completed',NULL,'批改意见：答案要点已核对',NULL,'2026-06-05 15:20:03','2026-06-08 21:55:17'),(28,233,48,201,103,'completed',18.00,'测试批改通过','2026-06-08 09:51:30','2026-06-08 09:51:18','2026-06-08 09:51:29');
/*!40000 ALTER TABLE `grading_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_log`
--

DROP TABLE IF EXISTS `login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint NOT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `error_message` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_log`
--

LOCK TABLES `login_log` WRITE;
/*!40000 ALTER TABLE `login_log` DISABLE KEYS */;
INSERT INTO `login_log` VALUES (1,1,'admin',1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120','password',NULL,'2025-06-01 08:00:00'),(2,101,'zhanglaoshi',1,'192.168.1.10','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120','password',NULL,'2025-06-01 08:30:00'),(3,201,'xiaoming',1,'192.168.1.100','Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120','password',NULL,'2025-06-01 13:55:00'),(4,202,'xiaohong',1,'192.168.1.101','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605','password',NULL,'2025-06-01 13:56:00'),(5,203,'xiaogang',1,'192.168.1.102','Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)','password',NULL,'2025-06-01 13:57:00'),(6,999,'unknown_user',0,'10.0.0.50','Mozilla/5.0 (compatible; Bot/1.0)','password',NULL,'2025-06-01 09:00:00'),(7,1,'admin',1,'127.0.0.1','node','password',NULL,'2026-06-08 23:39:03'),(8,103,'wanglaoshi',1,'127.0.0.1','node','password',NULL,'2026-06-08 23:39:03'),(9,400,'stu_001',1,'127.0.0.1','node','password',NULL,'2026-06-08 23:39:03'),(10,1,'admin',1,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-08 23:39:34'),(11,1,'admin',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:08:22'),(12,1,'admin',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:08:23'),(13,1,'admin',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:08:23'),(14,1,'admin',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:08:48'),(15,103,'wanglaoshi',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:08:48'),(16,400,'stu_001',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:08:48'),(17,103,'wanglaoshi',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:09:07'),(18,103,'wanglaoshi',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:09:24'),(19,1,'admin',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 00:09:53'),(20,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 00:10:18'),(21,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 00:10:37'),(22,1,'admin',1,'171.15.18.98','node','password',NULL,'2026-06-09 00:12:10'),(23,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 00:13:19'),(24,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 00:13:45'),(25,400,'stu_001',0,'171.15.18.3','Mozilla/5.0 (Linux; Android 14; 23043RP34C Build/UKQ1.240624.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/146.0.7680.178 Safari/537.36 XWEB/1460149 MMWEBSDK/20260502 MMWEBID/1953 REV/4843ebe11ecd759196310a3df45633a796c09f5b MicroMessenger/8.0.72.3100(0x28004850) WeChat/arm64 Weixin Android Tablet NetType/WIFI Language/zh_CN ABI/arm64','password','用户名或密码错误','2026-06-09 00:16:03'),(26,400,'stu_001',1,'171.15.18.3','Mozilla/5.0 (Linux; Android 14; 23043RP34C Build/UKQ1.240624.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/146.0.7680.178 Safari/537.36 XWEB/1460149 MMWEBSDK/20260502 MMWEBID/1953 REV/4843ebe11ecd759196310a3df45633a796c09f5b MicroMessenger/8.0.72.3100(0x28004850) WeChat/arm64 Weixin Android Tablet NetType/WIFI Language/zh_CN ABI/arm64','password',NULL,'2026-06-09 00:16:12'),(27,103,'wanglaoshi',1,'171.15.18.3','Mozilla/5.0 (Linux; Android 14; 23043RP34C Build/UKQ1.240624.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/146.0.7680.178 Safari/537.36 XWEB/1460149 MMWEBSDK/20260502 MMWEBID/1953 REV/4843ebe11ecd759196310a3df45633a796c09f5b MicroMessenger/8.0.72.3100(0x28004850) WeChat/arm64 Weixin Android Tablet NetType/WIFI Language/zh_CN ABI/arm64','password',NULL,'2026-06-09 00:17:33'),(28,400,'stu_001',1,'171.15.18.3','Mozilla/5.0 (Linux; Android 14; 23043RP34C Build/UKQ1.240624.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/146.0.7680.178 Safari/537.36 XWEB/1460149 MMWEBSDK/20260502 MMWEBID/1953 REV/4843ebe11ecd759196310a3df45633a796c09f5b MicroMessenger/8.0.72.3100(0x28004850) WeChat/arm64 Weixin Android Tablet NetType/WIFI Language/zh_CN ABI/arm64','password',NULL,'2026-06-09 00:19:39'),(29,1,'admin',1,'171.15.18.3','Mozilla/5.0 (Linux; Android 14; 23043RP34C Build/UKQ1.240624.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/146.0.7680.178 Safari/537.36 XWEB/1460149 MMWEBSDK/20260502 MMWEBID/1953 REV/4843ebe11ecd759196310a3df45633a796c09f5b MicroMessenger/8.0.72.3100(0x28004850) WeChat/arm64 Weixin Android Tablet NetType/WIFI Language/zh_CN ABI/arm64','password',NULL,'2026-06-09 00:21:29'),(30,207,'teacher',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 08:36:48'),(31,103,'wanglaoshi',1,'115.60.17.138','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 08:37:48'),(32,400,'stu_001',1,'115.60.17.138','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 08:41:16'),(33,1,'admin',1,'171.15.18.3','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 08:41:22'),(34,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 08:43:24'),(35,103,'wanglaoshi',1,'117.158.213.174','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36 QuarkPC/4.6.5.580','password',NULL,'2026-06-09 08:44:20'),(36,103,'wanglaoshi',1,'171.15.18.3','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 08:48:48'),(37,1,'admin',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 09:08:22'),(38,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 09:09:51'),(39,207,'teacher',0,'171.15.18.187','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 NetType/WIFI MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x63090a13) UnifiedPCWindowsWechat(0xf2541a1b) XWEB/19895 Flue','password','用户名或密码错误','2026-06-09 09:26:51'),(40,103,'wanglaoshi',1,'171.15.18.187','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 NetType/WIFI MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x63090a13) UnifiedPCWindowsWechat(0xf2541a1b) XWEB/19895 Flue','password',NULL,'2026-06-09 09:27:26'),(41,400,'stu_001',1,'115.60.17.138','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 09:38:49'),(42,1,'admin',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 15:35:53'),(43,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 15:40:19'),(44,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 15:43:52'),(45,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 15:47:03'),(46,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0','password',NULL,'2026-06-09 15:49:05'),(47,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 15:49:49'),(48,1,'admin',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:07:29'),(49,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:07:45'),(50,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:08:04'),(51,1,'admin',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:10:36'),(52,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:10:51'),(53,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:11:14'),(54,1,'admin',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:24:23'),(55,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:24:43'),(56,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:25:06'),(57,1,'admin',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:29:16'),(58,103,'wanglaoshi',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:29:39'),(59,400,'stu_001',1,'171.15.18.98','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/148.0.0.0 Safari/537.36','password',NULL,'2026-06-09 16:30:06');
/*!40000 ALTER TABLE `login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `related_id` bigint DEFAULT NULL,
  `is_read` tinyint NOT NULL DEFAULT '0',
  `read_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,201,'grade','计算机网络第一章测验成绩已发布','您在\"计算机网络第一章测验\"中的成绩已发布，请登录查看。',1,0,NULL,'2025-06-01 15:00:00'),(2,202,'grade','计算机网络第一章测验成绩已发布','您在\"计算机网络第一章测验\"中的成绩已发布，请登录查看。',1,0,NULL,'2025-06-01 15:00:00'),(3,203,'grade','计算机网络第一章测验成绩已发布','您在\"计算机网络第一章测验\"中的成绩已发布，请登录查看。本次成绩未达到及格线，请认真复习后参加补考。',1,0,NULL,'2025-06-01 15:00:00'),(4,204,'exam','新考试：计算机网络期中考试已安排','新的期中考试已安排，请及时复习备考。考试时间：下周同一时间。',2,0,NULL,'2025-06-02 09:00:00'),(5,205,'exam','新考试：计算机网络期中考试已安排','新的期中考试已安排，请及时复习备考。考试时间：下周同一时间。',2,0,NULL,'2025-06-02 09:00:00'),(6,206,'exam','新考试：计算机网络期中考试已安排','新的期中考试已安排，请及时复习备考。考试时间：下周同一时间。',2,0,NULL,'2025-06-02 09:00:00'),(7,201,'system','欢迎加入计算机网络课程','欢迎加入《计算机网络》课程！加课码：CS2024NET。请按时完成课程学习和测验。',NULL,1,NULL,'2025-03-01 09:00:00'),(8,202,'system','欢迎加入计算机网络课程','欢迎加入《计算机网络》课程！加课码：CS2024NET。请按时完成课程学习和测验。',NULL,1,NULL,'2025-03-01 09:05:00'),(9,203,'system','欢迎加入计算机网络课程','欢迎加入《计算机网络》课程！加课码：CS2024NET。请按时完成课程学习和测验。',NULL,1,NULL,'2025-03-01 09:10:00'),(10,204,'system','欢迎加入计算机网络课程','欢迎加入《计算机网络》课程！加课码：CS2024NET。请按时完成课程学习和测验。',NULL,1,NULL,'2025-03-01 09:15:00'),(11,205,'system','欢迎加入计算机网络课程','欢迎加入《计算机网络》课程！加课码：CS2024NET。请按时完成课程学习和测验。',NULL,1,NULL,'2025-03-01 09:20:00'),(12,206,'system','欢迎加入计算机网络课程','欢迎加入《计算机网络》课程！加课码：CS2024NET。请按时完成课程学习和测验。',NULL,1,NULL,'2025-03-01 09:25:00');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `online_user`
--

DROP TABLE IF EXISTS `online_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `online_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_time` datetime NOT NULL,
  `last_active_time` datetime DEFAULT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_last_active` (`last_active_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `online_user`
--

LOCK TABLES `online_user` WRITE;
/*!40000 ALTER TABLE `online_user` DISABLE KEYS */;
INSERT INTO `online_user` VALUES (1,1,'admin','admin','eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiYWRtaW4iLCJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJzdWIiOiIxIiwiaWF0IjoxNzgwOTkzNzU2LCJleHAiOjE3ODEwODAxNTZ9.vr4PVfuGRLj8EWPVE0qtCT838o4fstMr8J_ERvYwRaWDRdlgR8FKFZW06qIW5gfo','2026-06-09 16:29:16','2026-06-09 16:29:26','171.15.18.98',1,'2026-06-08 23:39:03'),(2,103,'wanglaoshi','teacher','eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoidGVhY2hlciIsInVzZXJJZCI6MTAzLCJ1c2VybmFtZSI6IndhbmdsYW9zaGkiLCJzdWIiOiIxMDMiLCJpYXQiOjE3ODA5OTM3NzksImV4cCI6MTc4MTA4MDE3OX0.Kr7SjUk5KreFQIAcR7N-NPDxF8XppTQVS9BHbJRZl1R7GeZORO9TmYoWFkN1OUZk','2026-06-09 16:29:40','2026-06-09 16:29:53','171.15.18.98',1,'2026-06-08 23:39:03'),(3,400,'stu_001','student','eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoic3R1ZGVudCIsInVzZXJJZCI6NDAwLCJ1c2VybmFtZSI6InN0dV8wMDEiLCJzdWIiOiI0MDAiLCJpYXQiOjE3ODA5OTM4MDYsImV4cCI6MTc4MTA4MDIwNn0.t3TUlC2JDP9URxRqhfjzqdnNGNyjZrIeaDnElq3DHw4upY3df2ur7pGZd8dlrB3O','2026-06-09 16:30:07','2026-06-09 16:30:16','171.15.18.98',1,'2026-06-08 23:39:04'),(4,207,'teacher','teacher','eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoidGVhY2hlciIsInVzZXJJZCI6MjA3LCJ1c2VybmFtZSI6InRlYWNoZXIiLCJzdWIiOiIyMDciLCJpYXQiOjE3ODA5NjU0MDgsImV4cCI6MTc4MTA1MTgwOH0.kMJTmNwZly8d1S0zrNTheJxMch0RbguTvw2M_Bj_R89da5_EowLe9oPwkAaS7Wj5','2026-06-09 08:36:48','2026-06-09 08:39:33','171.15.18.98',1,'2026-06-09 08:36:48');
/*!40000 ALTER TABLE `online_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint DEFAULT NULL,
  `teacher_id` bigint NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `difficulty` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'medium',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `score` decimal(5,2) NOT NULL DEFAULT '5.00',
  `knowledge_points` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chapter` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usage_count` int NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '1',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source_question_id` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_type` (`type`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,1,101,'single_choice','easy','计算机网络的主要功能有（）、数据传输和进行分布处理。','A','无',5.00,'计算机网络概述','第1章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(2,1,101,'single_choice','easy','计算机网络的目的是（）','C','无',5.00,'计算机网络概述','第1章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(3,1,101,'single_choice','easy','计算机网络可被理解为()。','C','无',5.00,'计算机网络概述','第1章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(4,1,101,'single_choice','medium','在计算机网络中，所有的计算机均连接到一条通信传输线路上，在线路两端连有防止信号反射的装置。这种连接结构被称为（）','A','无',5.00,'局域网技术','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(5,1,101,'single_choice','medium','当描述一个物理层接口引脚处于高电平时的含义时，该描述属于（）','C','无',5.00,'物理层接口特性','第2章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(6,1,101,'single_choice','easy','采用全双工通信方式，数据传输的方向为（）','A','无',5.00,'数据通信方式','第2章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(7,1,101,'single_choice','easy','半双工通信方式的数据传输的方向为（）','C','无',5.00,'数据通信方式','第2章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(8,1,101,'single_choice','medium','在物理层接口特性中，用于描述完成每种功能的事件发生顺序的是（）','C','无',5.00,'物理层接口特性','第2章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(9,1,101,'single_choice','hard','能从数据信号波形中提取同步信号的典型编码是（）','A','无',5.00,'数字编码','第2章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(10,1,101,'single_choice','easy','PPP协议是哪一层的协议（）','B','无',5.00,'PPP协议','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(11,1,101,'single_choice','easy','在以太网中，是根据（）地址来区分不同的设备的','B','无',5.00,'以太网协议','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(12,1,101,'single_choice','medium','以太网交换机的每一个端口可以看做一个（）','A','无',5.00,'交换机原理','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(13,1,101,'single_choice','easy','集线器是工作在（）','A','无',5.00,'集线器与交换机','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(14,1,101,'single_choice','easy','将传输比特流划分为帧，应属于下列OSI的（）处理','B','无',5.00,'帧封装','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(15,1,101,'single_choice','medium','下面关于CSMA/CD网络的叙述，（）是正确的','A','无',5.00,'CSMA/CD','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(16,1,101,'single_choice','easy','星形、总线形、环形和网状形是遵照的分类标准是（）','D','无',5.00,'网络拓扑','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(17,1,101,'single_choice','easy','就交换技术而言，局域网中的以太网采用的是（）','A','无',5.00,'交换技术','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(18,1,101,'single_choice','hard','假如收到1000000000个码元，经检查有一个码元出错，则误码率为（）','D','无',5.00,'差错控制','第3章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(19,1,101,'single_choice','medium','TCP的特点表述错误的是()','C','无',5.00,'TCP特点','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(20,1,101,'multi_choice','medium','TCP协议的主要功能包括()','B,C,D','无',8.00,'TCP功能','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(21,1,101,'multi_choice','hard','关于TCP报头格式正确的表述是（）','A,B,C','无',8.00,'TCP报头格式','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(22,1,101,'multi_choice','medium','以下关于TCP和UDP协议的描述中，错误的是()','A,B,D','无',8.00,'TCPvsUDP','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(23,1,101,'fill_blank','easy','分组交换采用____________技术。','存储转发','无',5.00,'分组交换','第1章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(24,1,101,'fill_blank','easy','OSI参考模型采用了______层的体系结构。','7','无',5.00,'OSI模型','第1章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(25,1,101,'fill_blank','medium','拥塞控制又分为________控制和________控制。','闭环|开环','无',5.00,'拥塞控制','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(26,1,101,'fill_blank','medium','拥塞控制算法有四种，分别是：慢开始、________、快重传、________。','拥塞避免|快恢复','无',5.00,'拥塞控制算法','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(27,1,101,'fill_blank','medium','只要出现________，就可以估计可能在网络某处出现了拥塞。','超时','无',5.00,'拥塞检测','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(28,1,101,'fill_blank','medium','当拥塞窗口cwnd超过慢开始门限ssthresh的时候，就开始执行________算法','拥塞避免','无',5.00,'拥塞避免','第4章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(29,1,101,'fill_blank','easy','一个电子邮件系统应具备三个主要组成构件，这就是________、________，以及邮件发送协议SMTP和邮件读取协议POP3。','用户代理|邮件服务器','无',5.00,'电子邮件系统','第5章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(30,1,101,'fill_blank','easy','WWW服务默认端口为________，其访问协议为________。','80|http','无',5.00,'WWW服务','第5章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(31,1,101,'fill_blank','easy','SNMP使用________传送报文。','UDP','无',5.00,'SNMP协议','第5章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(32,1,101,'fill_blank','easy','URL主要由四部分组成，分别是：协议、（）、（）和（）。','主机名|端口|路径','无',5.00,'URL组成','第5章',0,1,NULL,NULL,'2026-06-03 17:08:08','2026-06-03 17:08:08',0),(33,1,101,'single_choice','easy','在TCP/IP体系结构中，负责路由选择和分组转发的通常是哪个层次？','A','网络层负责寻址、路由与分组转发。',5.00,'网络层',NULL,0,1,NULL,NULL,'2026-06-04 15:46:37','2026-06-07 00:52:31',0),(34,1,101,'single_choice','easy','用于把域名解析为IP地址的协议通常是哪个？','A','DNS负责域名解析。',5.00,'应用层协议',NULL,0,1,NULL,NULL,'2026-06-04 15:51:17','2026-06-07 00:52:31',1),(35,1,101,'single_choice','easy','TCP协议最突出的特点是？','A','TCP是面向连接且提供可靠传输的协议。',5.00,'传输层',NULL,0,1,NULL,NULL,'2026-06-04 15:54:35','2026-06-07 00:52:31',1),(36,1,101,'single_choice','easy','交换机在局域网中主要依据什么地址转发数据帧？','A','交换机主要依据MAC地址转发帧。',5.00,'数据链路层',NULL,0,1,NULL,NULL,'2026-06-04 15:58:04','2026-06-07 00:52:31',1),(37,1,101,'single_choice','easy','双绞线、光纤等传输介质主要属于OSI模型的哪一层相关内容？','A','传输介质属于物理层研究范围。',5.00,'物理层',NULL,0,1,NULL,NULL,'2026-06-04 16:00:52','2026-06-07 00:52:31',1),(38,1,101,'single_choice','easy','IPv4地址一共由多少位二进制组成？','A','IPv4地址由32位二进制组成。',5.00,'IP地址',NULL,0,1,NULL,NULL,'2026-06-04 16:06:52','2026-06-07 00:52:31',1),(39,1,101,'single_choice','easy','防火墙的主要作用之一是？','A','防火墙用于控制网络访问、隔离风险。',5.00,'网络安全',NULL,0,1,NULL,NULL,'2026-06-04 16:14:14','2026-06-07 00:52:31',1),(40,11,103,'single_choice','medium','HTML中用于定义超链接的标签是哪个？','A','<a>标签是HTML中创建超链接的标准标签',5.00,'HTML基础','第一章',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(41,11,103,'single_choice','medium','CSS中设置元素背景颜色的属性是？','A','background-color属性控制背景颜色',5.00,'CSS基础','第二章',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(42,11,103,'single_choice','easy','Vue.js中用于双向数据绑定的指令是？','A','v-model实现表单元素与数据的双向绑定',5.00,'Vue基础','第三章',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(43,12,103,'single_choice','easy','Java中main方法的返回类型是什么？','A','JVM规范要求main方法返回void',5.00,'Java基础','第一章',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(44,12,103,'single_choice','medium','以下哪个不是Java的基本数据类型？','B','String是引用类型，其余int/double/boolean都是基本类型',5.00,'数据类型','第一章',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(45,12,103,'single_choice','medium','ArrayList和LinkedList的主要区别？','A','ArrayList底层数组查询快增删慢；LinkedList底层链表反之',5.00,'集合框架','第五章',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(46,13,103,'true_false','easy','Java源文件编译后会生成对应的.class文件。','true','Java代码经过javac编译后，会生成字节码文件.class。',10.00,'Java基础','Java基础',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-06-08 22:39:51',0),(47,13,103,'fill_blank','medium','在Java中，定义变量 age 并赋值18，代码应写为：int ___ = 18。','age','变量声明格式为“类型 变量名 = 值”，因此应填 age。',10.00,'变量定义','变量与数据类型',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-06-08 22:39:51',0),(48,13,103,'essay','hard','请简述 ArrayList 和 LinkedList 的主要区别。','ArrayList底层是数组，查询快、尾部追加效率高；LinkedList底层是链表，插入删除更灵活。','两者底层结构不同，因此在查询、插入、删除场景下各有优势。',20.00,'集合框架','集合框架',0,1,NULL,NULL,'2026-01-15 10:00:00','2026-06-08 22:39:51',0),(49,11,103,'single','easy','42424',NULL,'4242',2.00,'Vue基础',NULL,0,1,NULL,NULL,'2026-06-05 15:49:08','2026-06-05 15:49:08',0),(50,14,208,'single','easy','242',NULL,'4242',2.00,'多线程,网络编程',NULL,0,1,NULL,NULL,'2026-06-05 15:52:00','2026-06-05 15:52:00',0),(51,15,103,'single_choice','easy','课程15联调样题：2 + 2 = ?','A','2 + 2 = 4',5.00,'基础测试',NULL,0,1,NULL,NULL,'2026-06-06 17:50:19','2026-06-06 19:11:16',0),(82,17,103,'single_choice','easy','在分层网络体系结构中，某一层向上一层提供服务主要依赖的是（ ）。','B','分层模型中，下层通过接口向上层提供服务；协议主要规定同层实体之间通信规则。',5.00,'协议分层,体系结构','第一章 网络体系结构',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(83,17,103,'single_choice','easy','在 TCP/IP 体系结构中，IP 协议主要工作在（ ）。','C','IP 协议负责逻辑寻址与分组转发，属于 TCP/IP 模型中的网际层。',5.00,'TCP/IP模型,网际层','第一章 网络体系结构',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(84,17,103,'single_choice','medium','以太网交换机转发帧时，通常依据帧中的（ ）进行转发决策。','A','交换机维护 MAC 地址表，并根据目的 MAC 地址选择转发端口。',5.00,'交换机,MAC地址','第三章 数据链路层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(85,17,103,'single_choice','medium','某主机 IP 地址为 192.168.1.130，子网掩码为 255.255.255.128，则该主机所在子网的网络地址是（ ）。','C','/25 子网每段 128 个地址，192.168.1.130 属于 192.168.1.128/25。',5.00,'IP地址,子网划分','第四章 网络层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(86,17,103,'single_choice','hard','TCP 拥塞控制中，慢开始阶段拥塞窗口的典型变化特点是（ ）。','B','慢开始阶段每经过一个 RTT，拥塞窗口通常近似指数增长，直到达到阈值或发生拥塞。',5.00,'TCP,拥塞控制','第五章 传输层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(87,17,103,'single_choice','medium','DNS 的主要作用是（ ）。','C','DNS 负责将便于记忆的域名解析为 IP 地址，也可提供反向解析等功能。',5.00,'DNS,域名解析','第六章 应用层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(88,17,103,'multi_choice','medium','关于 TCP 三次握手，下列说法正确的有（ ）。','A,B,D','三次握手用于确认双方收发能力并同步初始序号；它不是为了传输 HTTP 正文。',8.00,'TCP,连接管理','第五章 传输层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(89,17,103,'multi_choice','medium','下列地址范围中，属于 IPv4 私有地址范围的有（ ）。','A,C,D','RFC1918 私有地址包括 10.0.0.0/8、172.16.0.0/12、192.168.0.0/16。',8.00,'IP地址,私有地址','第四章 网络层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(90,17,103,'multi_choice','hard','关于 CRC 校验，下列说法正确的有（ ）。','A,B,C','CRC 通过多项式除法生成校验序列，常用于差错检测，但不能保证纠正任意错误。',8.00,'CRC,差错检测','第三章 数据链路层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(91,17,103,'multi_choice','hard','关于 HTTPS，下列说法正确的有（ ）。','A,B,D','HTTPS 在 HTTP 基础上引入 TLS，为传输提供加密、完整性校验和服务器身份认证等能力。',8.00,'HTTPS,TLS','网络安全基础',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(92,17,103,'true_false','easy','IPv4 地址长度为 32 位。','true','IPv4 地址由 32 个二进制位组成，通常写成点分十进制形式。',4.00,'IP地址','第四章 网络层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(93,17,103,'true_false','easy','UDP 是面向连接的可靠传输协议。','false','UDP 是无连接协议，不提供 TCP 那样的可靠传输、拥塞控制和连接管理。',4.00,'TCP,UDP','第五章 传输层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(94,17,103,'true_false','medium','以太网交换机的每个端口通常可以隔离一个冲突域。','true','交换机每个端口形成独立冲突域，有利于减少冲突并提升网络性能。',4.00,'交换机,冲突域','第三章 数据链路层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(95,17,103,'true_false','medium','DNS 只能把域名解析为 IPv4 地址，不能支持 IPv6 地址解析。','false','DNS 可通过 A 记录解析 IPv4，也可通过 AAAA 记录解析 IPv6。',4.00,'DNS,IPv6','第六章 应用层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(96,17,103,'fill_blank','easy','IPv4 地址 192.168.1.10 中，第一个十进制字段是 ____。','192','点分十进制 IPv4 地址由 4 个 0~255 的十进制字段组成。',5.00,'IP地址','第四章 网络层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(97,17,103,'fill_blank','medium','HTTP 服务默认使用的 TCP 端口号是 ____。','80','传统 HTTP 默认端口是 80，HTTPS 默认端口是 443。',5.00,'端口号,HTTP','第五章 传输层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(98,17,103,'fill_blank','medium','DNS 中用于表示 IPv6 地址解析结果的资源记录类型是 ____。','AAAA','A 记录对应 IPv4 地址，AAAA 记录对应 IPv6 地址。',5.00,'DNS,IPv6','第六章 应用层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(99,17,103,'fill_blank','hard','在 IPv4 局域网中，ARP 协议常用于根据 IP 地址解析对应的 ____ 地址。','MAC','ARP 用于在局域网中根据目标 IPv4 地址获取目标 MAC 地址。',5.00,'ARP,MAC地址','第三章 数据链路层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(100,17,103,'essay','medium','简述 TCP 和 UDP 的主要区别，并各举一个适合使用的应用场景。','TCP 面向连接、可靠、有序，具备流量控制和拥塞控制；UDP 无连接、开销小、实时性好但不保证可靠。TCP 适合文件传输、网页访问等；UDP 适合音视频通话、实时游戏、DNS 查询等。','作答要点：连接方式、可靠性、开销/实时性、典型应用场景。',15.00,'TCP,UDP','第五章 传输层',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(101,17,103,'essay','hard','简述 HTTPS 相比 HTTP 在安全性上的改进，并说明证书的作用。','HTTPS 通过 TLS 对 HTTP 通信进行加密，降低窃听风险；通过完整性校验降低篡改风险；通过数字证书验证服务器身份，减少中间人攻击风险。证书由可信 CA 签发，包含服务器公钥和身份信息。','作答要点：加密、完整性、身份认证、证书与 CA。',15.00,'HTTPS,TLS','网络安全基础',0,1,'net_seed_20260608',NULL,'2026-06-08 10:00:05','2026-06-08 10:00:05',0),(102,15,103,'single_choice','medium','交换机在数据链路层主要依据什么信息转发数据帧？','B','交换机通过维护MAC地址表记录端口与设备MAC地址的映射关系，根据目标MAC地址进行数据帧的定向转发。',5.00,'交换机数据转发机制',NULL,0,1,'ai',NULL,'2026-06-08 15:19:06','2026-06-08 15:19:06',0),(103,15,103,'single_choice','medium',' VLAN（虚拟局域网）在交换机中的主要作用是什么？','B','VLAN技术将物理网络划分为多个逻辑广播域，有效抑制广播风暴并提升网络安全性与管理灵活性。',5.00,'VLAN技术原理',NULL,0,1,'ai',NULL,'2026-06-08 15:19:06','2026-06-08 15:19:06',0),(104,15,103,'single_choice','medium','生成树协议（STP）在交换机网络中的核心目的是什么？','B','STP通过阻塞冗余路径消除二层拓扑环路，避免因广播包无限循环导致的网络瘫痪。',5.00,'生成树协议功能',NULL,0,1,'ai',NULL,'2026-06-08 15:19:07','2026-06-08 15:19:07',0),(105,15,103,'single_choice','medium','交换机动态学习MAC地址表的触发条件是？','B','交换机通过检查入站数据帧的源MAC地址及其对应端口，自动更新MAC地址表以建立转发映射关系。',5.00,'MAC地址学习机制',NULL,0,1,'ai',NULL,'2026-06-08 15:19:07','2026-06-08 15:19:07',0),(106,15,103,'single_choice','medium','交换机端口安全功能中\'Sticky MAC\'模式的作用是？','B','Sticky MAC会将动态学习的合法MAC地址转为静态配置，重启后仍保留绑定关系，兼顾安全性与运维效率。',5.00,'端口安全技术',NULL,0,1,'ai',NULL,'2026-06-08 15:19:07','2026-06-08 15:19:07',0),(107,15,103,'single_choice','medium','交换机在数据链路层主要依据什么信息转发数据帧？','B','交换机通过维护MAC地址表记录端口与设备MAC地址的映射关系，根据目标MAC地址进行数据帧的定向转发。',5.00,'交换机数据转发机制',NULL,0,1,'ai',NULL,'2026-06-08 15:19:08','2026-06-08 15:19:08',0),(108,15,103,'single_choice','medium',' VLAN（虚拟局域网）在交换机中的主要作用是什么？','B','VLAN技术将物理网络划分为多个逻辑广播域，有效抑制广播风暴并提升网络安全性与管理灵活性。',5.00,'VLAN技术原理',NULL,0,1,'ai',NULL,'2026-06-08 15:19:08','2026-06-08 15:19:08',0),(109,15,103,'single_choice','medium','生成树协议（STP）在交换机网络中的核心目的是什么？','B','STP通过阻塞冗余路径消除二层拓扑环路，避免因广播包无限循环导致的网络瘫痪。',5.00,'生成树协议功能',NULL,0,1,'ai',NULL,'2026-06-08 15:19:08','2026-06-08 15:19:08',0),(110,15,103,'single_choice','medium','交换机动态学习MAC地址表的触发条件是？','B','交换机通过检查入站数据帧的源MAC地址及其对应端口，自动更新MAC地址表以建立转发映射关系。',5.00,'MAC地址学习机制',NULL,0,1,'ai',NULL,'2026-06-08 15:19:09','2026-06-08 15:19:09',0),(111,15,103,'single_choice','medium','交换机端口安全功能中\'Sticky MAC\'模式的作用是？','B','Sticky MAC会将动态学习的合法MAC地址转为静态配置，重启后仍保留绑定关系，兼顾安全性与运维效率。',5.00,'端口安全技术',NULL,0,1,'ai',NULL,'2026-06-08 15:19:09','2026-06-08 15:19:09',0);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_option`
--

DROP TABLE IF EXISTS `question_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_option` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL,
  `option_label` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `option_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_correct` tinyint NOT NULL DEFAULT '0',
  `sort_order` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=272 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_option`
--

LOCK TABLES `question_option` WRITE;
/*!40000 ALTER TABLE `question_option` DISABLE KEYS */;
INSERT INTO `question_option` VALUES (1,1,'A','资源共享',1,1,'2026-06-03 17:08:08'),(2,1,'B','提高计算机的可靠性',0,2,'2026-06-03 17:08:08'),(3,1,'C','共享数据库',0,3,'2026-06-03 17:08:08'),(4,1,'D','使用服务器上的硬盘',0,4,'2026-06-03 17:08:08'),(5,2,'A','提高计算机运行速度',0,1,'2026-06-03 17:08:08'),(6,2,'B','连接多台计算机',0,2,'2026-06-03 17:08:08'),(7,2,'C','共享软硬件和数据资源',1,3,'2026-06-03 17:08:08'),(8,2,'D','实现分布处理',0,4,'2026-06-03 17:08:08'),(9,3,'A','多个处理器通过共享内存实现的紧耦合系统',0,1,'2026-06-03 17:08:08'),(10,3,'B','执行计算机数据处理的软件模块',0,2,'2026-06-03 17:08:08'),(11,3,'C','由自治的计算机互连起来的集合体',1,3,'2026-06-03 17:08:08'),(12,3,'D','用于共同完成一项任务的分布式系统',0,4,'2026-06-03 17:08:08'),(13,4,'A','总线结构',1,1,'2026-06-03 17:08:08'),(14,4,'B','环型结构',0,2,'2026-06-03 17:08:08'),(15,4,'C','星型结构',0,3,'2026-06-03 17:08:08'),(16,4,'D','网状结构',0,4,'2026-06-03 17:08:08'),(17,5,'A','机械特性',0,1,'2026-06-03 17:08:08'),(18,5,'B','电气特性',0,2,'2026-06-03 17:08:08'),(19,5,'C','功能特性',1,3,'2026-06-03 17:08:08'),(20,5,'D','规程特性',0,4,'2026-06-03 17:08:08'),(21,6,'A','可以在两个方向上同时传输',1,1,'2026-06-03 17:08:08'),(22,6,'B','只能在一个方向上传输',0,2,'2026-06-03 17:08:08'),(23,6,'C','可以在两个方向上传输，但不能同时进行',0,3,'2026-06-03 17:08:08'),(24,6,'D','以上均不对',0,4,'2026-06-03 17:08:08'),(25,7,'A','可以在两个方向上同时传输',0,1,'2026-06-03 17:08:08'),(26,7,'B','只能在一个方向上传输',0,2,'2026-06-03 17:08:08'),(27,7,'C','可以在两个方向上传输，但不能同时进行',1,3,'2026-06-03 17:08:08'),(28,7,'D','以上均不对',0,4,'2026-06-03 17:08:08'),(29,8,'A','机械特性',0,1,'2026-06-03 17:08:08'),(30,8,'B','功能特性',0,2,'2026-06-03 17:08:08'),(31,8,'C','过程特性',1,3,'2026-06-03 17:08:08'),(32,8,'D','电气特性',0,4,'2026-06-03 17:08:08'),(33,9,'A','曼彻斯特编码',1,1,'2026-06-03 17:08:08'),(34,9,'B','不归零码',0,2,'2026-06-03 17:08:08'),(35,9,'C','BCD码',0,3,'2026-06-03 17:08:08'),(36,9,'D','循环冗余码',0,4,'2026-06-03 17:08:08'),(37,10,'A','物理层',0,1,'2026-06-03 17:08:08'),(38,10,'B','数据链路层',1,2,'2026-06-03 17:08:08'),(39,10,'C','网络层',0,3,'2026-06-03 17:08:08'),(40,10,'D','高层',0,4,'2026-06-03 17:08:08'),(41,11,'A','LLC地址',0,1,'2026-06-03 17:08:08'),(42,11,'B','MAC地址',1,2,'2026-06-03 17:08:08'),(43,11,'C','IP地址',0,3,'2026-06-03 17:08:08'),(44,11,'D','IPX地址',0,4,'2026-06-03 17:08:08'),(45,12,'A','冲突域',1,1,'2026-06-03 17:08:08'),(46,12,'B','广播域',0,2,'2026-06-03 17:08:08'),(47,12,'C','管理域',0,3,'2026-06-03 17:08:08'),(48,12,'D','阻塞域',0,4,'2026-06-03 17:08:08'),(49,13,'A','物理层',1,1,'2026-06-03 17:08:08'),(50,13,'B','链路层',0,2,'2026-06-03 17:08:08'),(51,13,'C','网络层',0,3,'2026-06-03 17:08:08'),(52,13,'D','运输层',0,4,'2026-06-03 17:08:08'),(53,14,'A','物理层',0,1,'2026-06-03 17:08:08'),(54,14,'B','数据链路层',1,2,'2026-06-03 17:08:08'),(55,14,'C','传输层',0,3,'2026-06-03 17:08:08'),(56,14,'D','网络层',0,4,'2026-06-03 17:08:08'),(57,15,'A','任何一个节点的通信数据要通过整个网络，并且每一个节点都接收并检验该数据',1,1,'2026-06-03 17:08:08'),(58,15,'B','如果源节点知道目的地的IP和MAC地址的话，信号是直接送往目的地',0,2,'2026-06-03 17:08:08'),(59,15,'C','每一个节点的数据发往最近的路由器，路由器将数据直接发到目的地',0,3,'2026-06-03 17:08:08'),(60,15,'D','信号都是以广播方式发送的',0,4,'2026-06-03 17:08:08'),(61,16,'A','网络功能',0,1,'2026-06-03 17:08:08'),(62,16,'B','管理性质',0,2,'2026-06-03 17:08:08'),(63,16,'C','网络跨度',0,3,'2026-06-03 17:08:08'),(64,16,'D','网络拓扑',1,4,'2026-06-03 17:08:08'),(65,17,'A','分组交换技术',1,1,'2026-06-03 17:08:08'),(66,17,'B','电路交换技术',0,2,'2026-06-03 17:08:08'),(67,17,'C','报文交换技术',0,3,'2026-06-03 17:08:08'),(68,17,'D','分组交换与电路交换结合技术',0,4,'2026-06-03 17:08:08'),(69,18,'A','十的负二次方',0,1,'2026-06-03 17:08:08'),(70,18,'B','十的负四次方',0,2,'2026-06-03 17:08:08'),(71,18,'C','十的负六次方',0,3,'2026-06-03 17:08:08'),(72,18,'D','十的负九次方',1,4,'2026-06-03 17:08:08'),(73,19,'A','TCP是面向流的运输层协议',0,1,'2026-06-03 17:08:08'),(74,19,'B','TCP报文段占用网络带宽较小',0,2,'2026-06-03 17:08:08'),(75,19,'C','TCP只能在半双工下工作',1,3,'2026-06-03 17:08:08'),(76,19,'D','TCP无法实现流量控制',0,4,'2026-06-03 17:08:08'),(77,20,'A','提供不可靠的数据传输服务',0,1,'2026-06-03 17:08:08'),(78,20,'B','对程序发送的大块数据进行分段和重组',1,2,'2026-06-03 17:08:08'),(79,20,'C','确保正确排序以及按顺序传递分段的数据',1,3,'2026-06-03 17:08:08'),(80,20,'D','通过计算机校验和，进行传输数据的完整性检查',1,4,'2026-06-03 17:08:08'),(81,21,'A','报头长度为20~60B,其中固定部分为20B',1,1,'2026-06-03 17:08:08'),(82,21,'B','端口号字段依次表示源端口号与目的端口号',1,2,'2026-06-03 17:08:08'),(83,21,'C','报头长度总是4的倍数个字节',1,3,'2026-06-03 17:08:08'),(84,21,'D','TCP校验和伪首部中IP分组头的协议字段为17',0,4,'2026-06-03 17:08:08'),(85,22,'A','TCP是端到端的协议，UDP是点到点的协议',1,1,'2026-06-03 17:08:08'),(86,22,'B','TCP是点到点的协议，UDP是端到端的协议',1,2,'2026-06-03 17:08:08'),(87,22,'C','TCP和UDP都是端到端的协议',0,3,'2026-06-03 17:08:08'),(88,22,'D','TCP和UDP都是点到点的协议',1,4,'2026-06-03 17:08:08'),(89,40,'A','<a>标签',1,1,'2026-01-15 10:00:00'),(90,40,'B','<link>标签',0,2,'2026-01-15 10:00:00'),(91,40,'C','<href>标签',0,3,'2026-01-15 10:00:00'),(92,40,'D','<url>标签',0,4,'2026-01-15 10:00:00'),(93,41,'A','background-color',1,1,'2026-01-15 10:00:00'),(94,41,'B','color(文字颜色)',0,2,'2026-01-15 10:00:00'),(95,41,'C','bg-color',0,3,'2026-01-15 10:00:00'),(96,41,'D','back-color',0,4,'2026-01-15 10:00:00'),(97,42,'A','v-model',1,1,'2026-01-15 10:00:00'),(98,42,'B','v-if',0,2,'2026-01-15 10:00:00'),(99,42,'C','v-html',0,3,'2026-01-15 10:00:00'),(100,42,'D','v-for',0,4,'2026-01-15 10:00:00'),(101,43,'A','void',1,1,'2026-01-15 10:00:00'),(102,43,'B','int',0,2,'2026-01-15 10:00:00'),(103,43,'C','String',0,3,'2026-01-15 10:00:00'),(104,43,'D','boolean',0,4,'2026-01-15 10:00:00'),(105,44,'A','int',0,1,'2026-01-15 10:00:00'),(106,44,'B','String(引用类型)',1,2,'2026-01-15 10:00:00'),(107,44,'C','double',0,3,'2026-01-15 10:00:00'),(108,44,'D','boolean',0,4,'2026-01-15 10:00:00'),(109,45,'A','ArrayList数组查询快 LinkedList链表增删快',1,1,'2026-01-15 10:00:00'),(110,45,'B','两者完全相同',0,2,'2026-01-15 10:00:00'),(111,45,'C','LinkedList更快',0,3,'2026-01-15 10:00:00'),(112,46,'A','read_csv()',1,1,'2026-01-15 10:00:00'),(113,46,'B','load_csv()',0,2,'2026-01-15 10:00:00'),(114,46,'C','open_csv()',0,3,'2026-01-15 10:00:00'),(115,46,'D','import_csv()',0,4,'2026-01-15 10:00:00'),(116,47,'A','zeros()',1,1,'2026-01-15 10:00:00'),(117,47,'B','empty()',0,2,'2026-01-15 10:00:00'),(118,47,'C','full()',0,3,'2026-01-15 10:00:00'),(119,47,'D','ones()',0,4,'2026-01-15 10:00:00'),(120,48,'A','plot()',1,1,'2026-01-15 10:00:00'),(121,48,'B','bar()柱状图',0,2,'2026-01-15 10:00:00'),(122,48,'C','scatter()散点图',0,3,'2026-01-15 10:00:00'),(123,48,'D','pie()饼图',0,4,'2026-01-15 10:00:00'),(124,50,'A','1',1,1,'2026-06-05 15:52:00'),(125,50,'B','2',0,2,'2026-06-05 15:52:00'),(126,50,'C','3',0,3,'2026-06-05 15:52:00'),(127,50,'D','4',0,4,'2026-06-05 15:52:00'),(128,51,'A','4',1,1,'2026-06-06 17:50:18'),(129,51,'B','3',0,2,'2026-06-06 17:50:18'),(130,51,'C','5',0,3,'2026-06-06 17:50:18'),(131,51,'D','6',0,4,'2026-06-06 17:50:18'),(192,82,'A','同层实体之间的物理连接',0,1,'2026-06-08 10:00:05'),(193,82,'B','相邻层之间约定的服务接口',1,2,'2026-06-08 10:00:05'),(194,82,'C','应用程序的用户界面',0,3,'2026-06-08 10:00:05'),(195,82,'D','网络管理员手工配置的路由表',0,4,'2026-06-08 10:00:05'),(196,83,'A','应用层',0,1,'2026-06-08 10:00:05'),(197,83,'B','传输层',0,2,'2026-06-08 10:00:05'),(198,83,'C','网际层',1,3,'2026-06-08 10:00:05'),(199,83,'D','网络接口层',0,4,'2026-06-08 10:00:05'),(200,84,'A','目的 MAC 地址',1,1,'2026-06-08 10:00:05'),(201,84,'B','目的端口号',0,2,'2026-06-08 10:00:05'),(202,84,'C','HTTP 请求路径',0,3,'2026-06-08 10:00:05'),(203,84,'D','进程 ID',0,4,'2026-06-08 10:00:05'),(204,85,'A','192.168.1.0',0,1,'2026-06-08 10:00:05'),(205,85,'B','192.168.1.64',0,2,'2026-06-08 10:00:05'),(206,85,'C','192.168.1.128',1,3,'2026-06-08 10:00:05'),(207,85,'D','192.168.1.255',0,4,'2026-06-08 10:00:05'),(208,86,'A','始终保持不变',0,1,'2026-06-08 10:00:05'),(209,86,'B','近似指数增长',1,2,'2026-06-08 10:00:05'),(210,86,'C','只会线性下降',0,3,'2026-06-08 10:00:05'),(211,86,'D','每收到一个报文段就归零',0,4,'2026-06-08 10:00:05'),(212,87,'A','加密所有网页内容',0,1,'2026-06-08 10:00:05'),(213,87,'B','压缩图片文件',0,2,'2026-06-08 10:00:05'),(214,87,'C','完成域名与 IP 地址之间的映射',1,3,'2026-06-08 10:00:05'),(215,87,'D','替代 TCP 建立连接',0,4,'2026-06-08 10:00:05'),(216,88,'A','客户端通常先发送 SYN 报文段',1,1,'2026-06-08 10:00:05'),(217,88,'B','服务端收到 SYN 后通常回复 SYN+ACK',1,2,'2026-06-08 10:00:05'),(218,88,'C','三次握手用于直接传输完整网页正文',0,3,'2026-06-08 10:00:05'),(219,88,'D','第三次握手中客户端通常发送 ACK',1,4,'2026-06-08 10:00:05'),(220,89,'A','10.0.0.0/8',1,1,'2026-06-08 10:00:05'),(221,89,'B','11.0.0.0/8',0,2,'2026-06-08 10:00:05'),(222,89,'C','172.16.0.0/12',1,3,'2026-06-08 10:00:05'),(223,89,'D','192.168.0.0/16',1,4,'2026-06-08 10:00:05'),(224,90,'A','CRC 常用于数据链路层差错检测',1,1,'2026-06-08 10:00:05'),(225,90,'B','发送端会根据生成多项式计算校验序列',1,2,'2026-06-08 10:00:05'),(226,90,'C','接收端可用同一生成多项式进行检测',1,3,'2026-06-08 10:00:05'),(227,90,'D','CRC 可以保证纠正任意位错误',0,4,'2026-06-08 10:00:05'),(228,91,'A','HTTPS 通常使用 TLS 保护 HTTP 通信',1,1,'2026-06-08 10:00:05'),(229,91,'B','HTTPS 可以降低明文被窃听的风险',1,2,'2026-06-08 10:00:05'),(230,91,'C','HTTPS 会取消 DNS 查询过程',0,3,'2026-06-08 10:00:05'),(231,91,'D','证书可用于验证服务器身份',1,4,'2026-06-08 10:00:05'),(232,102,'A','IP地址',0,1,'2026-06-08 15:19:06'),(233,102,'B','MAC地址表',1,2,'2026-06-08 15:19:06'),(234,102,'C','ARP缓存表',0,3,'2026-06-08 15:19:06'),(235,102,'D','路由表',0,4,'2026-06-08 15:19:06'),(236,103,'A','提高带宽利用率',0,1,'2026-06-08 15:19:06'),(237,103,'B','隔离广播域',1,2,'2026-06-08 15:19:06'),(238,103,'C','增强网络安全加密',0,3,'2026-06-08 15:19:06'),(239,103,'D','自动分配IP地址',0,4,'2026-06-08 15:19:06'),(240,104,'A','负载均衡',0,1,'2026-06-08 15:19:06'),(241,104,'B','防止环路',1,2,'2026-06-08 15:19:06'),(242,104,'C','动态路由选择',0,3,'2026-06-08 15:19:06'),(243,104,'D','QoS优先级调度',0,4,'2026-06-08 15:19:06'),(244,105,'A','收到未知单播帧',0,1,'2026-06-08 15:19:06'),(245,105,'B','发送源MAC信息的数据帧',1,2,'2026-06-08 15:19:06'),(246,105,'C','管理员手动配置',0,3,'2026-06-08 15:19:06'),(247,105,'D','周期性ARP请求',0,4,'2026-06-08 15:19:06'),(248,106,'A','永久禁用违规端口',0,1,'2026-06-08 15:19:06'),(249,106,'B','动态保存学习到的MAC地址',1,2,'2026-06-08 15:19:06'),(250,106,'C','强制切换端口速率',0,3,'2026-06-08 15:19:06'),(251,106,'D','自动删除过期MAC条目',0,4,'2026-06-08 15:19:06'),(252,107,'A','IP地址',0,1,'2026-06-08 15:19:08'),(253,107,'B','MAC地址表',1,2,'2026-06-08 15:19:08'),(254,107,'C','ARP缓存表',0,3,'2026-06-08 15:19:08'),(255,107,'D','路由表',0,4,'2026-06-08 15:19:08'),(256,108,'A','提高带宽利用率',0,1,'2026-06-08 15:19:08'),(257,108,'B','隔离广播域',1,2,'2026-06-08 15:19:08'),(258,108,'C','增强网络安全加密',0,3,'2026-06-08 15:19:08'),(259,108,'D','自动分配IP地址',0,4,'2026-06-08 15:19:08'),(260,109,'A','负载均衡',0,1,'2026-06-08 15:19:08'),(261,109,'B','防止环路',1,2,'2026-06-08 15:19:08'),(262,109,'C','动态路由选择',0,3,'2026-06-08 15:19:08'),(263,109,'D','QoS优先级调度',0,4,'2026-06-08 15:19:08'),(264,110,'A','收到未知单播帧',0,1,'2026-06-08 15:19:08'),(265,110,'B','发送源MAC信息的数据帧',1,2,'2026-06-08 15:19:08'),(266,110,'C','管理员手动配置',0,3,'2026-06-08 15:19:08'),(267,110,'D','周期性ARP请求',0,4,'2026-06-08 15:19:08'),(268,111,'A','永久禁用违规端口',0,1,'2026-06-08 15:19:08'),(269,111,'B','动态保存学习到的MAC地址',1,2,'2026-06-08 15:19:08'),(270,111,'C','强制切换端口速率',0,3,'2026-06-08 15:19:08'),(271,111,'D','自动删除过期MAC条目',0,4,'2026-06-08 15:19:08');
/*!40000 ALTER TABLE `question_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semester`
--

DROP TABLE IF EXISTS `semester`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semester` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semester`
--

LOCK TABLES `semester` WRITE;
/*!40000 ALTER TABLE `semester` DISABLE KEYS */;
INSERT INTO `semester` VALUES (1,'2024-2025学年第二学期','2025-02-20','2025-07-10',1,'2026-06-03 17:08:07','2026-06-03 17:08:07',0),(2,'2024-2025学年第一学期','2024-09-01','2025-01-15',1,'2026-06-03 17:08:07','2026-06-09 08:42:48',0),(7,'2025-2026学年第一学期','2026-07-11','2026-07-14',1,'2026-06-08 22:52:17','2026-06-09 08:44:24',0),(8,'2025-2026学年第二学期','2026-07-31','2026-09-30',1,'2026-06-09 08:44:56','2026-06-09 08:44:56',0);
/*!40000 ALTER TABLE `semester` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_class`
--

DROP TABLE IF EXISTS `student_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `semester_id` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_student_class_semester` (`student_id`,`class_id`,`semester_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_class`
--

LOCK TABLES `student_class` WRITE;
/*!40000 ALTER TABLE `student_class` DISABLE KEYS */;
INSERT INTO `student_class` VALUES (1,201,1,1,'2026-06-03 17:08:07'),(2,202,1,1,'2026-06-03 17:08:07'),(3,203,1,1,'2026-06-03 17:08:07'),(4,204,2,1,'2026-06-03 17:08:07'),(5,205,2,1,'2026-06-03 17:08:07'),(7,306,901,1,'2026-06-06 20:25:12'),(8,307,901,1,'2026-06-06 20:25:12'),(9,400,902,1,'2026-06-06 20:25:12'),(10,401,902,1,'2026-06-06 20:25:12'),(11,402,903,1,'2026-06-06 20:25:12'),(12,403,903,1,'2026-06-06 20:25:12'),(13,404,904,1,'2026-06-06 20:25:12'),(14,405,904,1,'2026-06-06 20:25:12'),(15,406,901,1,'2026-06-06 20:25:12'),(16,407,902,1,'2026-06-06 20:25:12'),(17,408,903,1,'2026-06-06 20:25:12'),(18,409,904,1,'2026-06-06 20:25:12'),(25,400,910,7,'2026-06-08 23:11:32'),(26,401,910,7,'2026-06-08 23:11:32'),(27,429,910,7,'2026-06-08 23:11:32'),(28,206,3,7,'2026-06-08 23:17:28'),(29,419,3,7,'2026-06-08 23:17:28');
/*!40000 ALTER TABLE `student_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_favorite_question`
--

DROP TABLE IF EXISTS `student_favorite_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_favorite_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_student_question` (`student_id`,`question_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_favorite_question`
--

LOCK TABLES `student_favorite_question` WRITE;
/*!40000 ALTER TABLE `student_favorite_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_favorite_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_imported_question`
--

DROP TABLE IF EXISTS `student_imported_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_imported_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `original_question_id` bigint NOT NULL,
  `imported_question_id` bigint NOT NULL,
  `bank_id` bigint DEFAULT NULL,
  `import_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_original_question_id` (`original_question_id`),
  KEY `idx_imported_question_id` (`imported_question_id`),
  KEY `idx_bank_id` (`bank_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_imported_question`
--

LOCK TABLES `student_imported_question` WRITE;
/*!40000 ALTER TABLE `student_imported_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_imported_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_question_bank`
--

DROP TABLE IF EXISTS `student_question_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_question_bank` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source_bank_id` bigint DEFAULT NULL,
  `question_count` int NOT NULL DEFAULT '0',
  `is_shared` tinyint NOT NULL DEFAULT '0',
  `share_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `share_expire_time` datetime DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_source_bank_id` (`source_bank_id`),
  KEY `idx_share_code` (`share_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_question_bank`
--

LOCK TABLES `student_question_bank` WRITE;
/*!40000 ALTER TABLE `student_question_bank` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_question_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_question_bank_item`
--

DROP TABLE IF EXISTS `student_question_bank_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_question_bank_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bank_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_bank_question` (`bank_id`,`question_id`),
  KEY `idx_bank_id` (`bank_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_question_bank_item`
--

LOCK TABLES `student_question_bank_item` WRITE;
/*!40000 ALTER TABLE `student_question_bank_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_question_bank_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_question_bank_share`
--

DROP TABLE IF EXISTS `student_question_bank_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_question_bank_share` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bank_id` bigint NOT NULL,
  `share_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `creator_id` bigint NOT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `used_count` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_share_code` (`share_code`),
  KEY `idx_bank_id` (`bank_id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_question_bank_share`
--

LOCK TABLES `student_question_bank_share` WRITE;
/*!40000 ALTER TABLE `student_question_bank_share` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_question_bank_share` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_class`
--

DROP TABLE IF EXISTS `sys_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_grade` (`grade`)
) ENGINE=InnoDB AUTO_INCREMENT=911 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_class`
--

LOCK TABLES `sys_class` WRITE;
/*!40000 ALTER TABLE `sys_class` DISABLE KEYS */;
INSERT INTO `sys_class` VALUES (1,'计算机科学2401班','2024级','计算机科学与技术专业2401班','2026-06-03 17:08:07','2026-06-08 22:52:35',1),(2,'软件工程2401班','2024级','软件工程专业2401班','2026-06-03 17:08:07','2026-06-03 17:08:07',0),(3,'信息安全2401班','2024级','信息安全专业2401班','2026-06-03 17:08:07','2026-06-03 17:08:07',0),(901,'计科2401班','2024级','计算机科学与技术专业2024级1班','2026-06-06 20:25:12','2026-06-08 21:51:48',0),(902,'计科2402班','2024级','计算机科学与技术专业2024级2班','2026-06-06 20:25:12','2026-06-08 21:51:48',0),(903,'软工2401班','2024级','软件工程专业2024级1班','2026-06-06 20:25:12','2026-06-08 21:51:48',0),(904,'软工2402班','2024级','软件工程专业2024级2班','2026-06-06 20:25:12','2026-06-08 21:51:48',0),(910,'计算机一班','2024级','111','2026-06-08 22:53:02','2026-06-08 22:53:02',0);
/*!40000 ALTER TABLE `sys_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `last_login_time` datetime DEFAULT NULL,
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=435 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$Tlvv1obfzrkOVyTiYjfgguH1bzuYuqMuzuV6zI8Bki5V2j9W3tUD6','系统管理员','admin',NULL,NULL,NULL,1,'2026-06-09 16:29:16','171.15.18.98','2026-06-03 17:08:07','2026-06-03 17:08:07',0),(101,'zhanglaoshi','123456','张老师','teacher','zhanglaoshi@nlstudy.local','13800000101',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(102,'laoli','123456','李老师','teacher','laoli@nlstudy.local','13800000102',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(103,'wanglaoshi','$2a$10$MPVY0aWcPzpEy8tJjjPPUewU1oKmwHlBF4g0xRA/EranQCFCR5C6y','王老师','teacher','wanglaoshi@nlstudy.local','13800000103',NULL,1,'2026-06-09 16:29:40','171.15.18.98','2026-06-03 17:08:07','2026-06-08 22:59:56',0),(201,'xiaoming','$2a$10$Ei3mMdwBFyf4sIeLuiTQmOaXjxFOc7upZktcF4pmekHcuHDnx4CYu','王小明','student','xiaoming@nlstudy.local','13800000201',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(202,'xiaohong','123456','李小红','student','xiaohong@nlstudy.local','13800000202',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(203,'xiaogang','123456','赵小刚','student','xiaogang@nlstudy.local','13800000203',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(204,'xiaoli','123456','周小丽','student','xiaoli@nlstudy.local','13800000204',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(205,'xiaowei','123456','陈小伟','student','xiaowei@nlstudy.local','13800000205',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(206,'xiaoqian','123456','吴小倩','student','xiaoqian@nlstudy.local','13800000206',NULL,1,NULL,NULL,'2026-06-03 17:08:07','2026-06-08 22:59:56',0),(207,'teacher','','演示','teacher','teacher@nlstudy.local','13800000207',NULL,1,'2026-06-09 08:36:48','171.15.18.98','2026-06-04 16:38:39','2026-06-09 08:45:22',0),(208,'student','$2a$10$3y675imp//FMvGvFdR40IufjL6ZFg3uuJHZ9zQZnDZvLx9ACNLvZG','演示学生','student','student@nlstudy.local','13800000208',NULL,1,NULL,NULL,'2026-06-04 16:38:39','2026-06-08 22:59:56',0),(301,'student1','.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq','王小明','student','wang@example.com','13800138010',NULL,1,NULL,NULL,'2026-06-04 17:47:03','2026-06-08 22:39:51',0),(302,'student2','.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq','李小红','student','lihong@example.com','13800138011',NULL,1,NULL,NULL,'2026-06-04 17:47:03','2026-06-08 22:39:51',0),(303,'student3','.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq','赵小刚','student','zhao@example.com','13800138012',NULL,1,NULL,NULL,'2026-06-04 17:47:03','2026-06-08 22:39:51',0),(304,'student4','.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq','刘小华','student','liu@example.com','13800138013',NULL,1,NULL,NULL,'2026-06-04 17:47:03','2026-06-08 22:40:49',0),(305,'student5','.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq','陈小丽','student','chen@example.com','13800138014',NULL,1,NULL,NULL,'2026-06-04 17:47:03','2026-06-08 22:40:49',0),(306,'stu_wang_01','e10adc3949ba59abbe56e057f20f883e','张三','student','zhangsan@test.com','13800138001',NULL,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(307,'stu_wang_02','e10adc3949ba59abbe56e057f20f883e','李四','student','lisi@test.com','13800138002',NULL,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(308,'stu_wang_03','e10adc3949ba59abbe56e057f20f883e','王五','student','wangwu@test.com','13800138003',NULL,1,NULL,NULL,'2026-01-15 10:00:00','2026-01-15 10:00:00',0),(400,'stu_001','$2a$10$JdKOHbz7t5H.sbhH8qS8B.WnrrmiyZHl46JncEduUwxNNRrcnYiaq','张明','student','stu_001@nlstudy.local','13800000400',NULL,1,'2026-06-09 16:30:07','171.15.18.98','2026-06-05 15:20:03','2026-06-08 22:59:56',0),(401,'stu_002','$2a$10$Lie1uRR.qWGXb.oWE/8HkOSC.lpMqlSFjs5snDwyYVJEQ1qxAvS1S','李娜','student','stu_002@nlstudy.local','13800000401',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:56',0),(402,'stu_003','$2a$10$WN064OdS.Bdl6UUDFms8VuJCEnhHqKHrEIynzr16OZOfhr.EbtoHe','王强','student','stu_003@nlstudy.local','13800000402',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:56',0),(403,'stu_004','$2a$10$CILiEUjgJtzOIaRrhNbdtuRuVznyriyZVPqfJ6LrtXYp8pB/kgGIu','赵芳','student','stu_004@nlstudy.local','13800000403',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:56',0),(404,'stu_005','$2a$10$jDRElfXwmDvcnOyOkbQOVuxL4uhaNp6pUC0qU7Tjvl12QLaQm1FNm','刘洋','student','stu_005@nlstudy.local','13800000404',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:56',0),(405,'stu_006','$2a$10$Wcg0/rqm2kTYX2IXQIeNd.DoPO20Q3eB/E6co8q.XRj1NzTgHq4Ai','陈静','student','stu_006@nlstudy.local','13800000405',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:56',0),(406,'stu_007','$2a$10$fgCVeL1q7dr.lyXQ3l2IgudgqXvi7WXNr2vWS7NtR7D/5jxYrk23W','杨磊','student','stu_007@nlstudy.local','13800000406',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(407,'stu_008','$2a$10$b30V8LFWB3d4XUHz9mz3EeU5x1.a6Lq.EigbX14koh3T7QJuRzX8O','周婷','student','stu_008@nlstudy.local','13800000407',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(408,'stu_009','$2a$10$gUhnTCu3olgUCWjXrI84je3dVmWA9FUm4PMzja5MJgA0ra00bByce','吴鹏','student','stu_009@nlstudy.local','13800000408',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(409,'stu_010','$2a$10$T8jLeYBffoVGhtfar.wx3OAiIq9lu.I6EaEGVb.cYvIij4IGVO94e','郑丽','student','stu_010@nlstudy.local','13800000409',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(410,'stu_011','$2a$10$Q/yFtlxul3Bwcirh48iGWuN0o8MZzS65zSds8NvMmVDuN/S7l49dq','孙浩','student','stu_011@nlstudy.local','13800000410',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(411,'stu_012','$2a$10$9PnU89A3SXS2Thl1aJE8O.WtsN4n.ShMVOOnLlXjHzomXSwVQmNGS','马红','student','stu_012@nlstudy.local','13800000411',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(412,'stu_013','$2a$10$SR/YSF0AawcNV28A4DfIm.71IE8XruU6UumNquOWBYLv1Y.BhCKmG','朱军','student','stu_013@nlstudy.local','13800000412',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(413,'stu_014','$2a$10$lpDInoiyfGYZS84Qz.bs4uBtcpLld.skvBJVbfvnOO.4TTcFsutmO','胡敏','student','stu_014@nlstudy.local','13800000413',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(414,'stu_015','$2a$10$AQZUoOuCOtxHd9sBDCj54enuQ4dYucyxQusNiGqE6gp.aI7g8Yw7i','郭涛','student','stu_015@nlstudy.local','13800000414',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(415,'stu_016','$2a$10$wKHrPfiTQjzUd.8./rRXueaJKgA8.WoFji9XZ7pTtcdTdY93Vm6Qi','林燕','student','stu_016@nlstudy.local','13800000415',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(416,'stu_017','$2a$10$/q12b2SlS.LzBvGDRiOtSe8zDloFcfPcOgkJDvCjDZfwvxih81hkG','何伟','student','stu_017@nlstudy.local','13800000416',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(417,'stu_018','$2a$10$UmBlEFtCxX3YZO6cLELq5uA7hsqqSZelAiYKL9WEKxivfR7g2/5DS','罗雪','student','stu_018@nlstudy.local','13800000417',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(418,'stu_019','$2a$10$vxWs74x8XTuahL6lKZ10Iu3e1OD1XLcFLt6PclPy0cdmxl3M8hLXu','高峰','student','stu_019@nlstudy.local','13800000418',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(419,'stu_020','$2a$10$fG0c1WtnBtTGdEO2jAqnTOxe7Uhn72WsXo0sAoF.hSuB/R4veeyiG','梁娟','student','stu_020@nlstudy.local','13800000419',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(420,'stu_021','$2a$10$mxCpNZ6YtTCRdiXHadh2LOVnP7NebCL6ZYvjNkuM4I0jSHfe2V3a2','谢飞','student','stu_021@nlstudy.local','13800000420',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(421,'stu_022','$2a$10$R6sGY6ZNSma/vNpnldbeyeMlGoJA4oK5GFyp6Uo/mEURzWsmG2CSS','唐玲','student','stu_022@nlstudy.local','13800000421',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(422,'stu_023','$2a$10$cGOE2J7R.zxw1SbAjciq/OhvkVLatJ.9sEt61xIDZPsCHwVhHuwim','韩杰','student','stu_023@nlstudy.local','13800000422',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(423,'stu_024','$2a$10$/2FvlNQZthLl6cc5gPzfTOI.IZy2LgEjhvt64/2bhXOlzYXoM.MKu','冯霞','student','stu_024@nlstudy.local','13800000423',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(424,'stu_025','$2a$10$WP9nll0UaDkSbrqRIv09J.EpZzueYnMJXIMO931ru3igbNuxUaYi6','曹刚','student','stu_025@nlstudy.local','13800000424',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(425,'stu_026','$2a$10$FOBvxwyxdoVXtcKsAB.ZFuQWYcgly5ifbqAKY1UySrnrWQmoYGrYi','彭华','student','stu_026@nlstudy.local','13800000425',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(426,'stu_027','$2a$10$BaJ5tubX.pC1fwMeMXYrCuWk6qJP4NzRi5DMeasl7mub9pzJY0EPi','董勇','student','stu_027@nlstudy.local','13800000426',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(427,'stu_028','$2a$10$6DZyIdBjg.OY9Kbb850upuIME4qsOezk4E3ZApiUd/ad.B2QdCmeu','蒋琴','student','stu_028@nlstudy.local','13800000427',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(428,'stu_029','$2a$10$WIcLJ/a3J4oNegM2LETLm.U0jkOMRhoG/Nav1t5iTlwQnCsdWVgVK','袁斌','student','stu_029@nlstudy.local','13800000428',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0),(429,'stu_030','$2a$10$e3NlXUFVlRZKAoX77ZY8SOs5jZoVIgIInSR7INbplAuOSncagrtEW','许萍','student','stu_030@nlstudy.local','13800000429',NULL,1,NULL,NULL,'2026-06-05 15:20:03','2026-06-08 22:59:57',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_log`
--

DROP TABLE IF EXISTS `system_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `execution_time` int DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_module` (`module`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_log`
--

LOCK TABLES `system_log` WRITE;
/*!40000 ALTER TABLE `system_log` DISABLE KEYS */;
INSERT INTO `system_log` VALUES (1,1,'admin','LOGIN','认证模块','login','{\"username\":\"admin\",\"password\":\"***\"}','登录成功','127.0.0.1','Chrome/120',1,NULL,150,'2025-06-01 08:00:00'),(2,101,'zhanglaoshi','LOGIN','认证模块','login','{\"username\":\"zhanglaoshi\",\"password\":\"***\"}','登录成功','192.168.1.10','Chrome/120',1,NULL,120,'2025-06-01 08:30:00'),(3,101,'zhanglaoshi','CREATE','题库管理','addQuestion','{\"content\":\"计算机网络主要功能...\",\"type\":\"single_choice\"}','创建题目成功，ID=1','192.168.1.10','Chrome/120',1,NULL,200,'2025-06-01 09:00:00'),(4,101,'zhanglaoshi','CREATE','试卷管理','createExamPaper','{\"name\":\"计算机网络第一章测验\",\"courseId\":1}','创建试卷成功，ID=1','192.168.1.10','Chrome/120',1,NULL,300,'2025-06-01 10:00:00'),(5,101,'zhanglaoshi','PUBLISH','考试管理','publishExam','{\"examId\":1,\"startTime\":\"2025-06-02 14:00:00\"}','发布考试成功','192.168.1.10','Chrome/120',1,NULL,180,'2025-06-01 11:00:00'),(6,201,'xiaoming','SUBMIT','考试模块','submitExam','{\"examId\":1}','提交考试成功，得分62分','192.168.1.100','Chrome/120',1,NULL,500,'2025-06-01 14:22:30'),(7,202,'xiaohong','SUBMIT','考试模块','submitExam','{\"examId\":1}','提交考试成功，得分55分','192.168.1.101','Safari/605',1,NULL,480,'2025-06-01 14:25:10'),(8,203,'xiaogang','SUBMIT','考试模块','submitExam','{\"examId\":1}','提交考试成功，得分48分','192.168.1.102','Mobile Safari',1,NULL,520,'2025-06-01 14:28:45'),(9,1,'admin','PUBLISH','成绩管理','publishGrade','{\"examId\":1}','批量发布考试成绩，共3条','127.0.0.1','Chrome/120',1,NULL,250,'2025-06-01 15:00:00'),(10,1,'admin','CREATE','通知管理','sendNotification','{\"title\":\"成绩已发布\",\"target\":\"all_students\"}','发送通知成功，共12条','127.0.0.1','Chrome/120',1,NULL,100,'2025-06-01 15:05:00');
/*!40000 ALTER TABLE `system_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_favorite_question`
--

DROP TABLE IF EXISTS `teacher_favorite_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_favorite_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_teacher_question` (`teacher_id`,`question_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_favorite_question`
--

LOCK TABLES `teacher_favorite_question` WRITE;
/*!40000 ALTER TABLE `teacher_favorite_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher_favorite_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_info`
--

DROP TABLE IF EXISTS `teacher_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `research_direction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_info`
--

LOCK TABLES `teacher_info` WRITE;
/*!40000 ALTER TABLE `teacher_info` DISABLE KEYS */;
INSERT INTO `teacher_info` VALUES (1,101,'副教授','计算机网络、分布式系统','从事计算机网络教学与研究十余年，主讲计算机网络、操作系统等课程。','2026-06-03 17:08:07','2026-06-03 17:08:07'),(2,102,'讲师','软件工程、Java开发','专注于Java程序设计及软件工程方向的教学工作。','2026-06-03 17:08:07','2026-06-03 17:08:07'),(3,103,'??','数据结构、算法分析','数据结构与算法领域专家，发表学术论文多篇。','2026-06-03 17:08:07','2026-06-08 23:11:32'),(4,207,'讲师','?????????','?????????????????','2026-06-08 22:59:56','2026-06-08 23:03:28');
/*!40000 ALTER TABLE `teacher_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wrong_practice_record`
--

DROP TABLE IF EXISTS `wrong_practice_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wrong_practice_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `wrong_question_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `practice_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_correct` tinyint DEFAULT NULL,
  `score` decimal(6,2) DEFAULT NULL,
  `practice_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_wrong_question_id` (`wrong_question_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrong_practice_record`
--

LOCK TABLES `wrong_practice_record` WRITE;
/*!40000 ALTER TABLE `wrong_practice_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `wrong_practice_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wrong_question`
--

DROP TABLE IF EXISTS `wrong_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wrong_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'exam',
  `exam_id` bigint DEFAULT NULL,
  `exam_record_id` bigint DEFAULT NULL,
  `practice_record_id` bigint DEFAULT NULL,
  `question_id` bigint NOT NULL,
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `wrong_times` int NOT NULL DEFAULT '1',
  `last_wrong_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_favorited` tinyint NOT NULL DEFAULT '0',
  `practice_count` int NOT NULL DEFAULT '0',
  `correct_count` int NOT NULL DEFAULT '0',
  `my_solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_source_type` (`source_type`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_practice_record_id` (`practice_record_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrong_question`
--

LOCK TABLES `wrong_question` WRITE;
/*!40000 ALTER TABLE `wrong_question` DISABLE KEYS */;
INSERT INTO `wrong_question` VALUES (1,203,'exam',1,3,NULL,9,'B','A',1,'2025-06-01 14:28:45',0,0,0,NULL,'2026-06-03 17:08:09','2026-06-03 17:08:09'),(2,400,'exam',8,173,NULL,51,'B','A',1,'2026-06-06 18:20:12',1,1,1,NULL,'2026-06-06 18:15:18','2026-06-06 18:15:18'),(3,103,'exam',16,227,NULL,49,'A',NULL,1,'2026-06-07 17:31:01',0,0,0,NULL,'2026-06-07 17:31:00','2026-06-07 17:31:00'),(4,400,'exam',19,235,NULL,42,'B','A',1,'2026-06-08 21:41:41',0,0,0,NULL,'2026-06-08 21:41:41','2026-06-08 21:41:41');
/*!40000 ALTER TABLE `wrong_question` ENABLE KEYS */;
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-09 16:35:52
