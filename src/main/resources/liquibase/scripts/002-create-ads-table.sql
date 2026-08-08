--liquibase formatted sql

--changeset resale-platform:create-ads-table
CREATE TABLE ads (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    price       INT NOT NULL,
    description TEXT,
    image       VARCHAR(255),
    author_id   INT NOT NULL,
    CONSTRAINT fk_ads_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE INDEX idx_ads_author ON ads (author_id);

--rollback DROP TABLE ads;
