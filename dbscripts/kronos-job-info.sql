DROP TABLE IF EXISTS `kronos_job_info`;
CREATE TABLE `kronos_job_info` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `cron_expression` varchar(255) DEFAULT NULL,
                                   `cron_job` bit(1) DEFAULT NULL,
                                   `job_class` varchar(255) DEFAULT NULL,
                                   `job_group` varchar(255) DEFAULT NULL,
                                   `job_name` varchar(255) DEFAULT NULL,
                                   `repeat_time` bigint(20) DEFAULT NULL,
                                   `job_data` blob NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=INNODB;