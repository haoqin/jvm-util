CREATE SCHEMA IF NOT EXISTS PUBLIC;

CREATE TABLE IF NOT EXISTS PUBLIC.orca_user
(
  id VARCHAR(36) NOT NULL,
  uid VARCHAR(36) NOT NULL,
  username VARCHAR(36) NOT NULL,
  profile_image VARCHAR DEFAULT '' NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  date_of_birth TIMESTAMP NOT NULL,
  email_address VARCHAR(100) NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  citizenship VARCHAR(2) DEFAULT 'US' NOT NULL,
  person_id VARCHAR,
  street_line_1 VARCHAR(100),
  street_line_2 VARCHAR(100),
  city VARCHAR,
  state VARCHAR(2),
  postal_code VARCHAR(5),
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.pod
(
  id VARCHAR(36) NOT NULL,
  pod_username VARCHAR(36) NOT NULL,
  pod_name VARCHAR(36) DEFAULT 'New Orca Pod',
  pod_description VARCHAR(256) DEFAULT '',
  emoji VARCHAR DEFAULT '🐬',
  is_public BOOLEAN DEFAULT FALSE,
  is_dm BOOLEAN DEFAULT TRUE,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.user_pod_lookup
(
  id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  pod_id VARCHAR(36) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  permission INT DEFAULT 0,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.expense
(
  id VARCHAR(36) NOT NULL,
  sender_user_id VARCHAR(36) NOT NULL,
  pod_user_id VARCHAR(36) NOT NULL,
  message VARCHAR(256) DEFAULT '',
  amount FLOAT NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.orca_transaction
(
  id VARCHAR(36) NOT NULL,
  pod_user_id VARCHAR(36) NOT NULL,
  expense_id VARCHAR(36) NOT NULL,
  sender_user_id VARCHAR(36) NOT NULL,
  receiver_user_id VARCHAR(36) NOT NULL,
  message VARCHAR(256) DEFAULT '',
  amount FLOAT NOT NULL,
  booking_id VARCHAR,
  is_pending BOOLEAN DEFAULT TRUE,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.user_balance
(
  id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  transaction_id VARCHAR(36) NOT NULL,
  balance FLOAT NOT NULL,
  updated_at TIMESTAMP NOT NULL
);


CREATE TABLE IF NOT EXISTS PUBLIC.notification
(
  id VARCHAR(36) NOT NULL,
  sender_user_id VARCHAR(36) NOT NULL,
  receiver_user_id VARCHAR(36) NOT NULL,
  message VARCHAR(256) DEFAULT '',
  nav_params VARCHAR(1024) DEFAULT '',
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.notification_token
(
  id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  device_id VARCHAR(36),
  token VARCHAR(50) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.friend
(
  id VARCHAR(36) NOT NULL,
  requester_user_id VARCHAR(36) NOT NULL,
  requested_user_id VARCHAR(36) NOT NULL,
  is_blocked BOOLEAN DEFAULT FALSE,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.user_account_lookup
(
  id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  account_id VARCHAR NOT NULL,
  updated_at TIMESTAMP NOT NULL
);