-- CREATE SEQUENCE task_seq START 1;

CREATE TABLE IF NOT EXISTS todo (
    id bigserial NOT NULL PRIMARY KEY,
    name varchar(100) NOT NULL,
    description varchar(500),
    time_created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    time_updated TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS task (
    id bigserial NOT NULL PRIMARY KEY,
    name varchar(100) NOT NULL,
    description varchar(255),
    todo_id bigint,
    time_created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    time_updated TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (todo_id) REFERENCES todo (id)
);
