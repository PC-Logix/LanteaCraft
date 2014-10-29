Building & Gradling.
================

Setup
----------------
You should setup the decompile environment if you plan on using Eclipse:

1. `gradlew setupDecompWorkspace` to build the deobfuscation environment for the first time.
2. `gradlew eclipse [--refresh-dependencies]` to build Eclipse project files.


The build tasks and gradle setup has been configured to operate out of the box, but requires some user-end setup before it will work properly.

1. Import the project directory into your Eclipse workspace.
2. By default, you should already have `/src/main/java` set as a Source folder; if not, add it.
3. Add `/src/externs/java` and `/src/main/resources` as Source folders if they are not Source folders.
4. You may need to add libraries and set the LWJGL natives folder (`/build/natives`; perform step 13 if you don't have this directory already).
5. Create the `/runtime` and the `/server_runtime` folder, case sensitive.
6. Create a new Debug profile, give it any name.
7. Select the Target Project as the LanteaCraft root source.
8. When launching as a client use `GradleStart` as the Main.
9. Tell Forge to use 1.7 by setting the Program Arguments to `--version 1.7 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker`.
10. Tell Forge to ignore any invalid chain certificates by adding `-Dfml.ignoreInvalidMinecraftCertificates=true` to the VM arguments.
11. You must add `-Dfml.coreMods.load=pcl.lc.coremod.LCCoreMod` to the VM arguments of your configuration. Forge will not detect coremods which are not in jar format, even if they exist in /bin.
12. Set the Working Directory to the `/runtime` folder.
13. If you need to specify a JDT Launcher, select one, then press Apply.

Erratum
----------------
* If you require a specific username for your test, you should set the `--username=` parameter in Program Arguments.
* When testing on servers without logging in, you should manually set the `--uuid=` parameter to match a UUID.
* If you find you have no `/build/natives` directory after performing a build or jar task, use `gradle[w] eclipse --refresh-dependencies`.
* If you intend to run the `gradle[w] jar` task, you need to set `JAVA_HOME` and `BUILD_NUMBER`. 
**`BUILD_NUMBER` is usually set by Jenkins and will default to `0` in gradle tasks if not set in the environment (see http://lanteacraft.com/jenkins/env-vars.html).
