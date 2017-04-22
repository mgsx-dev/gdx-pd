
[![Build Status](https://travis-ci.org/mgsx-dev/gdx-pd.svg?branch=master)](https://travis-ci.org/mgsx-dev/gdx-pd)

Pure Data extension for LibGDX.

# Introduction

## What is it ?

LibGDX is a cross platform game framework. If you don't know, please visit : http://www.badlogicgames.com/

Puredata (Pd) is an audio synthesis application coded in C providing graphical programming.
gdx-pd is based on LibPd java bindings. If you don't know, please visit : https://github.com/libpd/libpd

This extension enables audio synthesis in games with pd patches and provides some usefull tools for audio design.

## Features

* Wraps/abstracts libpd in a libGDX fashion way.
* Audio processing thread fully integrated with LibGDX audio implementation : you can use both Pd and Sounds/Musics.
* Pd patch loader for AssetManager.
* Midi sequencers (including "à la live" sequencer).
* Midi file reader/writer and loader for AssetManager.
* Full Pd Vanilla support including extra externals.
* Live patching in Pd throw network/OSC, see [Full Live Patching Documentation](doc/LivePatching.md)
* Audio baking at runtime or during gradle build.

## Supported platforms

| Platform   | Supported |
|------------|-----------|
| Linux 64   |    yes    |
| Linux 32   |    yes    |
| Android    |    yes    |
| Windows 64 |    yes    |
| Windows 32 |    yes    |
| MacOSX 64  |    yes    |
| MacOSX 32  |    yes    |
| iOS        |  not yet  |
| Web        |  not yet  |

## Documentation

Full documentation is available in this repository :

* [Getting started](doc/GettingStarted.md)
* [Assets organization](doc/AssetsOrganization.md)
* [Working with MIDI](doc/WorkingWithMidi.md)
* [Remote live patching](doc/LivePatching.md)

## Showcase and Resources

* **Rainstick** : A rainstick [available on Android](https://play.google.com/store/apps/details?id=net.mgsx.rainstick). Sources [here](https://github.com/mgsx-dev/rainstick)
* **gdx-pd demo** : Gdx-Pd features demo available for [Desktop](https://github.com/mgsx-dev/gdx-pd-demo/releases) and [Android](https://play.google.com/store/apps/details?id=net.mgsx.pd.demo). Sources [here](https://github.com/mgsx-dev/gdx-pd-demo)
* **pd-patchbank** (coming soon) : A "Starter Kit" collection of pd patches. 

## Futur works

* Deploy on Maven Central
* Audio 3D spatialization (with a VR demo).
* Add support for all platforms.
* Easy custom external integration.

# How to use

## Configure your LibGDX project

Just add gradle dependencies as usual :

**Jars are not available on Maven Central yet. Please read the "Build from sources" section.**

```
project(":core") {
    apply plugin: "java"
    dependencies {
        ...
        compile "net.mgsx.gdx:gdx-pd:$pdVersion"
        ...
    }
}

project(":desktop") {
    apply plugin: "java"
    dependencies {
    	compile project(":core")
        ...
        compile "net.mgsx.gdx:gdx-pd-platform:$pdVersion:desktop"
        compile "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-desktop"
        ...
    }
}

project(":android") {
    apply plugin: "android"

    configurations { natives }

    dependencies {
        compile project(":core")
        ...
        compile "net.mgsx.gdx:gdx-pd-backend-android:$pdVersion"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-armeabi"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-armeabi-v7a"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-arm64-v8a"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-x86"
        natives "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-x86_64"
		 ...        
    }
}

```

[Read more ...](doc/GettingStarted.md)

# Build from sources

Note that pre-built native binaries are already tracked in this repository and doesn't need to be built exept changes in C sources.

First setup your local git clone :

	$ git clone https://github.com/mgsx-dev/gdx-pd.git
	$ cd gdx-pd
	$ git submodule init
	$ git submodule update

You have to tell gradle about your Android sdk location by creating a local.properties file :

	$ echo 'sdk.dir=[absolute path to Android SDK location]' > local.properties

Optionnaly you may want to publish locally in order to use it in your local projects (including gdx-pd-tests projects) :

	$ ./gradlew publishToMavenLocal

## Re-Build natives

First fetch pd sources :

	$ cd gdx-pd/libpd
	$ git submodule init
	$ git submodule update
	$ cd ..

### Build Linux, Android and Windows binaries

Linux, Android and Windows binaries can be built on any platform supporting Docker (Linux, Windows and OSX).
It only requires Docker installed (see https://docs.docker.com/engine/installation/), NDK and other cross compiler tools are already included in the docker image.

To rebuild binaries, just run command below :

	$ docker run --rm -v $(pwd):/work -w /work/native -it mgsx/gdx-docker ../gradlew buildNative
	$ sudo chown -R $USER:$USER .

### Build OSX binaries

Only OSX bianries require a Mac development environnement :
* XCode and command line tools (make, gcc, g++...)
* Java 1.8+
* homebrew : https://brew.sh
* Ant : `brew install ant`

To rebuild binaries, just run command below :

	$ cd native
	$ ../gradlew generateBuildScripts
	$ ant -f jni/build-macosx32.xml -v -Dhas-compiler=true clean postcompile
	$ ant -f jni/build-macosx64.xml -v -Dhas-compiler=true clean postcompile


# Running tests and examples

A bunch of examples are provided in [desktop test project](tests/desktop/src/net/mgsx/pd). Just import gradle project from "tests" folder and run java classes. You don't need to build gdx-pd in order to run these tests.

An example illustrates sound baking during a gradle build. (you have to replace pdVersion variable with latest gdx-pd version) :

	$ cd tests/example-offline
	$ ../gradlew -PpdVersion=0.6.0-SNAPSHOT bake 

# Licence

Pure Data and LibPD (git submodules and native binaries) are licenced under the [Standard Improved BSD License](PDLICENCE)

Gdx-pd is licensed under the [Apache 2 License](LICENCE)

# Thanks

Thanks to Miller Puckette for [Pure Data](http://puredata.info).

Thanks to Peter Brinkmann & the libpd team for their [Pure Data wrappers](https://github.com/libpd/libpd)

Thanks to [b2renger](https://github.com/b2renger) for his contribution in all gdx-pd related projects.

