CREATE TABLE authorities
(
    authority_id INT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL UNIQUE
);

CREATE SEQUENCE authorities_seq START 1;

CREATE TABLE roles_authorities
(
    role_id      INT NOT NULL REFERENCES roles (role_id) ON DELETE CASCADE,
    authority_id INT NOT NULL REFERENCES authorities (authority_id) ON DELETE CASCADE
);