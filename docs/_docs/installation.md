---
title: Server Installation and Configuration
permalink: /docs/installation/
redirect_from: /docs/index.html
---

## Overview

The purpose of this guide is to walk through the steps that need to be completed prior to booting up the Lygeum server for the first time. If you just want to test drive Lygeum, it pretty much runs out of the box with its own embedded and local-only database. For actual deployments that are going to be run in production you’ll need to configure a shared database for Lygeum storage. This guide will show you how it is simple to deploy a production ready Lygeum server.<br><br>

## Booting Lygeum

Running lygeum is very simple, the server does not require any installation process, you can either download the fat jar and run it or boot the docker official image.

## Fat jar running mode
Assuming you have installed the Java Runtime Environment with version 11 or above.
1. First download the jar binary [from the downloads page](/lygeum/downloads).
2. start lygeum server with the following command:
```bash
java -jar lygeum-server-<version>.jar <arguments>
```
the arguments are the following and are optional if you want just to explore Lygeum in test mode.

| Argument      | Description                |
| ------------- |:--------------------------:|
| --db-vendor   | The database system vendor currently `postgres` or `h2`(default embedded `h2`) |
| --db-host   | The database server host (required if vendor is postgres)      |
| --db-database   | The database to use (default `lygeum`)    |
| --db-schema   | The database schema (default `public`)    |
| --db-user   | The database user (default `postgres`)     |
| --db-password   | The database user password (default `postgres`)     |

## Docker

### Run the server with the default embedded database<br>
_This should be used only for test purposes_
```bash
docker run -itd -p 5000:5000 kaiso/lygeum:latest
```
You can go on the administration console by visiting `http://localhost:5000`
<br>

### Run the server with PostgreSQL backend<br>
_Suitable for production environment_

```bash
docker run -itd -p 5000:5000 -e DB_VENDOR=postgres (add other database environment variables here) kaiso/lygeum:latest
```
### Environment variables to specify:
`DB_VENDOR` : the database vendor, currently `h2` or `postgres`<br>
`DB_HOST` : the hostname or ip of the database<br>
`DB_PORT`: Specify port of the database (optional, default is DB vendor default port)<br>
`DB_DATABASE`: Specify name of the database to use (optional, default is lygeum).<br>
`DB_SCHEMA`: Specify name of the schema to use for DB that support schemas (optional, default is public on Postgres).<br>
`DB_USER`: Specify user to use to authenticate to the database (optional, default is postgres).<br>
`DB_PASSWORD`: Specify user's password to use to authenticate to the database (optional, default is postgres).
<br>

##### For more information about Lygeum docker setup instructions [see here](https://hub.docker.com/r/kaiso/lygeum)
<br>

## Access the Lygeum server console
* Open `http://<host or ip>:5000` in your favorite browser and login with the default credentials:
  * user: `lygeum`
  * password: `lygeum`
_(It is recommanded to change the default password in non test deployment)_