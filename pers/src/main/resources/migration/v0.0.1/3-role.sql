--liquibase formatted sql
--changeset kaiso:init-role author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table lgm_role (
    id BIGINT primary key,
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    code varchar(255),
    name varchar(255),
    description varchar(500)
);

insert into lgm_role values (1,'SETUP',CURRENT_TIMESTAMP,'SETUP',CURRENT_TIMESTAMP,'LYGEUM_ADMIN','Administrator', 'the lygeum super user role');
insert into lgm_role values (2,'SETUP',CURRENT_TIMESTAMP,'SETUP',CURRENT_TIMESTAMP,'LYGEUM_OPERATOR','Operator', 'the lygeum operator role');
insert into lgm_role values (3,'SETUP',CURRENT_TIMESTAMP,'SETUP',CURRENT_TIMESTAMP,'ALL_ENV_CREATE','Create environment', 'Create environment');
insert into lgm_role values (4,'SETUP',CURRENT_TIMESTAMP,'SETUP',CURRENT_TIMESTAMP,'ALL_ENV_DELETE','Delete any environment', 'Delete any environment');
insert into lgm_role values (5,'SETUP',CURRENT_TIMESTAMP,'SETUP',CURRENT_TIMESTAMP,'ALL_APP_CREATE','Create application', 'Create application');
insert into lgm_role values (6,'SETUP',CURRENT_TIMESTAMP,'SETUP',CURRENT_TIMESTAMP,'ALL_APP_DELETE','Delete any application', 'Delete any application');
commit;