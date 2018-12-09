FROM openjdk:8
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update
RUN apt-get install -y git wget maven software-properties-common dirmngr default-jdk default-jre

RUN mkdir /emeraldquest
COPY . /emeraldquest/
RUN mkdir -p /spigot/plugins

WORKDIR /spigot

# DOWNLOAD AND BUILD SPIGOT
ADD https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar /tmp/BuildTools.jar
RUN export SHELL=/bin/bash && cd /tmp && java -jar BuildTools.jar --rev 1.12.2
RUN cp /tmp/Spigot/Spigot-Server/target/spigot-*.jar /spigot/spigot.jar
RUN cd /spigot && echo "eula=true" > eula.txt
COPY server.properties /spigot/
COPY bukkit.yml /spigot/
COPY spigot.yml /spigot/
RUN export SHELL=/bin/bash && cd /emeraldquest/ && mvn clean compile assembly:single
RUN cp /emeraldquest/target/EmeraldQuest.jar /spigot/plugins/
# Add the last version of NoCheatPlus
# ADD http://ci.md-5.net/job/NoCheatPlus/lastSuccessfulBuild/artifact/target/NoCheatPlus.jar /spigot/plugins/NoCheatPlus.jar
WORKDIR /spigot

CMD java -jar spigot.jar
