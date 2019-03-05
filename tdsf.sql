/*
Navicat MySQL Data Transfer

Source Server         : tdsf
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : tdsf

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2019-03-05 19:43:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `article_id` int(11) NOT NULL AUTO_INCREMENT,
  `article_name` varchar(255) NOT NULL,
  `article_content` text NOT NULL COMMENT '正文内容',
  `article_type` int(30) NOT NULL,
  `article_creat_time` varchar(30) NOT NULL,
  `article_lastchange_time` varchar(30) NOT NULL COMMENT '最后修改时间',
  `article_lable` int(11) DEFAULT NULL,
  `article_ view_number` int(11) DEFAULT NULL,
  `article_comments_ number` int(11) DEFAULT NULL,
  PRIMARY KEY (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of article
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` char(255) DEFAULT NULL,
  `user_password` char(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1', '1', '0000.');
