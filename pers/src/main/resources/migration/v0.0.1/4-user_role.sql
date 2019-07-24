--liquibase formatted sql
--changeset kaiso:init-user_role-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table aps_user_role (
    id BIGINT primary key,
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    user_id BIGINT,
    role_id BIGINT,
    CONSTRAINT aps_user_role_fk_user FOREIGN KEY(user_id)
        REFERENCES aps_user(id),
    CONSTRAINT aps_user_role_fk_role FOREIGN KEY(role_id)
        REFERENCES aps_role(id)
);