--liquibase formatted sql

--changeset resale-platform:create-comments-table
CREATE TABLE comments (
    id         SERIAL PRIMARY KEY,
    text       VARCHAR(255) NOT NULL,
    created_at BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000,
    author_id  INT NOT NULL,
    ad_id      INT NOT NULL,
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comments_ad FOREIGN KEY (ad_id) REFERENCES ads (id) ON DELETE CASCADE
);

CREATE INDEX idx_comments_author ON comments (author_id);
CREATE INDEX idx_comments_ad ON comments (ad_id);

--rollback DROP TABLE comments;
