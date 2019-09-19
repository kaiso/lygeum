--liquibase formatted sql
--changeset kaiso:init-sequences author-name:Kais OMRI
DROP SEQUENCE IF EXISTS LGM_SEQUENCE;
CREATE SEQUENCE LGM_SEQUENCE INCREMENT BY 1 START WITH 51 ;
