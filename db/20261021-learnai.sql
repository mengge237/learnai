-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: learnai
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `learnai`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `learnai` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `learnai`;

--
-- Table structure for table `ai_interaction`
--

DROP TABLE IF EXISTS `ai_interaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_interaction` (
  `interaction_id` bigint NOT NULL AUTO_INCREMENT,
  `ai_message` text COLLATE utf8mb4_unicode_ci,
  `interaction_time` datetime(6) NOT NULL,
  `interaction_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  `topic` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `user_message` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`interaction_id`),
  KEY `IDXip6wtq9bb5dsnt0o2vh6a46vo` (`user_id`,`interaction_time`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_interaction`
--

LOCK TABLES `ai_interaction` WRITE;
/*!40000 ALTER TABLE `ai_interaction` DISABLE KEYS */;
INSERT INTO `ai_interaction` VALUES (1,'你好！我是你的 AI 学习助手，可以为你推荐学习资源、制定学习路径、分析学习进度，有什么可以帮你的吗？','2026-08-09 22:43:36.953129','chat',NULL,'问候',3,'你好'),(2,'根据你的学习记录，为你推荐以下同类热门资源：《Blender 材质与灯光》《Blender 动画制作实战》《低多边形风格化建模》，都是 Blender 入门方向的优质内容！','2026-08-10 22:43:36.953129','chat',NULL,'推荐',3,'推荐一些适合我的学习资源'),(3,'平台为你准备了多条学习路径：《Blender 零基础到进阶之路》《Web 3D 图形开发路线》《3D 游戏开发工程师路线》，建议按顺序完成路径中的资源，循序渐进。','2026-08-11 22:43:36.953129','chat',NULL,'学习路径',3,'学习路径怎么安排？'),(4,'你已完成《Blender 基础建模入门》，当前正在学习《Blender 材质与灯光》（进度 60%）和《Unity 3D 游戏开发入门》（进度 35%），继续保持！','2026-08-12 22:43:36.953129','chat',NULL,'学习进度',3,'我的学习进度怎么样？'),(5,'法线贴图（Normal Map）是一种记录表面法线方向的纹理贴图，通过扰动光照让低模表现出高模的细节，是次世代游戏开发的核心技术之一。','2026-08-13 22:43:36.953129','chat',NULL,'问题',3,'什么是法线贴图？'),(6,'不客气！学习路上有任何问题都可以随时问我，祝你学习愉快！','2026-08-14 22:43:36.953129','chat',NULL,'感谢',3,'谢谢你的帮助'),(7,'再见！期待下次和你一起学习，加油！','2026-08-15 22:43:36.953129','chat',NULL,'告别',3,'再见'),(8,'您的学习进度报告：\n\n📚 总学习资源数：4\n✅ 已完成：1\n🔄 学习中：3\n📊 平均进度：63.8%\r\n\r\n最近学习的资源：\n  - 《Blender 动画制作实战》: 进行中 (60%)\n  - 《Unity 3D 游戏开发入门》: 进行中 (35%)\n  - 《Blender 材质与灯光》: 进行中 (60%)\n\n继续加油！坚持学习，您会取得更大的进步！','2026-08-16 02:10:34.831204','chat',NULL,'学习进度',3,'我的学习进度怎么样'),(9,'根据您的学习需求，我为您推荐以下热门学习资源：\n\n1. 《Three.js 3D 网页开发》\n   - 难度：入门\n   - 时长：120分钟\n   - 作者：周杰\n\n2. 《Blender 基础建模入门》\n   - 难度：入门\n   - 时长：90分钟\n   - 作者：李明\n\n3. 《Unity 3D 游戏开发入门》\n   - 难度：入门\n   - 时长：150分钟\n   - 作者：陈静\n\n4. 《Blender 材质与灯光》\n   - 难度：初级\n   - 时长：120分钟\n   - 作者：李明\n\n5. 《Three.js 高级渲染技巧》\n   - 难度：高级\n   - 时长：140分钟\n   - 作者：周杰\n\n点击资源名称即可开始学习！','2026-08-16 03:17:38.394406','chat',NULL,'资源推荐',1,'给我推荐一些学习资源'),(10,'您的学习进度报告：\n\n总学习资源数：4\n已完成：1\n学习中：3\n平均进度：63.8%\r\n\r\n最近学习的资源：\n  - 《Blender 动画制作实战》: 进行中 (60%)\n  - 《Unity 3D 游戏开发入门》: 进行中 (35%)\n  - 《Blender 材质与灯光》: 进行中 (60%)\n\n继续加油！坚持学习，您会取得更大的进步！','2026-08-16 04:11:55.648924','chat',NULL,'学习进度',3,'我的学习进度怎么样'),(11,'你好！我是您的学习助手。请问有什么我可以帮助您的吗？无论是学习上的问题，还是需要推荐学习资源，我都很乐意为您服务！','2026-08-16 04:12:04.044067','chat',NULL,'普通对话',3,'你好'),(12,'你好！我是您的学习助手。请问有什么我可以帮助您的吗？无论是学习上的问题，还是需要推荐学习资源，我都很乐意为您服务！','2026-08-16 04:13:55.218050','chat',NULL,'普通对话',3,'你好');
/*!40000 ALTER TABLE `ai_interaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `comment_date` datetime(6) NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_approved` bit(1) NOT NULL,
  `model_id` bigint DEFAULT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'2026-08-09 23:43:36.888975','讲得特别清楚，零基础也能跟上！',_binary '',NULL,NULL,1,3),(2,'2026-08-10 23:43:36.893386','感谢支持，后续会持续更新进阶内容',_binary '',NULL,1,1,1),(3,'2026-08-10 23:43:36.893386','配套练习素材很实用，推荐！',_binary '',NULL,NULL,1,2),(4,'2026-08-11 23:43:36.893386','灯光部分讲得不错，期待材质进阶篇',_binary '',NULL,NULL,2,3),(5,'2026-08-11 23:43:36.893386','材质进阶篇已列入更新计划',_binary '',NULL,NULL,2,1),(6,'2026-08-12 23:43:36.893386','Three.js 入门首选，代码注释很详细',_binary '',NULL,NULL,12,3),(7,'2026-08-12 23:43:36.893386','跟着做完了第一个小游戏，成就感满满！',_binary '',NULL,NULL,7,3),(8,'2026-08-13 23:43:36.893386','WebGL 基础概念讲得很透彻',_binary '',NULL,NULL,10,3),(9,'2026-08-14 23:43:36.893386','内容偏高级，建议先学完基础课程',_binary '',NULL,NULL,4,2),(10,'2026-08-12 23:43:36.893386','模型面数优化得很好，引擎里跑得很流畅',_binary '',4,NULL,NULL,3),(11,'2026-08-13 23:43:36.893386','感谢购买支持，使用中有问题随时反馈',_binary '',4,NULL,NULL,1),(12,'2026-08-13 23:43:36.893386','小狐狸太可爱了，直接下单',_binary '',6,NULL,NULL,3),(13,'2026-08-14 23:43:36.893386','推荐给角色建模初学者，布线很规范',_binary '',1,NULL,NULL,1),(14,'2026-08-14 23:43:36.893386','别墅模型细节丰富，渲染效果很棒',_binary '',3,NULL,NULL,3),(15,'2026-08-16 02:10:35.286892','E2E 端到端验证评论',_binary '',NULL,NULL,2,3);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `download`
--

DROP TABLE IF EXISTS `download`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `download` (
  `download_id` bigint NOT NULL AUTO_INCREMENT,
  `download_time` datetime(6) NOT NULL,
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model_id` bigint DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`download_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `download`
--

LOCK TABLES `download` WRITE;
/*!40000 ALTER TABLE `download` DISABLE KEYS */;
INSERT INTO `download` VALUES (1,'2026-08-10 00:43:36.942993','127.0.0.1',NULL,1,3),(2,'2026-08-13 00:43:36.942993','127.0.0.1',NULL,12,3),(3,'2026-08-14 00:43:36.942993','127.0.0.1',1,NULL,3),(4,'2026-08-15 00:43:36.942993','127.0.0.1',6,NULL,3),(5,'2026-08-16 02:10:35.531014','0:0:0:0:0:0:0:1',NULL,3,3);
/*!40000 ALTER TABLE `download` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `favorite_id` bigint NOT NULL AUTO_INCREMENT,
  `added_date` datetime(6) NOT NULL,
  `model_id` bigint DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `UKd6h6tr8myjkc7qj5rarcalfu3` (`user_id`,`resource_id`),
  UNIQUE KEY `UKjvrfklr6ubr4f67ttrjtjlqq6` (`user_id`,`model_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,'2026-08-10 01:43:36.927362',NULL,1,3),(3,'2026-08-13 01:43:36.927362',NULL,12,3),(4,'2026-08-14 01:43:36.927362',1,NULL,3),(5,'2026-08-15 01:43:36.927362',3,NULL,3),(7,'2026-08-16 02:11:12.747270',NULL,2,3);
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `learning_path`
--

DROP TABLE IF EXISTS `learning_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `learning_path` (
  `path_id` bigint NOT NULL AUTO_INCREMENT,
  `cover_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_date` datetime(6) NOT NULL,
  `description` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `difficulty_level` int NOT NULL,
  `enrollment_count` int NOT NULL,
  `estimated_hours` int DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `path_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_audience` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `view_count` int NOT NULL,
  PRIMARY KEY (`path_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `learning_path`
--

LOCK TABLES `learning_path` WRITE;
/*!40000 ALTER TABLE `learning_path` DISABLE KEYS */;
INSERT INTO `learning_path` VALUES (1,NULL,'2026-07-27 01:43:36.639238','从界面认识到动画制作，系统掌握 Blender 全流程技能，适合完全零基础的学习者。',1,320,40,_binary '','Blender 零基础到进阶之路','零基础学习者、3D 爱好者',1250),(2,NULL,'2026-07-29 01:43:36.639238','Unity 入门到物理引擎再到 UE5 场景搭建，覆盖主流游戏引擎开发技能。',2,210,60,_binary '','3D 游戏开发工程师路线','有一定编程基础、想进入游戏行业的开发者',980),(3,NULL,'2026-08-01 01:43:36.639238','从 WebGL 原理到 Three.js 实战，掌握在浏览器中构建 3D 应用的全套技能。',2,180,45,_binary '','Web 3D 图形开发路线','前端开发者、网页 3D 应用爱好者',760),(4,NULL,'2026-08-04 01:43:36.639238','Maya 建模、ZBrush 雕刻与次世代纹理，打造完整的角色设计能力树。',3,95,80,_binary '','3D 角色设计大师之路','有一定建模基础、想成为角色设计师的学习者',540);
/*!40000 ALTER TABLE `learning_path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `learning_record`
--

DROP TABLE IF EXISTS `learning_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `learning_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `duration_minutes` int DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `progress` double NOT NULL,
  `resource_id` bigint NOT NULL,
  `score` int DEFAULT NULL,
  `start_time` datetime(6) NOT NULL,
  `status` enum('Abandoned','Completed','InProgress','NotStarted','Paused') COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `UKadeds3vkkp3b35x27ilyj2j9b` (`user_id`,`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `learning_record`
--

LOCK TABLES `learning_record` WRITE;
/*!40000 ALTER TABLE `learning_record` DISABLE KEYS */;
INSERT INTO `learning_record` VALUES (1,150,'2026-08-09 00:43:36.676327','系统学完了基础建模部分，收获很大！',100,1,92,'2026-08-08 21:43:36.676327','Completed',3),(2,75,NULL,'正在学习材质节点部分',60,2,NULL,'2026-08-12 21:43:36.682373','InProgress',3),(3,40,NULL,'刚完成角色控制部分',35,7,NULL,'2026-08-14 21:43:36.688403','InProgress',3),(4,NULL,NULL,'E2E',60,3,NULL,'2026-08-16 02:10:34.109630','InProgress',3),(5,NULL,NULL,NULL,0,3,NULL,'2026-08-16 02:24:06.606626','InProgress',1),(6,4,NULL,NULL,0,1,NULL,'2026-08-31 11:47:19.738938','InProgress',1);
/*!40000 ALTER TABLE `learning_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `learning_resource`
--

DROP TABLE IF EXISTS `learning_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `learning_resource` (
  `resource_id` bigint NOT NULL AUTO_INCREMENT,
  `approved_by` bigint DEFAULT NULL,
  `approved_date` datetime(6) DEFAULT NULL,
  `author` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `completion_count` int NOT NULL,
  `create_date` datetime(6) NOT NULL,
  `description` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `difficulty_level` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration_minutes` int DEFAULT NULL,
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_approved` bit(1) NOT NULL,
  `is_free` bit(1) NOT NULL,
  `is_public` bit(1) NOT NULL,
  `learning_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `like_count` int NOT NULL,
  `original_file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prerequisite_resource_id` bigint DEFAULT NULL,
  `preview_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `rejection_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resource_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resource_title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `thumbnail_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `video_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `view_count` int NOT NULL,
  `category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`resource_id`),
  KEY `FKj50irgks6kbiox16utbd28vbt` (`category_id`),
  CONSTRAINT `FKj50irgks6kbiox16utbd28vbt` FOREIGN KEY (`category_id`) REFERENCES `resource_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `learning_resource`
--

LOCK TABLES `learning_resource` WRITE;
/*!40000 ALTER TABLE `learning_resource` DISABLE KEYS */;
INSERT INTO `learning_resource` VALUES (1,2,'2026-08-16 03:43:36.220340','李明',689,'2026-08-15 22:43:36.220340','从零开始认识 Blender 界面，掌握基础网格编辑、挤出、环切等核心建模操作，完成第一个 3D 作品。','入门',90,'resources/202608/621b184a.pdf',_binary '',_binary '',_binary '','视频',412,'Blender 基础建模入门（演示资料）.pdf',NULL,'/uploads/previews/202608/621b184a.png',0.00,NULL,'RES-001','Blender 基础建模入门',NULL,NULL,3563,6),(2,2,'2026-08-15 03:43:36.220340','李明',341,'2026-08-14 22:43:36.220340','深入讲解 PBR 材质原理、节点编辑器使用与三点布光法，让你的模型质感提升一个档次。','初级',120,'resources/202608/902eda93.pdf',_binary '',_binary '',_binary '','视频',267,'Blender 材质与灯光（演示资料）.pdf',NULL,'/uploads/previews/202608/902eda93.png',0.00,NULL,'RES-002','Blender 材质与灯光',NULL,NULL,2280,6),(3,2,'2026-08-14 03:43:36.220340','王芳',220,'2026-08-13 22:43:36.220340','关键帧动画、骨骼绑定与权重绘制全流程实战，制作一段完整的角色动画短片。','中级',180,'resources/202608/0f0fb58d.pdf',_binary '',_binary '\0',_binary '','视频',189,'Blender 动画制作实战（演示资料）.pdf',1,'/uploads/previews/202608/0f0fb58d.png',29.90,NULL,'RES-003','Blender 动画制作实战',NULL,NULL,1562,6),(4,2,'2026-08-13 03:43:36.220340','张伟',143,'2026-08-12 22:43:36.220340','从概念图到高模全流程：拓扑结构规划、布线技巧、细节雕刻与 UV 展开。','高级',240,'resources/202608/d8866aba.pdf',_binary '',_binary '\0',_binary '','视频',156,'Maya 角色建模全流程（演示资料）.pdf',NULL,'/uploads/previews/202608/d8866aba.png',49.90,NULL,'RES-004','Maya 角色建模全流程',NULL,NULL,980,7),(5,2,'2026-08-12 03:43:36.220340','张伟',176,'2026-08-11 22:43:36.220340','机械硬表面建模方法论：倒角策略、布尔运算与细分曲面的平衡。','中级',60,'resources/202608/2625b35c.pdf',_binary '',_binary '',_binary '','图文',98,'Maya 硬表面建模技巧（演示资料）.pdf',NULL,'/uploads/previews/202608/2625b35c.png',0.00,NULL,'RES-005','Maya 硬表面建模技巧',NULL,NULL,1340,7),(6,2,'2026-08-11 03:43:36.220340','刘洋',156,'2026-08-10 22:43:36.220340','V-Ray 渲染器实战：材质调整、灯光布置与后期处理，产出商业级室内效果图。','中级',200,'resources/202608/0504afc9.pdf',_binary '',_binary '\0',_binary '','视频',121,'3ds Max 室内效果图渲染（演示资料）.pdf',NULL,'/uploads/previews/202608/0504afc9.png',39.90,NULL,'RES-006','3ds Max 室内效果图渲染',NULL,NULL,1120,8),(7,2,'2026-08-10 03:43:36.220340','陈静',512,'2026-08-09 22:43:36.220340','使用 Unity 引擎搭建第一个 3D 游戏：场景搭建、角色控制、碰撞检测与 UI 系统。','入门',150,'resources/202608/e8d0c645.pdf',_binary '',_binary '',_binary '','视频',389,'Unity 3D 游戏开发入门（演示资料）.pdf',NULL,'/uploads/previews/202608/e8d0c645.png',0.00,NULL,'RES-007','Unity 3D 游戏开发入门',NULL,NULL,4210,10),(8,2,'2026-08-09 03:43:36.220340','陈静',203,'2026-08-08 22:43:36.220340','Rigidbody 刚体、碰撞体与关节组件的进阶应用，附 12 个可运行练习场景。','初级',90,'resources/202608/5b9161db.pdf',_binary '',_binary '',_binary '','练习',145,'Unity 物理引擎实战（演示资料）.pdf',NULL,'/uploads/previews/202608/5b9161db.png',0.00,NULL,'RES-008','Unity 物理引擎实战',NULL,NULL,1870,10),(9,2,'2026-08-08 03:43:36.220340','赵磊',167,'2026-08-07 22:43:36.220340','Nanite 与 Lumen 两大核心特性详解，搭建电影级光影场景。','中级',220,'resources/202608/abf2efb1.pdf',_binary '',_binary '\0',_binary '','视频',201,'Unreal Engine 5 场景搭建（演示资料）.pdf',NULL,'/uploads/previews/202608/abf2efb1.png',59.90,NULL,'RES-009','Unreal Engine 5 场景搭建',NULL,NULL,1450,11),(10,2,'2026-08-07 03:43:36.220340','孙倩',289,'2026-08-06 22:43:36.220340','从渲染管线讲起：顶点着色器、片元着色器与缓冲区对象，手写第一个 WebGL 程序。','初级',100,'resources/202608/3134bc85.pdf',_binary '',_binary '',_binary '','图文',176,'WebGL 图形编程基础（演示资料）.pdf',NULL,'/uploads/previews/202608/3134bc85.png',0.00,NULL,'RES-010','WebGL 图形编程基础',NULL,NULL,2010,12),(11,2,'2026-08-06 03:43:36.220340','孙倩',108,'2026-08-05 22:43:36.220340','GLSL 语法精讲与 15 个实用着色器案例源码（渐变、噪声、水波、卡通描边等）。','中级',80,'resources/202608/e57873f8.pdf',_binary '',_binary '\0',_binary '','源码',132,'GLSL 着色器语言入门（演示资料）.pdf',NULL,'/uploads/previews/202608/e57873f8.png',19.90,NULL,'RES-011','GLSL 着色器语言入门',NULL,NULL,890,13),(12,2,'2026-08-05 03:43:36.221601','周杰',634,'2026-08-04 22:43:36.221601','零基础掌握 Three.js：场景、相机、渲染器、材质与光照，附完整项目源码。','入门',120,'resources/202608/cf43d29b.pdf',_binary '',_binary '',_binary '','源码',478,'Three.js 3D 网页开发（演示资料）.pdf',10,'/uploads/previews/202608/cf43d29b.png',0.00,NULL,'RES-012','Three.js 3D 网页开发',NULL,NULL,5232,14),(13,2,'2026-08-04 03:43:36.221601','周杰',187,'2026-08-03 22:43:36.221601','后处理特效、性能优化、阴影调参与 GLTF 工作流，进阶 Three.js 渲染表现。','高级',140,'resources/202608/131eb461.pdf',_binary '',_binary '\0',_binary '','源码',210,'Three.js 高级渲染技巧（演示资料）.pdf',NULL,'/uploads/previews/202608/131eb461.png',24.90,NULL,'RES-013','Three.js 高级渲染技巧',NULL,NULL,1670,14),(14,2,'2026-08-16 02:10:38.063098','demo',102,'2026-08-02 22:43:36.221601','Substance Painter 完整工作流：烘焙法线、智能材质与导出到引擎。','中级',160,'resources/202608/6f20d1be.pdf',_binary '',_binary '\0',_binary '','视频',95,'次世代纹理绘制实战（演示资料）.pdf',NULL,'/uploads/previews/202608/6f20d1be.png',34.90,NULL,'RES-014','次世代纹理绘制实战',NULL,NULL,760,15),(15,NULL,NULL,'demo',251,'2026-08-01 22:43:36.221601','认识 ZBrush 核心笔刷与 DynaMesh，雕刻第一个数字头像。','入门',130,'resources/202608/8f7d3180.pdf',_binary '\0',_binary '',_binary '','视频',168,'ZBrush 数字雕刻入门（演示资料）.pdf',NULL,'/uploads/previews/202608/8f7d3180.png',0.00,NULL,'RES-015','ZBrush 数字雕刻入门',NULL,NULL,1340,9),(16,NULL,NULL,'demo',129,'2026-07-31 22:43:36.221601','低多边形（Low Poly）风格化建模：色彩搭配、光影氛围与导出全流程练习。','初级',70,'resources/202608/044a1d3d.pdf',_binary '\0',_binary '',_binary '','练习',87,'低多边形风格化建模（演示资料）.pdf',NULL,'/uploads/previews/202608/044a1d3d.png',0.00,NULL,'RES-016','低多边形风格化建模',NULL,NULL,980,6);
/*!40000 ALTER TABLE `learning_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `learning_step`
--

DROP TABLE IF EXISTS `learning_step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `learning_step` (
  `step_id` bigint NOT NULL AUTO_INCREMENT,
  `completed_time` datetime(6) DEFAULT NULL,
  `duration_seconds` int DEFAULT NULL,
  `record_id` bigint NOT NULL,
  `status` enum('Completed','InProgress','NotStarted','Skipped') COLLATE utf8mb4_unicode_ci NOT NULL,
  `step_number` int NOT NULL,
  `step_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `step_content` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`step_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `learning_step`
--

LOCK TABLES `learning_step` WRITE;
/*!40000 ALTER TABLE `learning_step` DISABLE KEYS */;
INSERT INTO `learning_step` VALUES (1,'2026-08-08 22:33:36.676327',2700,1,'Completed',1,'认识 Blender 界面与视口操作','欢迎开始《Blender 基础建模入门》！本步骤先带你认识界面，学会在三维空间里自由观察。\n\n▍界面五大区域\n1. 3D 视口（Viewport）：正中央最大的区域，模型都在这里操作；\n2. 大纲视图（Outliner）：右上角，以层级树列出场景中所有物体；\n3. 属性面板（Properties）：右下角，修改器、材质、渲染等设置都在这里；\n4. 时间线（Timeline）：底部，动画相关；\n5. 顶部工具栏：模式切换、吸附、坐标轴变换等。\n\n▍视口导航（必须练到肌肉记忆）\n· 旋转视角：按住鼠标中键拖动；\n· 平移视角：Shift + 鼠标中键拖动；\n· 缩放视角：滚动滚轮；\n· 正视图切换：小键盘 1/3/7 对应前/右/顶视图，5 切换透视/正交。\n\n▍核心概念：物体模式与编辑模式\n按 Tab 键在两者间切换。物体模式操作整个物体（移动/缩放/旋转），编辑模式操作构成物体的顶点、边、面。按数字键 1/2/3 分别切换点/边/面选择。\n\n▍变换操作\nG 移动、S 缩放、R 旋转，按下后按 X/Y/Z 可锁定轴向，输入数字精确控制。记住：先选中物体，再操作。'),(2,'2026-08-08 23:23:36.676327',2700,1,'Completed',2,'网格编辑核心操作：从圆柱到咖啡杯','本步骤是《Blender 基础建模入门》的核心，跟着做一遍，你就能做出第一个完整作品——咖啡杯。\n\n▍必学的五个核心操作\n1. 挤出 Extrude（E）：把面拉伸出厚度，建模最常用的操作；\n2. 内插面 Inset（I）：在面内部生成一圈小面，做杯口、边框必备；\n3. 环切 Loop Cut（Ctrl+R）：在边环上插一圈切线，滚轮可增加段数；\n4. 倒角 Bevel（Ctrl+B）：把锋利的边磨圆，让模型更真实；\n5. 合并 Merge（M）：焊接多余的顶点，保持网格干净。\n\n▍实战：制作一个咖啡杯\n1. 新建圆柱体（Shift+A → 网格 → 圆柱），顶点数设为 16；\n2. 进入编辑模式，选中顶面，按 I 内插一圈，再按 E 向下挤出杯壁；\n3. 选中杯口与杯底的边缘环，按 Ctrl+B 倒角；\n4. 制作杯柄：新建一个小立方体，缩放成条状，旋转 45° 贴合杯身；\n5. 右键 → 平滑着色（Shade Smooth），让曲面看起来光滑。\n\n▍镜像修改器（对称建模利器）\n属性面板 → 修改器 → 添加镜像（Mirror），以物体原点为轴镜像。勾选「范围限制（Clipping）」可让对称面上的顶点自动焊接，做人脸、花瓶等对称物体效率翻倍。\n\n▍常见问题\n· 挤出的面方向歪了？检查挤出方向是否沿法线（Alt+E → 沿法线挤出）；\n· 物体中心不在想要的位置？右键 → 设置原点。'),(3,'2026-08-09 00:13:36.676327',2700,1,'Completed',3,'练习与总结：独立完成你的第一件作品','恭喜来到《Blender 基础建模入门》的最后一步！用练习检验成果，用总结沉淀知识。\n\n▍结课练习（三选一，建议全做）\n1. 复现课堂案例：不看教程，独立再做一次咖啡杯；\n2. 小房子：立方体挤出屋顶 → 环切分层 → 倒角窗框；\n3. 自由创作：用学过的五种操作，做一件身边的小物件（铅笔、水杯、手机）。\n\n▍自查清单\n· 能用键盘快捷键完成 G/S/R/E 全部操作；\n· 知道什么时候用内插面、什么时候用挤出；\n· 模型没有多余顶点、法线方向正确（Alt+N → 重新计算外侧）；\n· 能说出物体模式与编辑模式的区别。\n\n▍导出你的作品\n文件 → 导出 → FBX/OBJ。OBJ 适合通用交换，FBX 保留材质与动画信息。导出的文件可以直接上传到「模型资源库」展示你的成果！\n\n▍提交完成\n完成后点击「提交完成」打分，系统会记录你的成就并计入学习分析。下一步建议学习《Blender 材质与灯光》，给你的模型披上质感外衣。'),(4,'2026-08-12 22:28:36.682373',2700,2,'Completed',1,'PBR 材质原理：模型质感的基石','为什么别人的模型像照片，你的像塑料？秘密就在 PBR 材质。本步骤讲透它的原理。\n\n▍什么是 PBR\nPBR（Physically Based Rendering，基于物理的渲染）模拟真实世界的光与材质交互规则。在 Blender 中，一切材质都从「原理化 BSDF」节点出发。\n\n▍四个核心参数\n1. 基础色 Base Color：物体的固有色，金属的固有色也在这里设置；\n2. 金属度 Metallic：要么 0（非金属）要么 1（金属），尽量避免中间值；\n3. 粗糙度 Roughness：0 是镜面，1 是哑光，木头约 0.6-0.8，玻璃约 0.1；\n4. 法线贴图 Normal：用贴图假装出凹凸细节，低模变高模的关键。\n\n▍材质判断口诀\n「反光强、有色反射的是金属；反射永远是白色的是非金属。」拿不准时就参考真实照片，观察高光和反射的颜色。\n\n▍学习建议\n先在默认立方体上练习调参，感受每个滑块对视觉的影响，再进入下一步的节点编辑器。'),(5,NULL,2700,2,'InProgress',2,'节点编辑器与常用节点组合','本步骤进入《Blender 材质与灯光》的核心：用节点编辑器搭建材质网络。\n\n▍认识着色编辑器\n选择物体 → 底部工具栏切到 Shading 工作区。节点图从「原理化 BSDF」出发，最后连入「材质输出」。Shift+A 添加节点，Ctrl+Shift+左键可快速预览节点输出。\n\n▍必学的节点组合\n1. 贴图三件套：图像纹理（Image Texture）→ 连接基础色；注意颜色贴图用 sRGB 色彩空间，法线/粗糙度贴图用 Non-Color；\n2. 法线流程：图像纹理 → 法线贴图节点（Normal Map）→ 原理化 BSDF 的法线输入；\n3. 纹理控制：纹理坐标（Texture Coordinate）→ 映射（Mapping）节点，用来旋转、缩放、平铺贴图；\n4. 混合：混合着色器（Mix Shader）把两种材质按系数混合，做做旧、锈迹效果。\n\n▍UV 展开基础\n模型需要有 UV 贴图才能正确显示图像纹理。选中模型 → Tab 进入编辑模式 →U 键 → 智能 UV 投影（Smart UV Project）作为快速方案；正式流程是标记缝合边（Mark Seam）再展开。\n\n▍常见问题\n· 贴图发紫？说明贴图文件路径丢失，在图像纹理节点里重新加载；\n· 贴图拉伸？UV 未展开或接缝位置不当，重新展开 UV。'),(6,NULL,2700,2,'NotStarted',3,'三点布光法与渲染输出','最后一步：给场景打光并渲染。灯光决定氛围，同样的模型在不同灯光下判若两人。\n\n▍三点布光法（产品展示万能公式）\n1. 主光 Key Light：面光或聚光，从 45° 上方照亮主体，强度最高；\n2. 辅光 Fill Light：从另一侧打弱光（约主光 30%），消除死黑阴影；\n3. 轮廓光 Rim Light：从背后打向主体边缘（约主光 60%），把主体从背景中「抠」出来。\n\n▍HDRI 环境贴图\n世界属性 → 颜色 → 环境纹理，加载一张 .hdr 即可获得真实的环境光照与反射。免费 HDRI 可从 Poly Haven 下载。\n\n▍Eevee 还是 Cycles\n· Eevee：实时渲染，速度快，适合预览与动画；\n· Cycles：光线追踪，光影真实，适合静帧作品与产品图。\n\n▍渲染输出设置\n输出属性：分辨率 1920×1080、格式 PNG、开启降噪（Denoise）。按 F12 渲染，Ctrl+S 保存成品图。\n\n▍提交完成\n为本次学习打分后，建议继续《Blender 动画制作实战》，让模型动起来。'),(7,'2026-08-14 22:28:36.688403',2700,3,'Completed',1,'Unity 界面与场景搭建','欢迎进入游戏开发的世界！本步骤带你认识 Unity，并搭出第一个游戏场景。\n\n▍五大核心面板\n1. 场景视图（Scene）：搭场景的画布；\n2. 游戏视图（Game）：玩家看到的效果；\n3. 层级面板（Hierarchy）：场景中所有物体列表；\n4. 检查器（Inspector）：选中物体的属性面板；\n5. 项目窗口（Project）：所有资源文件。\n\n▍核心思想：一切都是组件\nGameObject 本身只是一个空壳，给它挂上「组件」才有功能：网格渲染器（Mesh Renderer）让它可见，碰撞体（Collider）让它能碰撞，脚本组件让它有自己的行为。\n\n▍第一个场景\n1. 新建平面（Plane）当地面；\n2. 放几个立方体当障碍物；\n3. 选中物体按 W/E/R 切换移动/旋转/缩放；\n4. 创建一盏方向光（Directional Light），场景立刻立体起来。\n\n▍Transform 组件\n所有物体都有 Transform：位置（Position）、旋转（Rotation）、缩放（Scale），是 3D 空间的身份证。\n\n▍学习建议\n没事就在场景里摆弄物体，把界面摸熟，下一步就写代码。'),(8,NULL,2700,3,'InProgress',2,'角色控制与物理碰撞','本步骤进入《Unity 3D 游戏开发入门》核心：写第一个脚本，让角色动起来。\n\n▍角色控制两种方案\n1. 角色控制器（Character Controller）：自带碰撞与爬坡，适合人形角色；\n2. 刚体 Rigidbody + 胶囊碰撞体：受物理引擎驱动，适合需要弹跳、被撞飞的玩法。\n\n▍第一个脚本 MovePlayer\n新建 C# 脚本，在 Update 里用 Input.GetAxis(\"Horizontal\") 读取键盘输入，移动 Transform 或设置刚体速度。写完拖到角色身上，按 Play 测试。\n\n▍碰撞检测\n· 碰撞体（Collider）是碰撞的基础，双方都要有；\n· 触发器（Is Trigger）：只检测不阻挡，做道具拾取、进入区域；\n· OnCollisionEnter：发生碰撞瞬间调用；\n· OnTriggerEnter：进入触发器区域调用。\n\n▍常见错误\n· NullReferenceException 空引用：脚本里引用的物体没在检查器里拖进去；\n· 物体穿过地面：忘了给地面加碰撞体；\n· 角色抽搐：不要在 Update 里直接改刚体位置，要用物理函数。\n\n▍实践任务\n做一个「收集金币」小玩法：角色移动 + 金币触发器 + 碰到即消失。'),(9,NULL,2700,3,'NotStarted',3,'UI 系统与打包发布','最后一步：加上 UI，把小游戏打包成能分享的成品。\n\n▍UI 系统（UGUI）\n1. 所有 UI 元素放在 Canvas 画布下，Canvas 决定渲染层级；\n2. 常用元素：文本（Text）、按钮（Button）、图片（Image）；\n3. Canvas Scaler 组件做屏幕适配，UI 在不同分辨率下不跑偏。\n\n▍得分系统\n脚本里维护一个 int score，拾取金币时 +1，UI 文本实时刷新；再写个 GameManager 管理游戏状态（开始/结束/重开）。\n\n▍打包发布\nFile → Build Settings：\n1. 把当前场景加入构建列表（Add Open Scenes）；\n2. 平台选 PC（Windows）；\n3. 点 Build 输出 exe，双击就能玩！\n\n▍结课练习\n完成「收集金币」小游戏：有角色控制、有碰撞拾取、有得分 UI、能打包运行。\n\n▍提交完成\n打分后建议学习《Unity 物理引擎实战》，做出更真实的物理玩法。'),(10,'2026-08-16 02:10:34.365170',NULL,4,'Completed',1,'关键帧动画基础：让时间流动起来','动画的本质是「在时间轴上记录变化」。本步骤建立关键帧思维。\n\n▍关键帧（Keyframe）概念\n在某一帧记录物体的状态（位置/旋转/缩放/任何属性），Blender 会自动计算两帧之间的过渡——这就是插值。\n\n▍操作要点\n1. 把时间线指针移到目标帧，移动物体后按 I 插入关键帧；\n2. 常用插值：贝塞尔（默认，平滑）、线性（匀速）、常量（跳变）；\n3. 开启自动关键帧（Auto Keying）后，改属性会自动记录；\n4. 曲线编辑器（Graph Editor）里拖动曲线手柄，微调缓入缓出。\n\n▍动画十二法则入门\n· 挤压与拉伸（Squash & Stretch）：弹跳的小球落地瞬间压扁；\n· 缓入缓出（Slow In & Slow Out）：动作起停都要有加减速；\n· 预备动作（Anticipation）：起跳前先下蹲。\n\n▍练习：弹跳小球\n一个球体，20 帧一个弹跳周期，下落加速、触地压扁、弹起拉伸，循环三次。这个小练习涵盖了最核心的动画手感。'),(11,NULL,NULL,4,'NotStarted',2,'骨骼绑定与权重绘制','让角色动起来，需要「骨架 + 蒙皮」。本步骤是角色动画的地基。\n\n▍骨架（Armature）基础\nShift+A → 骨架 → 单根骨骼。编辑模式里从骨骼尾端按 E 挤出子骨骼，搭出腿、躯干、手臂的层级。给骨骼命名（如 Leg.L / Leg.R）方便镜像。\n\n▍蒙皮：让网格跟随骨骼\n1. 先选网格，再 Shift 选中骨架，Ctrl+P → 附带自动权重（Automatic Weights）；\n2. Blender 自动计算每根骨骼对顶点的影响权重；\n3. 切到姿态模式（Pose Mode）旋转骨骼，检查形变是否自然。\n\n▍权重绘制（Weight Paint）\n自动权重不完美时手动修正：选中网格切到权重绘制模式，红色 = 完全受该骨骼控制，蓝色 = 完全不受控。关节处应平滑过渡（黄绿色）。\n\n▍IK 约束（反向运动学）\n给腿骨链加 IK 约束：移动脚部控制器，膝盖自动弯曲。绑定角色时先摆「T 字形」（T-Pose）再蒙皮。\n\n▍常见问题\n· 蒙皮后模型被拉歪？先应用模型变换（Ctrl+A → 全部变换）再绑定；\n· 关节处破面？去权重绘制模式刷低权重。'),(12,NULL,NULL,4,'NotStarted',3,'角色动画短片全流程实战','最后一步：把所学串成一段完整的角色动画。\n\n▍动画制作三阶段\n1. Blocking（关键姿势）：只摆每秒钟最重要的姿势，确认节奏与表演；\n2. Breakdown（分解姿势）：在关键姿势之间补充过渡姿势；\n3. In-between（中间帧）：让曲线编辑器里的插值平滑自然。\n\n▍循环行走动画\n行走周期 24 帧：接触 → 下压 → 过渡 → 蹬地。摆好 4 个关键姿势后，用「循环修改器」让动画无限重复。\n\n▍相机与运镜\n给相机加「跟随路径」约束，沿贝塞尔曲线移动；镜头语言：远景交代环境、中景表演动作、特写强调表情。\n\n▍渲染成片\n输出属性 → PNG 序列（不要直接输出视频，防止中途出错全部重渲），然后在视频编辑器中合成 MP4。\n\n▍提交完成\n打分提交后，你的动画学习之旅就正式起步了。建议接着学习《Maya 角色建模全流程》或上传你的动画作品到模型资源库展示。'),(13,NULL,NULL,5,'InProgress',1,'关键帧动画基础：让时间流动起来','动画的本质是「在时间轴上记录变化」。本步骤建立关键帧思维。\n\n▍关键帧（Keyframe）概念\n在某一帧记录物体的状态（位置/旋转/缩放/任何属性），Blender 会自动计算两帧之间的过渡——这就是插值。\n\n▍操作要点\n1. 把时间线指针移到目标帧，移动物体后按 I 插入关键帧；\n2. 常用插值：贝塞尔（默认，平滑）、线性（匀速）、常量（跳变）；\n3. 开启自动关键帧（Auto Keying）后，改属性会自动记录；\n4. 曲线编辑器（Graph Editor）里拖动曲线手柄，微调缓入缓出。\n\n▍动画十二法则入门\n· 挤压与拉伸（Squash & Stretch）：弹跳的小球落地瞬间压扁；\n· 缓入缓出（Slow In & Slow Out）：动作起停都要有加减速；\n· 预备动作（Anticipation）：起跳前先下蹲。\n\n▍练习：弹跳小球\n一个球体，20 帧一个弹跳周期，下落加速、触地压扁、弹起拉伸，循环三次。这个小练习涵盖了最核心的动画手感。'),(14,NULL,NULL,5,'InProgress',2,'骨骼绑定与权重绘制','让角色动起来，需要「骨架 + 蒙皮」。本步骤是角色动画的地基。\n\n▍骨架（Armature）基础\nShift+A → 骨架 → 单根骨骼。编辑模式里从骨骼尾端按 E 挤出子骨骼，搭出腿、躯干、手臂的层级。给骨骼命名（如 Leg.L / Leg.R）方便镜像。\n\n▍蒙皮：让网格跟随骨骼\n1. 先选网格，再 Shift 选中骨架，Ctrl+P → 附带自动权重（Automatic Weights）；\n2. Blender 自动计算每根骨骼对顶点的影响权重；\n3. 切到姿态模式（Pose Mode）旋转骨骼，检查形变是否自然。\n\n▍权重绘制（Weight Paint）\n自动权重不完美时手动修正：选中网格切到权重绘制模式，红色 = 完全受该骨骼控制，蓝色 = 完全不受控。关节处应平滑过渡（黄绿色）。\n\n▍IK 约束（反向运动学）\n给腿骨链加 IK 约束：移动脚部控制器，膝盖自动弯曲。绑定角色时先摆「T 字形」（T-Pose）再蒙皮。\n\n▍常见问题\n· 蒙皮后模型被拉歪？先应用模型变换（Ctrl+A → 全部变换）再绑定；\n· 关节处破面？去权重绘制模式刷低权重。'),(15,NULL,NULL,5,'InProgress',3,'角色动画短片全流程实战','最后一步：把所学串成一段完整的角色动画。\n\n▍动画制作三阶段\n1. Blocking（关键姿势）：只摆每秒钟最重要的姿势，确认节奏与表演；\n2. Breakdown（分解姿势）：在关键姿势之间补充过渡姿势；\n3. In-between（中间帧）：让曲线编辑器里的插值平滑自然。\n\n▍循环行走动画\n行走周期 24 帧：接触 → 下压 → 过渡 → 蹬地。摆好 4 个关键姿势后，用「循环修改器」让动画无限重复。\n\n▍相机与运镜\n给相机加「跟随路径」约束，沿贝塞尔曲线移动；镜头语言：远景交代环境、中景表演动作、特写强调表情。\n\n▍渲染成片\n输出属性 → PNG 序列（不要直接输出视频，防止中途出错全部重渲），然后在视频编辑器中合成 MP4。\n\n▍提交完成\n打分提交后，你的动画学习之旅就正式起步了。建议接着学习《Maya 角色建模全流程》或上传你的动画作品到模型资源库展示。'),(16,NULL,NULL,6,'NotStarted',1,'认识 Blender 界面与视口操作','欢迎开始《Blender 基础建模入门》！本步骤先带你认识界面，学会在三维空间里自由观察。\n\n▍界面五大区域\n1. 3D 视口（Viewport）：正中央最大的区域，模型都在这里操作；\n2. 大纲视图（Outliner）：右上角，以层级树列出场景中所有物体；\n3. 属性面板（Properties）：右下角，修改器、材质、渲染等设置都在这里；\n4. 时间线（Timeline）：底部，动画相关；\n5. 顶部工具栏：模式切换、吸附、坐标轴变换等。\n\n▍视口导航（必须练到肌肉记忆）\n· 旋转视角：按住鼠标中键拖动；\n· 平移视角：Shift + 鼠标中键拖动；\n· 缩放视角：滚动滚轮；\n· 正视图切换：小键盘 1/3/7 对应前/右/顶视图，5 切换透视/正交。\n\n▍核心概念：物体模式与编辑模式\n按 Tab 键在两者间切换。物体模式操作整个物体（移动/缩放/旋转），编辑模式操作构成物体的顶点、边、面。按数字键 1/2/3 分别切换点/边/面选择。\n\n▍变换操作\nG 移动、S 缩放、R 旋转，按下后按 X/Y/Z 可锁定轴向，输入数字精确控制。记住：先选中物体，再操作。'),(17,NULL,NULL,6,'InProgress',2,'网格编辑核心操作：从圆柱到咖啡杯','本步骤是《Blender 基础建模入门》的核心，跟着做一遍，你就能做出第一个完整作品——咖啡杯。\n\n▍必学的五个核心操作\n1. 挤出 Extrude（E）：把面拉伸出厚度，建模最常用的操作；\n2. 内插面 Inset（I）：在面内部生成一圈小面，做杯口、边框必备；\n3. 环切 Loop Cut（Ctrl+R）：在边环上插一圈切线，滚轮可增加段数；\n4. 倒角 Bevel（Ctrl+B）：把锋利的边磨圆，让模型更真实；\n5. 合并 Merge（M）：焊接多余的顶点，保持网格干净。\n\n▍实战：制作一个咖啡杯\n1. 新建圆柱体（Shift+A → 网格 → 圆柱），顶点数设为 16；\n2. 进入编辑模式，选中顶面，按 I 内插一圈，再按 E 向下挤出杯壁；\n3. 选中杯口与杯底的边缘环，按 Ctrl+B 倒角；\n4. 制作杯柄：新建一个小立方体，缩放成条状，旋转 45° 贴合杯身；\n5. 右键 → 平滑着色（Shade Smooth），让曲面看起来光滑。\n\n▍镜像修改器（对称建模利器）\n属性面板 → 修改器 → 添加镜像（Mirror），以物体原点为轴镜像。勾选「范围限制（Clipping）」可让对称面上的顶点自动焊接，做人脸、花瓶等对称物体效率翻倍。\n\n▍常见问题\n· 挤出的面方向歪了？检查挤出方向是否沿法线（Alt+E → 沿法线挤出）；\n· 物体中心不在想要的位置？右键 → 设置原点。'),(18,NULL,NULL,6,'InProgress',3,'练习与总结：独立完成你的第一件作品','恭喜来到《Blender 基础建模入门》的最后一步！用练习检验成果，用总结沉淀知识。\n\n▍结课练习（三选一，建议全做）\n1. 复现课堂案例：不看教程，独立再做一次咖啡杯；\n2. 小房子：立方体挤出屋顶 → 环切分层 → 倒角窗框；\n3. 自由创作：用学过的五种操作，做一件身边的小物件（铅笔、水杯、手机）。\n\n▍自查清单\n· 能用键盘快捷键完成 G/S/R/E 全部操作；\n· 知道什么时候用内插面、什么时候用挤出；\n· 模型没有多余顶点、法线方向正确（Alt+N → 重新计算外侧）；\n· 能说出物体模式与编辑模式的区别。\n\n▍导出你的作品\n文件 → 导出 → FBX/OBJ。OBJ 适合通用交换，FBX 保留材质与动画信息。导出的文件可以直接上传到「模型资源库」展示你的成果！\n\n▍提交完成\n完成后点击「提交完成」打分，系统会记录你的成就并计入学习分析。下一步建议学习《Blender 材质与灯光》，给你的模型披上质感外衣。');
/*!40000 ALTER TABLE `learning_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `model3d`
--

DROP TABLE IF EXISTS `model3d`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `model3d` (
  `model_id` bigint NOT NULL AUTO_INCREMENT,
  `approved_by` bigint DEFAULT NULL,
  `approved_date` datetime(6) DEFAULT NULL,
  `create_date` datetime(6) NOT NULL,
  `creator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_approved` bit(1) NOT NULL,
  `is_public` bit(1) NOT NULL,
  `model_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `original_file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `preview_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `rejection_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`model_id`),
  KEY `FKnt1uo0cdcupeqeabaokeeoyse` (`category_id`),
  CONSTRAINT `FKnt1uo0cdcupeqeabaokeeoyse` FOREIGN KEY (`category_id`) REFERENCES `model_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `model3d`
--

LOCK TABLES `model3d` WRITE;
/*!40000 ALTER TABLE `model3d` DISABLE KEYS */;
INSERT INTO `model3d` VALUES (1,2,'2026-07-27 02:43:36.691976','2026-07-26 20:43:36.691976','晨曦工作室','models/202608/137cd747.obj',_binary '',_binary '','MOD-001','卡通风格少女角色','卡通风格少女角色（演示模型）.obj','/uploads/previews/202608/137cd747.png',0.00,NULL,4),(2,2,'2026-07-29 02:43:36.691976','2026-07-28 20:43:36.691976','晨曦工作室','models/202608/7591ae50.obj',_binary '',_binary '','MOD-002','低多边形战士角色','低多边形战士角色（演示模型）.obj','/uploads/previews/202608/7591ae50.png',19.90,NULL,4),(3,2,'2026-07-31 02:43:36.691976','2026-07-30 20:43:36.691976','筑梦空间','models/202608/1d632532.obj',_binary '',_binary '','MOD-003','现代简约别墅','现代简约别墅（演示模型）.obj','/uploads/previews/202608/1d632532.png',49.90,NULL,5),(4,2,'2026-08-01 02:43:36.691976','2026-07-31 20:43:36.691976','星环工坊','models/202608/f2c402c8.obj',_binary '',_binary '','MOD-004','未来科幻战斗机','未来科幻战斗机（演示模型）.obj','/uploads/previews/202608/f2c402c8.png',99.50,NULL,1),(5,2,'2026-08-02 02:43:36.691976','2026-08-01 20:43:36.691976','齿轮公社','models/202608/e837c731.obj',_binary '',_binary '','MOD-005','蒸汽朋克机器人','蒸汽朋克机器人（演示模型）.obj','/uploads/previews/202608/e837c731.png',29.90,NULL,2),(6,2,'2026-08-03 02:43:36.691976','2026-08-02 20:43:36.691976','萌物制造','models/202608/8118d67e.obj',_binary '',_binary '','MOD-006','卡通小狐狸','卡通小狐狸（演示模型）.obj','/uploads/previews/202608/8118d67e.png',9.90,NULL,3),(7,NULL,NULL,'2026-08-03 20:43:36.691976','demo','models/202608/6f37a91a.obj',_binary '\0',_binary '','MOD-007','森林场景资源包','森林场景资源包（演示模型）.obj','/uploads/previews/202608/6f37a91a.png',59.90,NULL,6),(8,NULL,NULL,'2026-08-04 20:43:36.691976','demo','models/202608/597362c6.obj',_binary '\0',_binary '','MOD-008','中式园林建筑群','中式园林建筑群（演示模型）.obj','/uploads/previews/202608/597362c6.png',89.90,NULL,5);
/*!40000 ALTER TABLE `model3d` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `model_category`
--

DROP TABLE IF EXISTS `model_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `model_category` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `model_category`
--

LOCK TABLES `model_category` WRITE;
/*!40000 ALTER TABLE `model_category` DISABLE KEYS */;
INSERT INTO `model_category` VALUES (1,'交通工具','车辆飞行器等交通工具模型'),(2,'机械模型','机械与载具部件模型'),(3,'动物模型','写实与卡通动物模型'),(4,'角色模型','人物与动物角色 3D 模型'),(5,'建筑模型','建筑与室内场景模型'),(6,'场景模型','游戏与影视场景资源包');
/*!40000 ALTER TABLE `model_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `model_order`
--

DROP TABLE IF EXISTS `model_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `model_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `order_date` datetime(6) NOT NULL,
  `recipient_address` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `recipient_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `recipient_phone` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('Cancelled','Completed','Pending','PendingPayment','Processing','Shipped') COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `model_order`
--

LOCK TABLES `model_order` WRITE;
/*!40000 ALTER TABLE `model_order` DISABLE KEYS */;
INSERT INTO `model_order` VALUES (1,'2026-08-05 23:43:36.977145','广东省广州市天河区演示路 1 号','演示用户','13800001003','Completed',208.90,3),(2,'2026-08-12 23:43:36.984933','广东省广州市天河区演示路 1 号','演示用户','13800001003','Shipped',19.90,3),(3,'2026-08-13 23:43:36.987079','广东省广州市天河区演示路 1 号','演示用户','13800001003','Pending',9.90,3),(4,'2026-08-14 23:43:36.988453','广东省广州市天河区演示路 1 号','演示用户','13800001003','PendingPayment',49.90,3),(5,'2026-08-16 02:10:36.100788','北京市海淀区','张三','13800138000','Completed',0.00,3);
/*!40000 ALTER TABLE `model_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `order_item_id` bigint NOT NULL AUTO_INCREMENT,
  `license_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`order_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,'商用',4,1,2,99.50),(2,'个人',6,1,1,9.90),(3,'个人',2,2,1,19.90),(4,'个人',6,3,1,9.90),(5,'个人',3,4,1,49.90),(6,'个人',1,5,2,0.00);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `path_resource`
--

DROP TABLE IF EXISTS `path_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `path_resource` (
  `path_resource_id` bigint NOT NULL AUTO_INCREMENT,
  `path_id` bigint NOT NULL,
  `resource_id` bigint NOT NULL,
  `sequence_number` int NOT NULL,
  PRIMARY KEY (`path_resource_id`),
  UNIQUE KEY `UK2gu316wso72yjx7isy8rjv2i1` (`path_id`,`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `path_resource`
--

LOCK TABLES `path_resource` WRITE;
/*!40000 ALTER TABLE `path_resource` DISABLE KEYS */;
INSERT INTO `path_resource` VALUES (1,1,1,1),(2,1,2,2),(3,1,16,3),(4,1,3,4),(5,2,7,1),(6,2,8,2),(7,2,9,3),(8,3,10,1),(9,3,11,2),(10,3,12,3),(11,3,13,4),(12,4,4,1),(13,4,5,2),(14,4,15,3),(15,4,14,4);
/*!40000 ALTER TABLE `path_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_category`
--

DROP TABLE IF EXISTS `resource_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resource_category` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `parent_category_id` bigint DEFAULT NULL,
  `sort_order` int NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_category`
--

LOCK TABLES `resource_category` WRITE;
/*!40000 ALTER TABLE `resource_category` DISABLE KEYS */;
INSERT INTO `resource_category` VALUES (1,'3D建模','三维建模基础与高级技巧',_binary '',NULL,1),(2,'游戏开发','游戏引擎与交互开发',_binary '',NULL,2),(3,'图形学','计算机图形学与渲染',_binary '',NULL,3),(4,'前端开发','Web 前端与 3D 网页开发',_binary '',NULL,4),(5,'平面设计','贴图绘制与视觉设计',_binary '',NULL,5),(6,'Blender 入门','Blender 建模基础教程',_binary '',1,1),(7,'Maya 实战','Maya 案例实战教程',_binary '',1,2),(8,'3ds Max','3ds Max 效果图与渲染',_binary '',1,3),(9,'ZBrush 雕刻','数字雕刻入门与进阶',_binary '',1,4),(10,'Unity 基础','Unity 引擎 3D 游戏开发',_binary '',2,1),(11,'UE5 入门','虚幻引擎 5 场景与蓝图',_binary '',2,2),(12,'WebGL','原生 WebGL 图形编程',_binary '',3,1),(13,'着色器','GLSL/HLSL 着色器语言',_binary '',3,2),(14,'Three.js','Three.js 3D 网页开发',_binary '',4,1),(15,'纹理绘制','次世代纹理与 Substance',_binary '',5,1);
/*!40000 ALTER TABLE `resource_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_log`
--

DROP TABLE IF EXISTS `study_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT,
  `minutes` int NOT NULL,
  `study_date` date NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `UK4psbn25qs8o9kw7rdv3c6pbib` (`user_id`,`study_date`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_log`
--

LOCK TABLES `study_log` WRITE;
/*!40000 ALTER TABLE `study_log` DISABLE KEYS */;
INSERT INTO `study_log` VALUES (1,1,'2026-08-16','2026-08-16 03:37:02.862895',3),(2,4,'2026-08-31','2026-08-31 11:49:25.959632',1);
/*!40000 ALTER TABLE `study_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_learning_path`
--

DROP TABLE IF EXISTS `user_learning_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_learning_path` (
  `user_path_id` bigint NOT NULL AUTO_INCREMENT,
  `completed_date` datetime(6) DEFAULT NULL,
  `enroll_date` datetime(6) NOT NULL,
  `path_id` bigint NOT NULL,
  `progress` double NOT NULL,
  `status` enum('Completed','InProgress','NotStarted','Paused') COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`user_path_id`),
  UNIQUE KEY `UK9gxlhjpsqis4uw0uurl8s06vu` (`user_id`,`path_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_learning_path`
--

LOCK TABLES `user_learning_path` WRITE;
/*!40000 ALTER TABLE `user_learning_path` DISABLE KEYS */;
INSERT INTO `user_learning_path` VALUES (1,NULL,'2026-08-06 01:43:36.668343',1,50,'InProgress',3),(2,NULL,'2026-08-10 01:43:36.668343',2,33,'InProgress',3);
/*!40000 ALTER TABLE `user_learning_path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `authority` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `UK5m6djlf6toudifyf07bh74ich` (`authority`),
  UNIQUE KEY `UKjyy1ia186jxs085pd7ry00blj` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'ROLE_ADMIN','管理员'),(2,'ROLE_AUDITOR','审核员'),(3,'ROLE_USER','普通用户');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `animation_speed` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `bio` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `border_color` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `dark_mode` bit(1) NOT NULL,
  `default_shipping_address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `font_size` int NOT NULL,
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sidebar_position` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `theme_color` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_id` bigint NOT NULL,
  `student_no` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `theme_mode` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKqjp6iwe2anthe5yx88fl0coan` (`role_id`),
  CONSTRAINT `FKqjp6iwe2anthe5yx88fl0coan` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'slow','平台管理员',NULL,'#d0d0d0',NULL,'2026-07-17 01:43:36.014836',_binary '',NULL,'admin@learnai.com',18,'男',_binary '','北京市','$2a$10$pXyI04pamw1SBSCF3pQTJehmxfL3zcNlnqq5Gn1a6GTe.B2sqUacC','13800001001',NULL,'left','#e8590c','admin',1,'T0001','dark'),(2,'normal','内容审核专员',NULL,'#d0d0d0',NULL,'2026-07-17 01:43:36.084407',_binary '\0',NULL,'auditor@learnai.com',18,'女',_binary '','上海市','$2a$10$pT1S0gDa2Yd0nbfJ3rqOV.SpcnC996x7hw0RUrXQmGTjrbZeT2a/m','13800001002',NULL,'left','#e8590c','auditor',2,'T0002','light'),(3,'normal','热爱 3D 建模的学习者','2002-05-20','#d0d0d0','广州市','2026-07-17 01:43:36.153658',_binary '\0','广东省广州市天河区演示路 1 号','demo@learnai.com',18,'男',_binary '','广东省广州市','$2a$10$WunszlJxstDIhvlL/I9dBevYVP9SpecZiKe3gY2sRaSQmg3D9moqW','13800001003','广东省','left','#e8590c','demo',3,'2026010016','light'),(4,'normal',NULL,NULL,'#dee2e6',NULL,'2026-08-16 02:10:36.937765',_binary '\0',NULL,'e2e1786817436@test.com',18,NULL,_binary '',NULL,'$2a$10$diHzy69ILO30sUK.XE3owOZNlnTHyoIskb/2IVApazDe4G7ie./Ym',NULL,NULL,'left','#007bff','e2e1786817436',3,NULL,'light');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'learnai'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-21 19:34:22
