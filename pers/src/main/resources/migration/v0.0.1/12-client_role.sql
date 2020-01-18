--liquibase formatted sql
--changeset kaiso:init-client_role-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table lgm_client_role (
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    client_id BIGINT,
    role_id BIGINT,
    CONSTRAINT lgm_client_role_pk PRIMARY KEY(client_id,role_id),
    CONSTRAINT lgm_client_role_fk_user FOREIGN KEY(client_id)
        REFERENCES lgm_client(id) ON DELETE CASCADE,
    CONSTRAINT lgm_client_role_fk_role FOREIGN KEY(role_id)
        REFERENCES lgm_role(id) ON DELETE CASCADE
);
