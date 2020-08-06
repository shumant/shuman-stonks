DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id UUID  PRIMARY KEY,
  oauth_id VARCHAR(64)  NOT NULL,
  name VARCHAR(64) NOT NULL,
  email VARCHAR(64) NOT NULL,
  thumbnail_url VARCHAR(640) NOT NULL
);

DROP TABLE IF EXISTS auth;

CREATE TABLE auth (
  id UUID  PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  scopes VARCHAR(128) NOT NULL,
  access_token VARCHAR(128),
  refresh_Token VARCHAR(128)
);

