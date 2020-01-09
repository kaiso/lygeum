--liquibase formatted sql
--changeset kaiso:init-user_role-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table lgm_user_role (
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    user_id BIGINT,
    role_id BIGINT,
    CONSTRAINT lgm_user_role_pk PRIMARY KEY(user_id,role_id),
    CONSTRAINT lgm_user_role_fk_user FOREIGN KEY(user_id)
        REFERENCES lgm_user(id),
    CONSTRAINT lgm_user_role_fk_role FOREIGN KEY(role_id)
        REFERENCES lgm_role(id) ON DELETE CASCADE
);

insert into lgm_user_role values ('SETUP', SYSDATE, 'SETUP', SYSDATE, 1, 1);
commit;