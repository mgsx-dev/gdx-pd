
[![Build Status](https://travis-ci.org/mgsx-dev/gdx-pd.svg?branch=master)](https://travis-ci.org/mgsx-dev/gdx-pd)

[![StackExchange](https://img.shields.io/badge/stackoverflow-gdxpd-green.svg)](http://stackoverflow.com/search?q=gdxpd)

Pure Data extension for LibGDX.

Work in progress :

# Introduction

## What is it ?

LibGDX is a cross platform game framework. If you don't know, please visit : http://www.badlogicgames.com/

Puredata (Pd) is an audio synthesis application coded in C providing graphical programming.
gdx-pd is based on LibPd java bindings. If you don't know, please visit : https://github.com/libpd/libpd

This extension enables audio synthesis in games with pd patches and provides some usefull tools for audio design.

## Features

* Wraps/abstracts libpd in a libGDX fashion.
* Dedicated audio processing thread fully integrated with LibGDX audio implementation : you can use both Pd and Sounds/Musics.
* Pd patch loader for AssetManager.
* Midi sequencers (including "à la live" sequencer).
* Midi file reader/writer and loader for AssetManager.
* Live patching in Pd throw network/OSC, see [Full Live Patching Documentation](doc/LivePatching.md)
* Full Pd Vanilla support including extra externals.
* Easy custom Pd externals build with docker.

## Supported platforms

| Platform   |  Support  |
|------------|-----------|
| Linux 64   |    yes    |
| Linux 32   |    yes    |
| Android    |    yes    |
| Windows 64 |    yes    |
| Windows 32 |    yes    |
| MacOSX 64  |  not yet  |
| MacOSX 32  |  not yet  |
| iOS        |  not yet  |
| Web        |  not yet  |

## Futur works

* Audio 3D spatialization / VR.
* Add support for all platforms.

## Documentation

Full documentation is available in this repository :

* [Getting started](doc/GettingStarted.md)
* [Assets organization](doc/AssetsOrganization.md)
* [Pd patch libraries](doc/PatchLibraries.md)
* [Remote live patching](doc/LivePatching.md)

# How to use

## Configure your LibGDX project

Just add gradle dependencies as usual :

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

**Note** : because of [#3](https://github.com/mgsx-dev/gdx-pd/issues/3), you need to add jitpack as repository 
(this workaround is necessary while OSC release 0.4 is not in Maven Central yet) :

```
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```

[Read more ...](doc/GettingStarted.md)

# Build from sources

Only require java and docker environement for natives.
Tested on Ubuntu 16.04 x64.

First setup your local git clone :

```
git clone https://github.com/mgsx-dev/gdx-pd.git
cd gdx-pd

git submodule init
git submodule update

cd libpd
git submodule init
git submodule update
cd ..
```

You have to tell gradle about your Android sdk location by creating a local.properties file :

```
echo 'sdk.dir=[absolute path to Android SDK location]' > local.properties
```

Build libpd natives with docker :


```
docker run --rm -v $(pwd):/work -w /work/native -it mgsx/libgdx ../gradlew buildNative

sudo chown -R $USER:$USER .
```

And then publish locally in order to use it in your local projects :

```
./gradlew publishToMavenLocal

```

# Running tests and examples

A bunch of examples are provided in "tests" folder. Just import gradle project from "tests" folder and run java classes from desktop project. You don't need to build gdx-pd in order to run these tests.

An example illustrates sound baking during a gradle build. (you have to replace pdVersion variable with latest gdx-pd version) :

	$ cd tests/example-offline
	$ ../gradlew -PpdVersion=0.6.0-SNAPSHOT bake 


# Thanks

TODO
