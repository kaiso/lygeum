---
title: Getting started
permalink: /docs/home/
redirect_from: /docs/index.html
---

**`NB: The Lygeum server is currently in beta version and the docs are under construction`**


This guide will help you start with Lygeum. it covers installing and booting the server as well as configuring a first environment and application and downloading the associated configuration
### Download & Install
Running lygeum is very simple, you can either download the fat jar and run it or boot the docker official image.
#### Fat jar
Assuming you have installed the Java Runtime Environment with version 11 or above.
1. First download the [jar binary from here](https://bintray.com/kaiso/lygeum/download_file?file_path=lygeum-server-0.1.0b1.jar).
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

3. Open [http://localhost:5000](http://localhost:5000) in your favorite browser and login with the default credentials:
  * user: `lygeum`
  * password: `lygeum`
_(It is recommanded to change the default password in non test deployment)_

#### Docker
##### see [docker setup instructions here](https://hub.docker.com/r/kaiso/lygeum)

### Set up your first configuration
After you have logged in:
1. Select **`Configurations`** from the left side bar
2. Go to **`Environments`** and click on the **`Add`** button to add an environment, type the name and click **`save`**
3. Back to **`Configurations`** and select **`Applications`**
4. Click **`Add`** button to add an application, type the name and click **`save`**
5. Back to **`Configurations`** and select **`Properties`**
6. Select the environment from the dropdown and type the name of the application in the application field
7. Click **`Add`** button to add a property, type the name and the value and **`save`**

Now you have created your first configuration environment and application

### Use your configuration through the Lygeum CLI
In order to configure the Lygeum **Command Line Interface**, first make sure you have Python 3.5 or above installed.
1. install the Lygeum CLI via the following command:
```bash
pip3 install lygeumcli
```
2. Now you have to add a client to access the configuration created before so go to the Lygeum console again and browse to **`Administration > Clients`**
3. Create a new client by giving a name and then save
4. Copy the **Client ID** and the **Client Secret** we will use them in the next step.
5. Configure the Lygeum CLI by running:
```bash
lygeum configure
```
6. answer the questions by providing the client id, the client secret and the server url<br>
**(the url in our case it is `http://localhost:5000` if you are running the CLI on the same machine)**

7. Use the lygeum CLI to download your configuration by running:
```bash
lygeum properties download -e <the env> -a <the app> -l <layout> -f <the path to the file>
```
* The <`layout`> may be json, properties or yaml
* Example:
```bash
lygeum properties download -e my-env -a my-app -l json -f /tmp/my-config.json
```

**Congratulations you have achieved your first Lygeum centralized configuration**