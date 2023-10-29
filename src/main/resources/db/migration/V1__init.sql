CREATE TABLE IF NOT EXISTS todo (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    time_created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    time_updated TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS task (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    todo_id BIGINT,
    completed boolean NOT NULL,
    due_date TIMESTAMP,
    time_created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    time_updated TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (todo_id) REFERENCES todo (id)
);
