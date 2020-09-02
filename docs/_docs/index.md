---
title: Getting started
permalink: /docs/home/
redirect_from: /docs/index.html
---



This guide will help you start with Lygeum. it covers installing and booting the server in **test mode** as well as configuring a first environment and application and downloading the associated configuration
<br><br>

### 1. Download & Install
Running lygeum is very simple, you can either download the fat jar and run it or boot the docker official image.
go to [download page](/lygeum/downloads) and download the lygeum server based on your desired installation type.
<br><br>

### 2. Start lygeum server
For this tutorial Lygeum will run in test mode with an embedded database if you want to run Lygeum in production check out [the detailed installation instructions](/lygeum/docs/installation/)
* Fat jar
```bash
java -jar lygeum-server-<version>.jar 
```
* Docker 
```bash
docker run -itd --name lygeum -p 5000:5000 kaiso/lygeum:latest
```
<br><br>

### 3. Open [http://localhost:5000](http://localhost:5000) in your favorite browser and login with the default credentials:
  * user: `lygeum`
  * password: `lygeum`
_(It is recommanded to change the default password in non test deployment)_
<br><br>

### 4. Set up your first configuration
After you have logged in:
1. Select **`Configurations`** from the left side bar
2. Go to **`Environments`** and click on the **`Add`** button to add an environment, type the name and click **`save`**
3. Back to **`Configurations`** and select **`Applications`**
4. Click **`Add`** button to add an application, type the name and click **`save`**
5. Back to **`Configurations`** and select **`Properties`**
6. Select the environment from the dropdown and type the name of the application in the application field
7. Click **`Add`** button to add a property, type the name and the value and **`save`**

Now you have created your first configuration environment and application
<br><br>

### 5. Use your configuration through the Lygeum CLI
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
* The `layout` may be `json`, `properties` or `yaml`
* Example:
```bash
lygeum properties download -e my-env -a my-app -l json -f /tmp/my-config.json
```
<br><br>

### Congratulations you have achieved your first Lygeum centralized configuration