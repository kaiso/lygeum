--liquibase formatted sql
--changeset kaiso:init-sequences author-name:Kais OMRI
DROP SEQUENCE IF EXISTS APS_SEQUENCE;
CREATE SEQUENCE APS_SEQUENCE INCREMENT BY 1 START WITH 1 ;
