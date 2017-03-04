
[![Build Status](https://travis-ci.org/mgsx-dev/gdx-pd.svg?branch=master)](https://travis-ci.org/mgsx-dev/gdx-pd)

Pure Data extension for LibGDX.

Work in progress :

| Platform   | Scheduled | implemented | Tested |
|------------|-----------|-------------|--------|
| Linux 64   |     y     |      y      |   y    |
| Linux 32   |     y     |      y      |        |
| Android    |     y     |      y      |   y    |
| Windows 64 |     y     |      y      |        |
| Windows 32 |     y     |      y      |        |
| MacOSX 64  |     -     |      -      |        |
| MacOSX 32  |     -     |      -      |        |
| iOS        |     -     |      -      |        |
| Web        |     -     |      -      |        |

# What is it ?

LibGDX is a cross platform game framework. If you don't know, please visit them : http://www.badlogicgames.com/

Puredata (Pd) is an audio synthesis application coded in C providing graphical programming. 
gdx-pd is based on LibPD java bindings. If you don't know, please visit them : https://github.com/libpd/libpd

This extension enable audio synthesis in games with pd patches and provides some usefull tools  :
* new Music type : PdMusic which play midi files and route midi message to Pd.
* Asset loaders for both Patch and Midi files.
* Message dispatcher.

# Documentation

Full documentation is available in this repository :

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

## Initialize in your launchers


```
// Choose implementation :
Pd.audio = new PdAudioOpenAL(); // Desktop using LibGDX desktop audio implementation (OpenAL)
Pd.audio = new PdAudioDesktop(); // Desktop using JavaSoundImplmentation audio implementation

Pd.audio.create(); // initialize once

PdPatch patch = Pd.audio.open(file); // open a patch

... play with patch ...

Pd.audio.close(patch); // close a patch

Pd.audio.release(); // shutdown audio (before exit)
```

## Play with it

### Interact with patch

Most of PdAudio methods are same as LibPD PdBase class (send, read/write arrays), see PdAudio javadoc for details.

You can register listeners to receive message from pd :

```
Pd.audio.addListener("symbol", new PdListener(){ ... })
```

### Playing music

In order to play midi stream, your patch should implement some of general midi. You can start with provided example
"pd/midiplayer.pd".

To play a music, first open the patch and then load the midi file (.mid extension is automatically recognized).
You can then use LibGDX Music API to play it (Music.play())


## Take advantages of Live coding

Designing a pd patch for a game could be cumbersome : you have to modify your patch in pd, launch your app and
get to the context.

With LibGDX you can already live code with JVM hot code swapping. With pd, you can use the OSC implementation which
send all message to network in OSC format. You can then open your patch in Puredata and modify it directly.

```
Pd.audio = new PdAudioRemote();
```

There is currently some limitations working with arrays. You can write to array throw network but not read them or
get their size (see #5)

# Build from sources

Only require java and docker environement.
Tested on Ubuntu 16.04 x64.

```
git clone https://github.com/mgsx-dev/gdx-pd.git
cd gdx-pd

git submodule init
git submodule update

cd libpd
git submodule init
git submodule update
cd ..

docker run --rm -v $(pwd):/work -w /work/native -it mgsx/libgdx ../gradlew buildNative

sudo chown -R $USER:$USER .

./gradlew publishToMavenLocal

```

## Run examples

A demo application is available in sources and will be published soon on Android store.

# Credits

Demo application is shipped with some midi files authorized by the author Jason "Jay" Reichard who published a lot of
nice old school game music covers, please have a look at his website : http://zorasoft.net/midi.html



