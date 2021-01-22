--liquibase formatted sql
--changeset kaiso:init-commit author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
CREATE TABLE LGM_COMMIT (
		id VARCHAR(255) primary key,
		author VARCHAR(255),
		message VARCHAR(1000),
		created_at TIMESTAMP
	);