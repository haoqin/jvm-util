CREATE SCHEMA IF NOT EXISTS PUBLIC;
CREATE TABLE IF NOT EXISTS PUBLIC.pod_autokey
(
  id INT AUTO_INCREMENT NOT NULL,
  pod_username VARCHAR(36) NOT NULL,
  pod_name VARCHAR(36) DEFAULT 'New Orca Pod',
  pod_description VARCHAR(256) DEFAULT '',
  emoji VARCHAR DEFAULT '🐬',
  is_public BOOLEAN DEFAULT FALSE,
  is_dm BOOLEAN DEFAULT TRUE,
  updated_at TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);