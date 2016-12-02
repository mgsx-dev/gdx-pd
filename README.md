
Pure Data extension for LibGDX.

Work in progress

# How to use

## Use in your LibGDX projects

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
        compile "net.mgsx.gdx:gdx-pd-platform:$pdVersion:natives-desktop"
        ...
    }
}
```

## API

### LibGDX extension layer

In your desktop launcher :

```
Pd.audio = new PdAudioDesktop(); // configure implementation
		
Pd.audio.create(); // initialize once

Pd.audio.open(file); // open a patch

...

Pd.audio.release(); // shutdown audio (before exit)
```

### LibPD layer

PdBase class provides all you need to send/receive event to/from pure data.

See LibPD documentation for details.




# Work with sources

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

gradle publishToMavenLocal

```



