CREATE TABLE sample(
	id INTEGER NOT NULL,
	label CHAR(25),
	PRIMARY KEY (ID)
)

CREATE TABLE greeting (
	id IDENTITY, 
	message VARCHAR(100) NOT NULL, 
	createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO greeting(message) VALUES 
	('hello'),
	('hi'),
	('Buenos dias');