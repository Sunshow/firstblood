CREATE TABLE `bjgl_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(16) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `log_type` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  `url` text NULL,
  `action_name` varchar(255) DEFAULT NULL,
  `params` text NULL,
  `remote_ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_log_create` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bjgl_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(16) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(16) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `login_time` datetime DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `valid` smallint(6) NOT NULL DEFAULT '1',
  `memo` varchar(255) DEFAULT NULL,
  `tel` varchar(16) DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_valild` (`valid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bjgl_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) NOT NULL,
  `valid` smallint(6) NOT NULL DEFAULT '1',
  `memo` varchar(255) DEFAULT NULL,
  `restriction` tinyint(1) NOT NULL DEFAULT '0',
  `restriction_ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_valid` (`valid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bjgl_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bjgl_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) NOT NULL,
  `url` varchar(512) DEFAULT NULL,
  `order_view` int(11) NOT NULL DEFAULT '0',
  `valid` smallint(6) NOT NULL DEFAULT '1',
  `memo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_view`),
  KEY `idx_valid` (`valid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bjgl_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) NOT NULL,
  `url` varchar(512) DEFAULT NULL,
  `menu_id` bigint(20) NOT NULL,
  `order_view` int(11) NOT NULL DEFAULT '0',
  `action_name` varchar(255) NOT NULL,
  `param_name` varchar(255) DEFAULT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `valid` smallint(6) NOT NULL DEFAULT '1',
  `memo` varchar(255) DEFAULT NULL,
  `menu_item` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_menu` (`menu_id`),
  KEY `idx_order` (`order_view`),
  KEY `idx_valid` (`valid`),
  KEY `idx_menu_item` (`menu_item`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bjgl_permission_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  `order_view` int(11) NOT NULL DEFAULT '0',
  `method_name` varchar(255) NOT NULL,
  `valid` smallint(6) NOT NULL DEFAULT '1',
  `memo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_permission` (`permission_id`),
  KEY `idx_order` (`order_view`),
  KEY `idx_valid` (`valid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bjgl_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  `permission_item_ids` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;