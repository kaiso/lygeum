--liquibase formatted sql
--changeset kaiso:init-client-details-table author-name:Kais OMRI
--preconditions onFail:HALT onError:HALT
create table lgm_client (
    code VARCHAR(256) PRIMARY KEY,
    name VARCHAR(256),
    resource_ids VARCHAR(256),
    client_secret VARCHAR(256),
    scope VARCHAR(256),
    authorized_grant_types VARCHAR(256),
    web_server_redirect_uri VARCHAR(256),
    authorities VARCHAR(256),
    access_token_validity INTEGER,
    refresh_token_validity INTEGER,
    additional_information VARCHAR(4096),
    autoapprove VARCHAR(256)
);

INSERT INTO lgm_client
    (code, name, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, authorities, access_token_validity,
    refresh_token_validity, additional_information, autoapprove)
VALUES
    ('ui', 'ui', 'lygeumok', 'openid', 'password,authorization_code,refresh_token', null, null, 36000, 36000, null, true);
commit;