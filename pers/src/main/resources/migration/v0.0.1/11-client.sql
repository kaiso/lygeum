--liquibase formatted sql
--changeset kaiso:init-client-details-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
CREATE TABLE lgm_client (
    id BIGINT PRIMARY KEY,
    code VARCHAR(256) UNIQUE,
    createdBy varchar(255),
    createdDate timestamp,
    lastModifiedBy varchar(255),
    lastModifiedDate timestamp,
    name VARCHAR(256),
    resource_ids VARCHAR(256),
    client_secret VARCHAR(256),
    scope VARCHAR(256),
    authorized_grant_types VARCHAR(256),
    web_server_redirect_uri VARCHAR(256),
    access_token_validity INTEGER,
    refresh_token_validity INTEGER,
    additional_information VARCHAR(4096),
    autoapprove VARCHAR(256)
);

INSERT INTO lgm_client
    (id, code, name, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, access_token_validity,
    refresh_token_validity, additional_information, autoapprove, resource_ids,
    createdBy, createdDate, lastModifiedBy, lastModifiedDate)
VALUES
    (1, 'ui', 'ui', 'lygeumok', 'openid', 'password,authorization_code,refresh_token', null, 36000, 36000, null, true, 'lygeum-server', 'SETUP', CURRENT_TIMESTAMP, 'SETUP', CURRENT_TIMESTAMP);
commit;