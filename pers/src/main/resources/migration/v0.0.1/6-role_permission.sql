--liquibase formatted sql
--changeset kaiso:init-role_permission-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table aps_role_permission (
    id BIGINT primary key,
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    role_id BIGINT,
    permission_id BIGINT,
    CONSTRAINT aps_role_permission_fk_role FOREIGN KEY(role_id)
        REFERENCES aps_role(id),
    CONSTRAINT aps_role_permission_fk_permission FOREIGN KEY(permission_id)
        REFERENCES aps_permission(id)
);