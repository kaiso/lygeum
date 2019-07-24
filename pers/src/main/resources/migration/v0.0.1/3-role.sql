--liquibase formatted sql
--changeset kaiso:init-role author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table aps_role (
    id BIGINT primary key,
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    name varchar(255),
    description varchar(500)
);

insert into aps_role values (2,'ADMIN',SYSDATE,'ADMIN',SYSDATE,'ADMIN', 'the lygeum super user role');
commit;