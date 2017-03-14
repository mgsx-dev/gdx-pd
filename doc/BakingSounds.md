
# Baking sounds

Baking is a common technic well known in game developement which is to pre-render stuff like lighting,
global illumination, normal map, ...and so on into textures.

Generaly speaking baking is just pre-processing/caching computationnaly intensive tasks. Like procedural
textures, procedural sounds could be baked as well.

As any kind of baking, main drawback is loss of dynamic behavior of baked piece : baked light map can't change
if light orientation change (unless you baked it again in realtime which is beyond that point). Same way,
baked sounds loose any realtime effets.

Another drawback is seamlessness : a realtime procedural perlin noise is seams free but baked noise texture
have to be tileable in order to get seamless free. It also apply to sounds obviously : if you bake a continuous
wind sound, you may hear audio click when at loop points.

Finally, baking can be done at different stage (from early to late stage) :
* At design time : you bake things during developement, fine tuning it and store in your repository.
* At build time : some can be auto-generated at build time and shiped within your assets.
* At install time : to avoid shipping huge data in your application, you may want to bake things at application first loading, you have to make compromise between extra loading time and extra application storage.
* At load time : to avoid storage space, you could bake at each application/level loading, in this case you bake in memory only.
* In realtime : Sometimes you need to bake some part of your huge openworld map in realtime (with deffered rendering), for instance shadow maps in that kind of realtime baking : instead of compute complex ray casting which
is not always possible, you "bake" (or render) objets into FBO and use this texture in a second pass to project shadows on objets.

Baking time is a design choice and depends of what you want to achieve (what is required) and what are the constraints. But keep in mind, wanting to bake everything is not the right solution (baking a simple bip sounds is like baking a gradient texture) sometimes performance could be worse. In the other hand, baking nothing is not
always a good idea : hyper complex sounds which doesn't vary so much in your game will eat CPU for almost nothing.

Anyway, gdx-pd provide baking out of the box. Unfortunately, baking in realtime is not possible yet because the mono process nature of Pd (but it might change soon).

## Baking your assets

Baking at design time can be acoomplished with large variety of tools, if you like Pd to design your sounds, you can already bake them with Pd (writesf~ ...) but if you want to incorporate baking at build time, you can try pd-offline, it's a libpd ruby wrapper made by gdx-pd team as well.
You could also directly use gdx-pd in your gradle build scripts using groovy.

## Baking at runtime

gdx-pd provides an example (AudioGdxBakingTest) which illustrate who to do it. Just keep in mind, any other Pd patches can't run during baking process but you could always used LibGDX music facility to make your user wait with some music.

Note that baking is possible both in normal mode and in remote mode. The only limitation is you have to ensure your
destination arrays to have the right size since remote implementation is not able to know array size yet.

## Few advices

* use tileable waveform (use vnoise~ instead of noise~) in order to loop them.
* just bake what is interesting to bak, for instance, you can bake a complex waveform but keep realtime bandpass filtering.
* using envelops with zero amplitude at begin / end ensure sounds to be seamless.
* baking several variations of the same sound and blend all at runtime can give good results.
* granular synthesis / sound fonts can be a good solution in some cases.
* sound stretching is like texture stretching : it leads to undesirable artifacts but like with texture, mipmaps technic can be used with sounds as well.
* again, samplers could be costly (specially tabread4~ with high polyphony), simple procedural sounds don't have to be baked most of the time.

