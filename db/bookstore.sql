
/*
Navicat MySQL Data Transfer

Source Server         : Tencent
Source Server Version : 50717
Source Host           : 115.159.122.41:3306
Source Database       : bookstore

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-06-16 10:35:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `addr_id` int(11) NOT NULL,
  `province` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `latitude` double(13,10) DEFAULT NULL,
  `longtitude` double(13,10) DEFAULT NULL,
  `geohash` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`addr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES ('0', '0', '0', '0.0000000000', '0.0000000000', '0');
INSERT INTO `address` VALUES ('1', null, null, '31.2367400000', '121.4134050000', 'wtw3dy6x');
INSERT INTO `address` VALUES ('2', null, null, '31.2367370000', '121.4133810000', 'wtw3dy6x');
INSERT INTO `address` VALUES ('3', null, null, '31.2360420000', '121.4096970000', 'wtw3dwrg');
INSERT INTO `address` VALUES ('4', null, null, '31.2360000000', '121.4096750000', 'wtw3dwrf');

-- ----------------------------
-- Table structure for badbook
-- ----------------------------
DROP TABLE IF EXISTS `badbook`;
CREATE TABLE `badbook` (
  `bad_id` int(11) NOT NULL,
  `book_id` int(11) DEFAULT NULL,
  `report_user` int(11) DEFAULT NULL,
  `time` date DEFAULT NULL,
  PRIMARY KEY (`bad_id`),
  KEY `book_id` (`book_id`),
  KEY `report_user` (`report_user`),
  CONSTRAINT `badbook_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
  CONSTRAINT `badbook_ibfk_2` FOREIGN KEY (`report_user`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of badbook
-- ----------------------------

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `book_id` int(11) NOT NULL AUTO_INCREMENT,
  `isbn` varchar(255) DEFAULT NULL,
  `describes` varchar(1023) DEFAULT NULL,
  `author` varchar(55) DEFAULT NULL,
  `title` varchar(500) DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `binding` varchar(55) DEFAULT NULL,
  `price` varchar(55) DEFAULT NULL,
  `rating` varchar(55) DEFAULT NULL,
  `page` varchar(255) DEFAULT NULL,
  `store_id` int(255) NOT NULL,
  `store_time` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `reverseuser` int(11) DEFAULT NULL,
  PRIMARY KEY (`book_id`),
  KEY `FK_STORE` (`store_id`),
  KEY `reverseuser` (`reverseuser`),
  CONSTRAINT `FK_STORE` FOREIGN KEY (`store_id`) REFERENCES `bookstore` (`store_id`),
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`reverseuser`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES ('94', '9787543064089', '人间失格，即丧失为人的资格。由序、第一手札、第二手札、第三手札、后记共五个部分构成，其中序和后记以作者口吻叙说，三个手札则以主人公叶藏的口吻叙述。主人公叶藏胆小懦弱，惧怕世间的情感，不了解人类复杂的思', '[日] 太宰治', '人间失格', '武汉出版社', 'https://book.douban.com/subject/6973970/', 'https://img3.doubanio.com/lpic/s7003165.jpg', '平装', '25.00元', '8.4', '240', '2', '2017-06-12', '超好看', null);
INSERT INTO `book` VALUES ('96', '9787508639192', '婚姻会不会杀人？又是谁谋杀了婚姻？敷衍经营的婚姻值得存在吗？懒散消极的爱人要不要得到教训？把《别相信任何人》、《格雷的五十道阴影》甩出好几条街，密集上演令人侧目的畅销奇迹，从纽约律师，到绝望主妇，人手', '[美]吉莉安·弗琳', '消失的爱人', '中信出版社', 'https://book.douban.com/subject/24541883/', 'https://img3.doubanio.com/lpic/s27986875.jpg', '平装', '42.00元', '8.1', '480', '2', '2017-06-12', '恐怖悬疑看晚怀疑人生的一部经典', null);
INSERT INTO `book` VALUES ('97', '9787550263932', '这本书，是一群误入歧途的天才的故事，也是一群入院治疗的疯子的故事。\n\n这本书，是作者高铭耗时4年深入医院精神科、公安部等神秘机构，和数百名“非常态人类”直接接触后，以访谈形式记录了生活在社会另一个角落', '高铭', '天才在左 疯子在右（完整版）', '北京联合出版公司', 'https://book.douban.com/subject/26666472/', 'https://img3.doubanio.com/lpic/s28350186.jpg', '平装', '39.80', '7.4', '350', '2', '2017-06-12', '当你与世界格格不入 没准是世界错了', null);
INSERT INTO `book` VALUES ('98', '9787540474058', '黑色幽默仁波切、一米八三大诗人自扯自蛋 首部奇思妙想故事集，为聪明人提供幸福感。\n收入网络点击率超过100，000，000次的《扯 经》，附赠 “禅意”卡片4张。\n内容分为三部分：第一部分是作者在网络', '李诞', '笑场', '湖南文艺出版社', 'https://book.douban.com/subject/26696624/', 'https://img3.doubanio.com/lpic/s28386586.jpg', '平装', '36.00元', '7.5', '320', '2', '2017-06-12', '未曾开言 我先笑场。笑场后请听我诉一诉衷肠', null);
INSERT INTO `book` VALUES ('100', '9787514205039', '《自控力》内容简介：作为一名健康心理学家，凯利•麦格尼格尔博士的工作就是帮助人们管理压力，并在生活中做出积极的改变。多年来，通过观察学生们是如何控制选择的，她意识到，人们关于自控的很多看法实际上妨碍了', '[美]  凯利·麦格尼格尔', '自控力', '文化发展出版社(原印刷工业出版社)', 'https://book.douban.com/subject/10786473/', 'https://img3.doubanio.com/lpic/s10685385.jpg', '平装', '39.80元', '8.2', '263', '2', '2017-06-12', '我的自控力就是买回来两年从未打开这本书', null);
INSERT INTO `book` VALUES ('102', '9787201077642', '《小王子》是一本足以让人永葆童心的不朽经典，被全球亿万读者誉为非常值得收藏的书。\n遥远星球上的小王子，与美丽而骄傲的玫瑰吵架负气出走，在各星球漫游中，小王子遇到了傲慢的国王、酒鬼、惟利是图的商人，死守', '[法] 安托万•德•圣埃克苏佩', '小王子', '天津人民出版社', 'https://book.douban.com/subject/20443559/', 'https://img3.doubanio.com/lpic/s28850934.jpg', '精装', '32.00', '7.5', '152', '2', '2017-06-12', '重要的东西用眼睛是看不见的', null);
INSERT INTO `book` VALUES ('103', '9787115313256', '《evernote超效率数字笔记术》主要介绍了云笔记软件evernote在高效率地工作和生活方面的先进理念和绝佳技巧，深入浅出地阐述了收集资料和整理笔记的相关经验和深刻体悟，内容详实丰富，结构精严缜密', '电脑玩物站', 'Evernote超效率数字笔记术', '人民邮电出版社', 'https://book.douban.com/subject/24524405/', 'https://img3.doubanio.com/lpic/s26390032.jpg', '平装', '35.00元', '6.4', '224', '2', '2017-06-12', '嘻嘻。我是卖一块钱', null);
INSERT INTO `book` VALUES ('104', '9787550277014', '★ 比尔•盖茨2015年夏季推荐图书！2015年中国、2014年全美畅销趣味科普书，入选2014年度全美优选图书，上市后横扫《纽约时报》《出版人周刊》《华尔街时报》等各大图书榜，迄今稳居各大排行榜前列', '兰道尔·门罗 (Randall Munroe', '那些古怪又让人忧心的问题', '北京联合出版公司', 'https://book.douban.com/subject/26826089/', 'https://img3.doubanio.com/lpic/s28939876.jpg', '精装', 'CNY 68.00', '8.4', '304', '3', '2017-06-12', 'what if？', null);
INSERT INTO `book` VALUES ('105', '9787540471194', '★国际演说家、生命教练和咨商师克里斯多福•孟经典作品。\n★华语世界深具影响力身心灵作家张德芬翻译并全力推荐。\n★本书让你掌握所有人际关系包括温暖浪漫的亲密关系的本质。\n★持续十六年畅销经典、知见领袖最', '[加]克里斯多福•', '亲密关系：通往灵魂的桥梁', '湖南文艺出版社', 'https://book.douban.com/subject/26363229/', 'https://img3.doubanio.com/lpic/s28045305.jpg', '平装', '35.00', '7.9', '255', '3', '2017-06-12', '孟！！', null);
INSERT INTO `book` VALUES ('107', '9787560029740', '《大家的日语》讲述教材沿用《新日语基础教程1、2》的教学方式，清晰地提出学习项目和学习方法，精心地设计会话场面和出场人物，并通过大量的、反复的练习培养学习者的语言应用能力。而本套教材与其姊妹篇的最大区', '株式会社 ', '大家的日语1', '外语教学与研究出版社', 'https://book.douban.com/subject/1234197/', 'https://img3.doubanio.com/lpic/s4580016.jpg', '平装', '38.00', '8.3', '244', '1', '2017-06-12', '日语哦', '18');
INSERT INTO `book` VALUES ('110', '9787111392842', '本书全面涵盖了并行软件和硬件的方方面面，深入浅出地介绍如何使用mpi（分布式内存编程）、pthreads和openmp（共享内存编程）编写高效的并行程序。各章节包含了难易程度不同的编程习题。\n本书可以', 'Peter Pachec', '并行程序设计导论', '机械工业出版社华章公司', 'https://book.douban.com/subject/20374756/', 'https://img3.doubanio.com/lpic/s24230466.jpg', '平装', '49.00元', '7.6', '252', '1', '2017-06-13', '', null);
INSERT INTO `book` VALUES ('111', '9787111516682', '国内资深Web开发专家根据Spring MVC全新技术撰写，基于实际生产环境，从基础知识、源代码和实战3个维度对Spring MVC的结构和实现进行详细讲解\n全面介绍Spring MVC的架构、原理、', '韩路', '看透Spring MVC', '机械工业出版社', 'https://book.douban.com/subject/26696099/', 'https://img1.doubanio.com/lpic/s28824099.jpg', '平装', 'CNY 69.00', '7.6', '309', '4', '2017-06-13', '', null);
INSERT INTO `book` VALUES ('114', '9787560029740', '《大家的日语》讲述教材沿用《新日语基础教程1、2》的教学方式，清晰地提出学习项目和学习方法，精心地设计会话场面和出场人物，并通过大量的、反复的练习培养学习者的语言应用能力。而本套教材与其姊妹篇的最大区', '株式会社 ', '大家的日语1', '外语教学与研究出版社', 'https://book.douban.com/subject/1234197/', 'https://img3.doubanio.com/lpic/s4580016.jpg', '平装', '38.00', '8.3', '244', '4', '2017-06-13', '是你的日语书？ 不，是大家的日语书', null);
INSERT INTO `book` VALUES ('115', '9787040431995', '《马克思主义理论研究和建设工程重点教材:中国近现代史纲要(2015年修订版)》按照时间顺序共分为从鸦片战争到五四运动前夜、从五四运动到新中国成立、从新中国成立到社会主义现代化建设新时期三篇，具体内容包', '本书编写', '中国近现代史纲要（2015年修订版）', '高等教育出版社', 'https://book.douban.com/subject/26609627/', 'https://img1.doubanio.com/lpic/s29009187.jpg', '平装', '26.00元', '5.5', '352', '4', '2017-06-13', '读懂近纲 走遍天下都不怕', null);
INSERT INTO `book` VALUES ('117', '9787560031460', '《大家的日语2:学习辅导用书》正如书名《大家的日语》所说的那样，是为了使初学日语的人能愉快地学习，教师也能兴致勃勃地教下去，花了三年多的时间编写而成的。《大家的日语2:学习辅导用书》是作为《新日语基础', '日本3A株式会', '大家的日语(2)学习辅导用书', '外语教学与研究出版社', 'https://book.douban.com/subject/1111307/', 'https://img3.doubanio.com/lpic/s6016650.jpg', '简裝本', '18.90元', '8.3', '225', '1', '2017-06-13', '彩蛋', '19');

-- ----------------------------
-- Table structure for bookstore
-- ----------------------------
DROP TABLE IF EXISTS `bookstore`;
CREATE TABLE `bookstore` (
  `store_id` int(11) NOT NULL,
  `store_name` varchar(20) DEFAULT NULL,
  `store_describe` varchar(255) DEFAULT NULL,
  `addr_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`store_id`),
  KEY `addr_id` (`addr_id`),
  CONSTRAINT `bookstore_ibfk_2` FOREIGN KEY (`addr_id`) REFERENCES `address` (`addr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bookstore
-- ----------------------------
INSERT INTO `bookstore` VALUES ('0', '0', '0', '0');
INSERT INTO `bookstore` VALUES ('1', '雨晨的书架', '华丽的书架', '1');
INSERT INTO `bookstore` VALUES ('2', '汤汤的书架', '小仙女的书架', '2');
INSERT INTO `bookstore` VALUES ('3', '生化的书架', '666大西洋的书架', '3');
INSERT INTO `bookstore` VALUES ('4', '小可爱的书架', '', '4');

-- ----------------------------
-- Table structure for history
-- ----------------------------
DROP TABLE IF EXISTS `history`;
CREATE TABLE `history` (
  `history_id` int(11) NOT NULL AUTO_INCREMENT,
  `book_name` varchar(255) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `oper_time` varchar(55) NOT NULL,
  `oper_type` varchar(55) DEFAULT NULL,
  `isbn` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of history
-- ----------------------------
INSERT INTO `history` VALUES ('145', '并行程序设计导论', '16', '2017-06-12', '上架', '9787111392842', '我喜欢你');
INSERT INTO `history` VALUES ('146', '看透Spring MVC', '16', '2017-06-12', '上架', '9787111516682', '我喜欢你很久了');
INSERT INTO `history` VALUES ('147', '数据库系统概念', '16', '2017-06-12', '上架', '9787111375296', '我不要面子的？');
INSERT INTO `history` VALUES ('148', '大家的日语(1) 学习辅导用书', '16', '2017-06-12', '上架', '9787560031453', '日语很好玩的');
INSERT INTO `history` VALUES ('149', '人间失格', '17', '2017-06-12', '上架', '9787543064089', '超好看');
INSERT INTO `history` VALUES ('150', '大家的日语2', '18', '2017-06-12', '上架', '9787560031477', '啦啦啦啦啦~~~');
INSERT INTO `history` VALUES ('151', '消失的爱人', '17', '2017-06-12', '上架', '9787508639192', '恐怖悬疑看晚怀疑人生的一部经典');
INSERT INTO `history` VALUES ('152', '天才在左 疯子在右（完整版）', '17', '2017-06-12', '上架', '9787550263932', '当你与世界格格不入 没准是世界错了');
INSERT INTO `history` VALUES ('153', '笑场', '17', '2017-06-12', '上架', '9787540474058', '未曾开言 我先笑场。笑场后请听我诉一诉衷肠');
INSERT INTO `history` VALUES ('154', '龙族Ⅲ', '18', '2017-06-12', '上架', '9787549216468', '');
INSERT INTO `history` VALUES ('155', '自控力', '17', '2017-06-12', '上架', '9787514205039', '我的自控力就是买回来两年从未打开这本书');
INSERT INTO `history` VALUES ('156', '数据库系统概念', '18', '2017-06-12', '上架', '9787040311754', '');
INSERT INTO `history` VALUES ('157', '小王子', '17', '2017-06-12', '上架', '9787201077642', '重要的东西用眼睛是看不见的');
INSERT INTO `history` VALUES ('158', 'Evernote超效率数字笔记术', '17', '2017-06-12', '上架', '9787115313256', '嘻嘻。我是卖一块钱');
INSERT INTO `history` VALUES ('159', '大家的日语2', '18', '2017-06-12 23:02:36', '借入', '9787560031477', '啦啦啦啦啦~~~');
INSERT INTO `history` VALUES ('160', '大家的日语2', '18', '2017-06-12 23:02:36', '借出', '9787560031477', '啦啦啦啦啦~~~');
INSERT INTO `history` VALUES ('161', '那些古怪又让人忧心的问题', '18', '2017-06-12', '上架', '9787550277014', 'what if？');
INSERT INTO `history` VALUES ('162', '亲密关系：通往灵魂的桥梁', '18', '2017-06-12', '上架', '9787540471194', '孟！！');
INSERT INTO `history` VALUES ('163', '看透Spring MVC', '16', '2017-06-12', '上架', '9787111516682', '这是一个很好的框架哦');
INSERT INTO `history` VALUES ('164', '大家的日语1', '16', '2017-06-12', '上架', '9787560029740', '日语哦');
INSERT INTO `history` VALUES ('165', '大家的日语1', '19', '2017-06-12', '上架', '9787560029740', '');
INSERT INTO `history` VALUES ('166', '大家的日语1', '19', '2017-06-12', '上架', '9787560029740', '');
INSERT INTO `history` VALUES ('167', '并行程序设计导论', '16', '2017-06-13 00:04:03', '借入', '9787111392842', '我喜欢你');
INSERT INTO `history` VALUES ('168', '并行程序设计导论', '16', '2017-06-13 00:04:03', '借出', '9787111392842', '我喜欢你');
INSERT INTO `history` VALUES ('169', '并行程序设计导论', '16', '2017-06-13', '上架', '9787111392842', '');
INSERT INTO `history` VALUES ('170', '看透Spring MVC', '19', '2017-06-13', '上架', '9787111516682', '');
INSERT INTO `history` VALUES ('171', '大家的日语(1) 学习辅导用书', '19', '2017-06-13', '上架', '9787560031453', '');
INSERT INTO `history` VALUES ('172', '大家的日语1', '19', '2017-06-13', '上架', '9787560029740', '');
INSERT INTO `history` VALUES ('173', '大家的日语1', '16', '2017-06-13 12:15:26', '借入', '9787560029740', '');
INSERT INTO `history` VALUES ('174', '大家的日语1', '19', '2017-06-13 12:15:26', '借出', '9787560029740', '');
INSERT INTO `history` VALUES ('175', '大家的日语(1) 学习辅导用书', '16', '2017-06-13 12:18:35', '借入', '9787560031453', '');
INSERT INTO `history` VALUES ('176', '大家的日语(1) 学习辅导用书', '19', '2017-06-13 12:18:35', '借出', '9787560031453', '');
INSERT INTO `history` VALUES ('177', '大家的日语1', '19', '2017-06-13', '上架', '9787560029740', '是你的日语书？ 不，是大家的日语书');
INSERT INTO `history` VALUES ('178', '中国近现代史纲要（2015年修订版）', '19', '2017-06-13', '上架', '9787040431995', '读懂近纲 走遍天下都不怕');
INSERT INTO `history` VALUES ('179', '大家的日语(2)学习辅导用书', '19', '2017-06-13', '上架', '9787560031460', '大家的日语');
INSERT INTO `history` VALUES ('180', '大家的日语(2)学习辅导用书', '16', '2017-06-13', '上架', '9787560031460', '彩蛋');
INSERT INTO `history` VALUES ('181', '大家的日语(2)学习辅导用书', '16', '2017-06-13 14:48:12', '借入', '9787560031460', '大家的日语');
INSERT INTO `history` VALUES ('182', '大家的日语(2)学习辅导用书', '19', '2017-06-13 14:48:12', '借出', '9787560031460', '大家的日语');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(20) DEFAULT '一个人',
  `phone` varchar(11) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `gender` varchar(6) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `headPic` varchar(255) DEFAULT NULL,
  `store_id` int(11) DEFAULT NULL,
  `devicetoken` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FK_USER_STORE` (`store_id`),
  CONSTRAINT `FK_USER_STORE` FOREIGN KEY (`store_id`) REFERENCES `bookstore` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('16', '雨晨', '13162303868', '123', '女', '8', 'http://115.159.35.11:8080/bookstore/upload/2017-06-13/12871b5c-f4fe-43f4-ae22-45d8e874d256.jpg', '1', 'AgZc4LRzaJspr10_uURqZzumUqxTRW0cD0XDZPqVP6vm');
INSERT INTO `user` VALUES ('17', '汤汤', '13321577499', '123', '女', '7', null, '2', 'Ar4N1wHoNcoEmfWhsofACOGsSXe6YfChxvRuY_v8bLLY');
INSERT INTO `user` VALUES ('18', '生化', '13916062441', '123', null, '6', 'http://115.159.35.11:8080/bookstore/upload/2017-06-13/a56634cc-4834-40d3-9e62-eea15148a384.jpg', '3', null);
INSERT INTO `user` VALUES ('19', '小可爱', '13936366666', '123', '女', '10', 'http://115.159.35.11:8080/bookstore/upload/2017-06-13/2f2b2bb2-730b-43e1-bef5-559ed397c016.jpg', '4', 'Alprc2IncYvCJ63xww7rNnBhDaYJFwHkuKqitieaLUSH');
