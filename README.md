LanteaCraft
================
Restructure and overhaul to the original SGCraft Greg Ewing.

Build & health service: http://kenobi.pc-logix.com:8080/job/LanteaCraft/ ![ScreenShot](http://kenobi.pc-logix.com:8080/job/LanteaCraft/badge/icon/build_status.png)

TODO:
================
* Adding 7X7 stargates, Atlantis along with a DHD for it. [IN PROGRESS]
* Adding Iris to stargates. [IN PROGRESS]
* Adding a Milky Way DHD model instead of a block. [DONE]
* Adding a chargable crystal for power from the DHD [IN PROGRESS]
* Allowing DHD to power the Stargate [IN PROGRESS]
* Adding Naquadah Generators. [IN PROGRESS]
* Adding Custom Decor Blocks. [Not Started]
* Adding Weapons of all kinds. [Not Started]
* Adding a form of MALP if possable [Not Started]
* Adding Transport Rings [Not Started]


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

Gradle Configuration
================
Gradle has been configured to operate out of the box, but requires some launch configuration setup before it will work properly. To do this;

1. Create a new Debug profile. Give it any name.
2. Select the Target Project as the LanteaCraft root source.
3. When launching as a client use 'net.minecraft.launchwrapper.Launch' as the Main.
4. Tell Forge to use 1.6 by setting the Program Arguments to '--version 1.6 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker'.
5. Tell Forge to ignore any invalid chain certificates by adding'-Dfml.ignoreInvalidMinecraftCertificates=true' to the VM arguments.
6. Create a new folder in the root of the project called 'runtime' (case sensitive), and then set the Working Directory to the runtime folder. The runtime folder has been configured so that it is ignored by Git.
7. If you need to specify a JDT launcher, select one, then press Apply.