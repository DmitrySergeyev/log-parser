CREATE TABLE logger_schema.country (
	id INT NOT NULL AUTO_INCREMENT, 
	name VARCHAR(100) NOT NULL UNIQUE, 
	PRIMARY KEY (id));
	
CREATE TABLE logger_schema.domain_query (
	id INT NOT NULL AUTO_INCREMENT, 
	date TIMESTAMP NOT NULL, 
	ip VARCHAR(16) NOT NULL, 
	country_id INT, 
	PRIMARY KEY (id), 
	CONSTRAINT fk_domain_query_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE RESTRICT ON UPDATE CASCADE);
	
CREATE TABLE logger_schema.category (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL UNIQUE,
	PRIMARY KEY (id));
	
CREATE TABLE logger_schema.good (
	id INT NOT NULL AUTO_INCREMENT,
	store_id INT UNIQUE, 
	name VARCHAR(100) NOT NULL,
	category_id INT NOT NULL, 
	PRIMARY KEY (id),
	CONSTRAINT fk_good_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE RESTRICT ON UPDATE CASCADE);
	
CREATE TABLE logger_schema.category_query (
	id INT NOT NULL AUTO_INCREMENT,
	category_id INT NOT NULL,
	date TIMESTAMP NOT NULL, 
	ip VARCHAR(16) NOT NULL, 
	country_id INT, 
	PRIMARY KEY (id),
	CONSTRAINT fk_category_query_category_id FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT fk_category_query_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE RESTRICT ON UPDATE CASCADE);
	
CREATE TABLE logger_schema.good_query (
	id INT NOT NULL AUTO_INCREMENT,
	good_id INT NOT NULL,
	date TIMESTAMP NOT NULL, 
	ip VARCHAR(16) NOT NULL, 
	country_id INT, 
	PRIMARY KEY (id),
	CONSTRAINT fk_good_query_good_id FOREIGN KEY (good_id) REFERENCES good(id) ON DELETE RESTRICT ON UPDATE CASCADE, 
	CONSTRAINT fk_good_query_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE RESTRICT ON UPDATE CASCADE);
	
CREATE TABLE logger_schema.cart_item(
	id INT NOT NULL AUTO_INCREMENT,
	amount INT NOT NULL, 
	good_id INT NOT NULL, 
	cart_id INT NOT NULL, 
	date TIMESTAMP NOT NULL, 
	ip VARCHAR(16) NOT NULL, 
	country_id INT, 
	PRIMARY KEY (id), 
	CONSTRAINT fk_cart_item_good_id FOREIGN KEY (good_id) REFERENCES good(id) ON DELETE RESTRICT ON UPDATE CASCADE, 
	CONSTRAINT fk_cart_item_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE RESTRICT ON UPDATE CASCADE);
	
CREATE TABLE logger_schema.payment_query (
	id INT NOT NULL AUTO_INCREMENT, 
	user_id VARCHAR(20),
	cart_id INT NOT NULL, 
	date TIMESTAMP NOT NULL, 
	ip VARCHAR(16) NOT NULL, 
	country_id INT, 
	PRIMARY KEY (id), 
	CONSTRAINT fk_payment_query_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE RESTRICT ON UPDATE CASCADE);
	
CREATE TABLE logger_schema.success_payment_query (
	id INT NOT NULL AUTO_INCREMENT,
	cart_id INT NOT NULL,
	date TIMESTAMP NOT NULL, 
	ip VARCHAR(16) NOT NULL, 
	country_id INT, 
	PRIMARY KEY (id),
	CONSTRAINT fk_success_payment_query_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE RESTRICT ON UPDATE CASCADE);
	