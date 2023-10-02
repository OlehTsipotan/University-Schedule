CREATE TABLE disciplines(
    discipline_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

ALTER TABLE groups
ADD COLUMN discipline_id INT,
ADD CONSTRAINT fk_groups_disciplines
FOREIGN KEY (discipline_id)
REFERENCES disciplines(discipline_id);

CREATE SEQUENCE disciplines_seq START 1;