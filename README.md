LanteaCraft
================
Restructure and overhaul to the original SGCraft mod by Greg Ewing.

Build & health service: http://lanteacraft.com/jenkins/job/LanteaCraft/ ![ScreenShot](http://lanteacraft.com/jenkins/job/LanteaCraft/badge/icon/build_status.png)


Credits:
================
* Michiyo Ravencroft: "Yes".
* AfterLifeLochie: Head Code monkey. 
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
The build tasks and gradle setup has been configured to operate out of the box, but requires some user-end setup before it will work properly.

1. Use the pre-set Eclipse directory as your workspace.
2. By default, you should already have /src/main/java set as a Source folder. If not, add it.
3. Add /src/externs/java and /src/main/resources as Source folders.
4. You may need to add libraries, and set the LWJGL natives folder (/build/natives; perform setp 13 if you don't have this already)
5. Create a new /runtime folder, case sensitive.
6. Create a new Debug profile, give it any name.
7. Select the Target Project as the LanteaCraft root source.
8. When launching as a client use 'net.minecraft.launchwrapper.Launch' as the Main.
9. Tell Forge to use 1.6 by setting the Program Arguments to '--version 1.6 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker'.
10. Tell Forge to ignore any invalid chain certificates by adding '-Dfml.ignoreInvalidMinecraftCertificates=true' to the VM arguments.
11. Set the Working Directory to the /runtime folder.
12. If you need to specify a JDT launcher, select one, then press Apply.
13. If you find you have no /build/natives/ directory after performing a build or jar task, use `gradle[w] eclipse --refresh-dependencies`.
14. If you intend to run the `gradle[w] jar` task, you need to set BUILD_NUMBER and JDK_HOME. BUILD_NUMBER is usually set by Jenkins, see http://lanteacraft.com/jenkins/env-vars.html.