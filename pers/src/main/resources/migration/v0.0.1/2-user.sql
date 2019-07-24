--liquibase formatted sql
--changeset kaiso:init-user-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table aps_user (
    id BIGINT primary key,
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    email varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    password varchar(500),
);