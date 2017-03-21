
# How do I organize my Pd patches as LibGDX assets

## Android and classpath considerations

LibPD and Pd itself load patches from a directory. Hence there is for now no way to use classpath or streams (Zip/Jar) to let Pd retrieve dependencies (used abstractions).

When you load a patch with gdx-pd, Android implementation will extract your patch and all its content from its parent directory. This ensure abstraction and other resources (WAV tables, ...Etc) can be loaded by Pd.

When you read from classpath, there is no way to know parent folder from assets at root classpath level, that is directly in your root assets folder. So recommandation is to put all your Pd patches in a folder within your assets directory. This will enable gdx-pd to extract the whole directory. Also, keep in mind the whole folder (and subfolders) will be extracted, so just include in this folder what your patches really needs, this will reduce loading time.

TODOC : caching strategy.

Note that this limitation is also applyable with desktop distribution if you package your patches in a Jar or use classpath to access a folder.

Conclusion : **Do not put patches or any related resources directly in your assets root folder, use a subfolder instead.**

Here is an example of folder structure :

```
	android
	|- assets
	   |- pd
	      |- my-patch-1.pd
	      |- my-patch-2.pd
	      |- my-abstractions
	         |- abs1.pd
	         |- abs2.pd

```