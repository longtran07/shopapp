Create database shopapp;
use shopapp;
Create table users(
    id INT PRIMARY KEY AUTO_INCREMENT ,
    fullname VARCHAR(100) DEFAULT'',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) DEFAULT'',
    password VARCHAR(100) NOT NULL DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT DEFAULT 1,
    date_of_birth DATE,
    facebook_account_id INT DEFAULT 0, 
    google_account_id INT DEFAULT 0
     
);

Create table tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME,
    revoked TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);