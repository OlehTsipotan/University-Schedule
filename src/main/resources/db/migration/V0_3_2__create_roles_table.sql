CREATE TABLE roles
(
    role_id INT PRIMARY KEY,
    name    VARCHAR(255) NOT NULL UNIQUE
);

CREATE SEQUENCE roles_seq START 1;

ALTER TABLE users
    ADD COLUMN role_id INT,
    ADD CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id)
            REFERENCES roles (role_id);