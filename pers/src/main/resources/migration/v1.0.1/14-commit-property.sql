--liquibase formatted sql
--changeset kaiso:init-commit_property author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
CREATE TABLE LGM_COMMIT_PROPERTY (
		id VARCHAR(255) primary key,
		appcode VARCHAR(255),
		envcode VARCHAR(255),
		name VARCHAR(255),
		pvalue VARCHAR(255),
		commit_id VARCHAR(255),
		CONSTRAINT lgm_commit_property_fk_commit FOREIGN KEY(commit_id)
        REFERENCES LGM_COMMIT(id) ON DELETE CASCADE
	);
