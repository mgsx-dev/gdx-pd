*DRAFT*

# Patch dependency management

There is not really dependency management concept in Pd in order to publish/reuse Pd patches.
We provide here a way to cleanly implements Pd patch dependy management. This implementations is based on gradle
and jitpack.

Obviously, there are many ways to addres this need : you can manually copy patches in your project, use git submodules,
have your own dependency management, there are some ugly ways and better ways, hopefully, you still free to do whatever
you want and we just provide a clean and easy way to do it.

We recommand to use some conventions but you could adapt it to your needs. Project structure is like this :

```
	android
	|- assets
	   |- pd
	      |- libs
	         |- lib1
	         |- lib2
	         |- lib3
	      |- my-main-patch-1.pd
	      |- my-main-patch-2.pd
	      |- my-abstractions
	         |- abs1.pd
	         |- abs2.pd

```

Then in your main patches, you can easily declare path to libraries like this :

```
declare -path libs/lib1
declare -path libs/lib2
declare -path libs/lib3
```

This mecanism enable patch file resolutin both in LibGDX runtime context and Pd application context.


## configure your LibGDX / gdx-pd project

In order to add pd patch dependencies, you need to changes things a little in your gradle build scripts.
First add jitpack as a dependency provider in your root build.gradle file and then add some pd dependencies :

```
allprojects {
    ...
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

Now you can add some pd dependencies in the same file in the android project configuration. We put pd dependencies in
android project because it own assets directory. If you don't target android, you have to modify the project (dekstop?)
owning assets directory.

```
project(":android") {
    ...
    configurations { 
    	natives
    	pd
    }

    dependencies {
        ...
        pd 'com.github.mgsx-dev:gdx-pd-patchbank:master-SNAPSHOT'
        ...
    }
}

```

In order to have your library patches accessible form your patch and shipped within your application, you have to
tell gradle to extract these dependencies in your assets directory. So just add the following in your android project
gradle.build :

```
task copyPdPatches() {
	configurations.pd.resolvedConfiguration.resolvedArtifacts.each { artifact ->
      copy {
        from zipTree( artifact.getFile() )
        into 'assets/pd/libs/' + artifact.name
      }
    }
}
```

Since you don't want to track these auto exracted files, you have to git ignore them. Just create (or modify) .gitignore file
in android project :

```
/assets/pd/libs/
```

That's it. To verify it's working, just refresh your android project with eclipse (gradle/refresh) or run gradle :

```
./gradlew check
```

You should have pd patch library extracted in assets/pd/libs/gdx-pd-patchbank folder.

## Add or update some pd patch libraries

To add a new library, just add dependency as we do in setup. To update, just change version or URL. In this case you should
cleanup libs directory before.


# Create your own library

## Setup your pd library and use it locally

TODOC : see gdx-pd-patchbank configuration


```

	git init mypdlib
	cd mypdlib
	gradle init

```

Customize your build script in order to publish your distribution. group and version have to be adapted.
```

	buildscript {
	    repositories {
	        mavenCentral()
	    }
	}
	
	apply plugin: 'base'
	apply plugin: 'maven-publish'
	
	group = 'my.group.id'
	version = '0.0.1-SNAPSHOT'
	
	task patchZip(type: Zip) {
	    from files('lib')
	}
	
	publishing {
	    publications {
	        maven(MavenPublication) {
				artifact patchZip
	        }
	    }
	}
```

Finally put your patch in the lib folder

```

	mkdir lib
	mv /my/path/to/my/lib/* lib/
```

and publish locally your lib in order to use it within local projects ;

```
	
	gradle publishToMavenLocal
```

Now you can use your library in your local projects like this : 

```

    dependencies{
    	...
    	pd "com.github.mgsx-dev:gdx-pd-patchbank:master-SNAPSHOT"
    	...
    }
```

## Publish your library

TODOC : like any script language, just use jitpack

There are many ways to distribute your libray : publish to maven central ... but the simplest way is to use jitpack.

First you need a GIT repository

```
cd mypdlib
git init .
```

Configure your remote (create your github repo and add it to your remote) and push :
```
git push
```

then in your gradle project use now jitpack url pattern as foolow (assuming your github account is "me" and your repo is "mypdlib" :

```

    dependencies{
    	...
    	pd "com.github.me:mypdlib:master-SNAPSHOT"
    	...
    }
```
note that you can replace "master-SNAPSHOT" by any other jitpack url : another branch, a tag, a commit hash ...

Then you (or anybody else) have to refresh the gradle project :

```

    gradle check
```

