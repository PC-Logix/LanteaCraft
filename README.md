LanteaCraft
================
Restructure and overhaul to the original SGCraft mod by Greg Ewing.


Credits:
================
* Michiyo Ravencroft: Coder, Founder.
* AfterLifeLochie: Head Code monkey, Co-Founder. 
* DrakeBD & TempusBD (DeltaStrium): Textures, sounds and models.
* Greg Ewing: Original SGCraft sourcecode, since refactored.

License
================
LanteaCraft may be distributed by anyone - this includes modpacks - under the following conditions:
* You don't make money from downloads - be this pay-gating (AdFly) or other revenue;
* You distribute LanteaCraft un-modified and in whole; and
* We'd much like it if you linked back to our topic (or the Github repository).

The textures & 3D models in LanteaCraft are proprietarily produced by DeltaStrium and are the property of DeltaStrium & PC-Logix. You are not permitted to distribute or copy any textures and/or 3D models in any manner outside the official distribution form(s) without prior written permission.

Building & Gradling.
================

Setup
----------------
You should setup the decompile environment if you plan on using Eclipse:

1. `gradlew setupDecompWorkspace` to build the deobfuscation environment for the first time.
2. `gradlew eclipse [--refresh-dependencies]` to build Eclipse project files. Use the `refresh-dependencies` if the job fails; quite often than not the AmazonAWS causes resources to fail silently or violently.


The build tasks and gradle setup has been configured to operate out of the box, but requires some user-end setup before it will work properly.

1. Use the pre-set Eclipse directory as your workspace.
2. By default, you should already have `/src/main/java` set as a Source folder; if not, add it.
3. Add `/src/externs/java` and `/src/main/resources` as Source folders if they are not Source folders.
4. You may need to add libraries, and set the LWJGL natives folder (`/build/natives`; perform step 13 if you don't have this directory already).
5. Create the `/runtime` and the `/server_runtime` folder, case sensitive.
6. Create a new Debug profile, give it any name.
7. Select the Target Project as the LanteaCraft root source.
8. When launching as a client use `net.minecraft.launchwrapper.Launch` as the Main.
9. Tell Forge to use 1.6 by setting the Program Arguments to `--version 1.6 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker`.
10. Tell Forge to ignore any invalid chain certificates by adding `-Dfml.ignoreInvalidMinecraftCertificates=true` to the VM arguments.
11. Set the Working Directory to the `/runtime` folder.
12. If you need to specify a JDT Launcher, select one, then press Apply.

Erratum
----------------
* If you intend on testing next to other mods which are linked dependencies, you **must** add `-Dfml.coreMods.load=pcl.lc.coremod.LCCoreMod` to the VM arguments of your configuration. Forge will not detect coremods which are not in jar format, even if they exist in /bin.
* If you find you have no `/build/natives` directory after performing a build or jar task, use `gradle[w] eclipse --refresh-dependencies`.
* If you intend to run the `gradle[w] jar` task, you need to set `JAVA_HOME`. `BUILD_NUMBER` is usually set by Jenkins and will default to `0` if not set in the environment (see http://lanteacraft.com/jenkins/env-vars.html).
* More often than not, gradle doesn't create the assets cache in `/.gradle/assets`. You should copy it from your local Minecraft folder to this directory.
