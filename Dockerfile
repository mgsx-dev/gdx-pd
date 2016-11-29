from ubuntu

# Install git
RUN apt-get update -y && apt-get install -y wget git 

RUN mkdir /libgdx

WORKDIR /libgdx

# Download packr openjdk images
RUN wget https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u80-unofficial-macosx-x86_64-image.zip
RUN wget https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u80-unofficial-windows-amd64-image.zip
RUN wget https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u80-unofficial-linux-amd64-image.zip

# Download packr.jar
RUN wget http://libgdx.badlogicgames.com/packr/packr.jar

RUN apt-get install -y g++-mingw-w64-i686 g++-mingw-w64-x86-64

RUN apt-get install -y openjdk-8-jre openjdk-8-jdk

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64


RUN apt-get install -y ant gcc g++ gcc-multilib g++-multilib

RUN apt-get install -y make



COPY . /gdx-pd

WORKDIR /gdx-pd

RUN ./gradlew

ENV NDK_HOME /ndk

VOLUME /ndk