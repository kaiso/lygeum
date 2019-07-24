--liquibase formatted sql
--changeset kaiso:init-application author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table aps_permission (
    id BIGINT primary key,
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    name varchar(255),
    description varchar(500)
);
