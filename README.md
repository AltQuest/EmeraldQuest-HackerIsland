# EmeraldQuest

[EmeraldQuest](http://emeraldquest.co/) is a Minecraft server with a emerald-denominated currency and MMORPG elements. This repository is the open source code running on the server.

# Play EmeraldQuest
To play in the official EmeraldQuest server you must own the official Minecraft game for PC/Mac/Linux and add this server address: 
```sh
play.emeraldquest.co
```

# Important Note
This server is still under development, and bugs can, and most likely will, happen. Please report any bugs to a moderator promptly, to ensure they are fixed. Also note that your balance is not guaranteed, and you may experience issues with your balance. For this reason, it is highly recommended that you do not move large amounts. In addition, if you are caught breaking rules and are banned, you forfeit your access to your server resources, and all land tied to it. With this said, rules are non-negotiable, and will be strictly enforced. This server is a fair server, and anyone abusing any system set in place will be punished. Please play fair, have fun, and enjoy the server


# How it works?
## Everyone uses Emeralds!
The [EmeraldQuest](https://emeraldquest.co/) server and every player has a fair chance. Any player can receive and send emerald to any player inside the game. This is useful for buying materials, selling crafts, trading, tipping, etcetera.
![A player just joined the server](https://media.discordapp.net/attachments/417463752301215770/418990976229572608/EmeraldQuest02.png?width=400&height=225)
![The playercan see it's emerald balance](https://media.discordapp.net/attachments/417463752301215770/418990994692898826/EmeraldQuest03.png?width=400&height=225)

## And there's loot!
Every time a player kills an enemy (mob) there is a chance to get loot. If that is the case the server creates emeralds and gives to the player and the player is notified.
![A player got loot](https://media.discordapp.net/attachments/417463752301215770/418991008492290048/EmeraldQuest04.png?width=400&height=225)

## Everyone can send emerald anywhere
You can send emerald to any player with /send:
```sh
/send <amount> <username>
```
![Player using send command](https://media.discordapp.net/attachments/417463752301215770/537438811764948993/EmSend.png?width=400&height=239)

## Server loot
The EmeraldQuest server creates emerald loot randomly upon mob death, used for giving Loot to players.

## Daily Rewards!
The EmeraldQuest server will give each player 5e + (5e * consecutive login days) upon login every 24 hours.

## About the back-end technology

All persistent data is saved in a redis database so the server can respond as quick as possible. 

Everybody is welcome to contribute. :D

Here are the instructions to modify, install and run the server as localhost.


# Building the EmeraldQuest Java Plugin

## Install bash (Windows only)
To setup the workspace you need to run a gradle script that only runs on bash. You can get a distribution of bash by installing git from the [git-scm](https://git-scm.com/) website.

Warning: building EmeraldQuest is not currently supported on Windows 10 Anniversary edition bash. If you have that feature installed, your build will fail. If you are building using Windows 10 Anniversary edition, it's recommended to uninstall the Windows Subsytem for Linux feature first.

## Setup Workspace
There is a gradle task that will download and compile the latest Spigot API and other tools needed to compile the project. Using a terminal, go to the project directory and run:

````
./gradlew setupWorkspace
````

## Compile EmeraldQuest and generate a JAR file
After the workspace is set up, we can compile using the shadowJar task that will create a file under build/libs. This should be dropped on the plugins folder of your Spigot server, but you can automate the process for testing using Docker (instructions below)

````
./gradlew shadowJar
````
# Requirements for development


# Running a local EmeraldQuest test server
**********************************
How to install emeraldquest on Ubuntu Linux x86_64/64bit with docker. by @BitcoinJake09

1) First we will clone the github emeraldquest...

open terminal

$ git clone https://github.com/AltQuest/emeraldquest

2) Second we are going to install "Docker CE"

$ sudo apt-get update

$ sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common

$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

Verify that you now have the key with the fingerprint 9DC8 5822 9FC7 DD38 854A E2D8 8D81 803C 0EBF CD88, by searching for the last 8 characters of the fingerprint:

$ sudo apt-key fingerprint 0EBFCD88

the above command should result:

pub   4096R/0EBFCD88 2017-02-22
      Key fingerprint = 9DC8 5822 9FC7 DD38 854A  E2D8 8D81 803C 0EBF CD88
uid                  Docker Release (CE deb) <docker@docker.com>
sub   4096R/F273FCD8 2017-02-22

$ sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"

$ sudo apt-get update

$ sudo apt-get install docker-ce

3) Now we are going to install "Docker compose"

$ sudo curl -L https://github.com/docker/compose/releases/download/1.19.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose

and give permission to docker compose

$ chmod +x /usr/local/bin/docker-compose
$ chmod +x gradlew

To verify docker compose is installed you can run this:

$ docker-compose --version

It should result:

docker-compose version 1.19.0, build #######
*************************

Running locally via Docker is the fastest way to develop and test code. [Docker](http://docker.com) and [Docker Compose](https://docs.docker.com/compose/) can be used for testing the compiled plugin on spigot.

1. Build EmeraldQuest using the instructions above (./gradlew shadowJar).
2. Install [Docker](https://docs.docker.com/engine/installation/), and [Docker Compose](https://docs.docker.com/compose/install/) if you haven't yet.
3. Create a docker-compose.yml file with your configuration. A good idea is to create a volume on spigot's 'plugins' pointing to the local directory where .jar files are compiled. Or you can use the following example:

````
spigot:
    container_name: emeraldquest
  environment:
    - EMERLADQUEST_ENV=development
    - DENOMINATION_NAME=Ems	//could change to anything, displays on sidebar
    - LAND_PRICE=10		//this sets ingame land price in emeralds
    - ADMIN_UUID=        	//put you uuid here
    - SET_PvP=true		//sets pvp
  build: .
  volumes:
    - "./build/libs/:/spigot/plugins"
    - ".:/emeraldquest"
  ports:
    - "25565:25565"
  links:
    - redis
redis:
  image: redis


````

4. Use docker-compose to spawn a test server

```
docker-compose up
```
******************************
IF YOU ALREADY HAVE DOCKER AND ENVIRONMENT SET UP ALL YOU HAVE TO DO IS

$git clone "URL OF WHATEVER QUEST FORK YOU WANT"

this will work with any of these so far
https://github.com/bitquest/bitquest
https://github.com/AltQuest/AltQuest-running-transations-broken
https://github.com/AltQuest/emeraldquest

$cd WHATEVERYOUGOT

ex: $cd emeraldquest

$ chmod +x gradlew

   you will want to make or change docker-compose.yml at this point also

$ ./gradlew setupWorkspace

$ ./gradlew shadowJar

$ docker-compose up
****************************
# More info

https://emeraldquest.co/
