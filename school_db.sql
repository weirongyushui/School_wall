/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : school_db

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 18/05/2026 22:49:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for login
-- ----------------------------
DROP TABLE IF EXISTS `login`;
CREATE TABLE `login`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `login_ibfk_1`(`user_id` ASC) USING BTREE,
  CONSTRAINT `login_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login
-- ----------------------------
INSERT INTO `login` VALUES (1, 'weirongyushui', 'Logic031209', 244010215, '2026-05-04 13:14:42', '2026-05-04 13:14:42');
INSERT INTO `login` VALUES (2, 'jjr', '15001194828', 244010120, '2026-05-05 14:41:31', '2026-05-05 14:41:35');
INSERT INTO `login` VALUES (3, 'test', '25f9e794323b453885f5181f1b624d0b', 244010216, '2026-05-12 08:06:45', '2026-05-12 08:06:45');

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子正文内容',
  `images` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片URL，多张用英文逗号分隔，如：url1,url2,url3',
  `user_id` bigint NOT NULL COMMENT '发布者ID，关联user表的id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '冗余字段：发布者用户名，发帖时写入，后续用户改名不同步',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '冗余字段：发布者头像URL，发帖时写入',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否匿名：0-实名 1-匿名',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '帖子状态：1-正常 0-已删除 2-已屏蔽',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数，冗余字段，点赞取消时同步更新',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数，冗余字段，新增删除评论时同步更新',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `category` smallint NOT NULL COMMENT '分区，0-全部，1-发发牢骚 2-吹吹牛皮 3-校园互助 4-联名请愿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status_created`(`status` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '校园墙帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, '123456789', NULL, 244010216, 'test', 'http://localhost:8081/static/app01/images/default_avatar.jpg', 0, 1, 0, 0, '2026-05-12 22:52:52', '2026-05-14 12:04:56', 1);
INSERT INTO `post` VALUES (2, '测试', NULL, 244010216, 'test', 'http://localhost:8081/static/app01/images/default_avatar.jpg', 0, 1, 0, 0, '2026-05-14 11:58:00', '2026-05-14 12:05:13', 1);
INSERT INTO `post` VALUES (3, '测试输出', NULL, 244010216, 'test', 'http://localhost:8081/static/app01/images/default_avatar.jpg', 0, 1, 0, 0, '2026-05-18 15:12:37', '2026-05-18 15:13:51', 1);
INSERT INTO `post` VALUES (4, '你好这里是校园墙', NULL, 244010216, 'test', 'http://localhost:8081/static/app01/images/default_avatar.jpg', 0, 1, 0, 0, '2026-05-18 15:17:26', '2026-05-18 15:18:10', 1);
INSERT INTO `post` VALUES (5, '校园墙', NULL, 244010216, 'test', 'http://localhost:8081/static/app01/images/default_avatar.jpg', 0, 1, 0, 0, '2026-05-18 15:18:39', '2026-05-18 15:18:39', 1);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` int NULL DEFAULT NULL,
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `grade` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `birthday` datetime(6) NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `qq` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `wechat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址（如宿舍/院系/城市等）',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户个人信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 244010215, '威荣与水', 'http://localhost:63342/web/static/templates/updated_avatar.jpg', 1, '计算机科学与技术', '24', 'the world', '2026-04-01 00:00:00.000000', '12345678978', '123456799@163.com', '85278542', 'sa', '', '2026-04-04 15:46:10', '2026-04-26 23:16:46');
INSERT INTO `user` VALUES (2, 244010120, 'jjr', NULL, 1, '计算机科学与技术', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-05 15:56:50', '2026-05-05 15:56:54');
INSERT INTO `user` VALUES (7, 244010216, 'test', 'http://localhost:8081/static/app01/images/default_avatar.jpg', 1, '', '', '', NULL, '', '', '', '', '', '2026-05-12 08:06:45', '2026-05-12 16:07:11');

SET FOREIGN_KEY_CHECKS = 1;
