SET FOREIGN_KEY_CHECKS = 0;

SET @querystring =
	(SELECT CONCAT(
		'DROP TABLES IF EXISTS ', 
		(SELECT IFNULL(GROUP_CONCAT(table_name), 'default_name') FROM information_schema.tables WHERE table_schema = 'logger_schema'), 
        ';'));
        
PREPARE stmt FROM @queryString;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
SET @querystring = null;

SET FOREIGN_KEY_CHECKS = 1;