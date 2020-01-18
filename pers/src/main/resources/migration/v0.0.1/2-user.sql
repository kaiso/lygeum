--liquibase formatted sql
--changeset kaiso:init-user-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table lgm_user (
    id BIGINT primary key,
    code varchar(255),
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    username varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    password varchar(500)
);

insert into lgm_user values (1, 'LGMADMUSR01', 'SETUP', CURRENT_TIMESTAMP, 'SETUP', CURRENT_TIMESTAMP, 'lygeum', 'Lygeum', '', '$2a$04$.rgQ2uOg0EbUcO9OeGXqFuPqO87M3csr0C9HUkQTXpZYXBnogDsg2');
commit;