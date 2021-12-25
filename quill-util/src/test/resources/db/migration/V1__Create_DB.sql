CREATE TABLE IF NOT EXISTS user
(
  username VARCHAR(30) NOT NULL,
  uid VARCHAR(30) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  profile_image VARCHAR
);

CREATE TABLE IF NOT EXISTS transaction
(
  transaction_id INT NOT NULL AUTO_INCREMENT,
  sender_username VARCHAR(30) NOT NULL,
  receiver_username VARCHAR(30) NOT NULL,
  expense_id INT NOT NULL,
  message VARCHAR(256) NOT NULL,
  amount FLOAT NOT NULL,
  is_pending BOOLEAN NOT NULL
);
