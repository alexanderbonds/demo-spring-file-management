CREATE TABLE files (
    UUID        UUID PRIMARY KEY,
    filename    VARCHAR(255) NOT NULL,
    mimetype    VARCHAR(255) NOT NULL,
    size        BIGINT
);
