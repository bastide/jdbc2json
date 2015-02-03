CREATE TABLE greeting (id IDENTITY, message VARCHAR(100) NOT NULL, createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

INSERT INTO greeting(message) values 
    ('hello'),
    ('Iñtërnâtiônàlizætiøn'),
    ('שלום ירושלים');