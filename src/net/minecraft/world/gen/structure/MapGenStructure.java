package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase
{
    private MapGenStructureData field_143029_e;

    /**
     * Used to store a list of all structures that have been recursively generated. Used so that during recursive
     * generation, the structure generator can avoid generating structures that intersect ones that have already been
     * placed.
     */
    Map structureMap = new HashMap();

    public abstract String func_143025_a();

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected final void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte)
    {
        this.func_143027_a(par1World);

        if (!this.getStructureMap().containsKey(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par2, par3))))
        {
            this.rand.nextInt();

            try
            {
                if (this.canSpawnStructureAtCoords(par2, par3))
                {
                    StructureStart structurestart = this.getStructureStart(par2, par3);
                    this.getStructureMap().put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par2, par3)), structurestart);
                    this.func_143026_a(par2, par3, structurestart);
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception preparing structure feature");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Feature being prepared");
                crashreportcategory.addCrashSectionCallable("Is feature chunk", new CallableIsFeatureChunk(this, par2, par3));
                crashreportcategory.addCrashSection("Chunk location", String.format("%d,%d", new Object[] {Integer.valueOf(par2), Integer.valueOf(par3)}));
                crashreportcategory.addCrashSectionCallable("Chunk pos hash", new CallableChunkPosHash(this, par2, par3));
                crashreportcategory.addCrashSectionCallable("Structure type", new CallableStructureType(this));
                throw new ReportedException(crashreport);
            }
        }
    }

    /**
     * Generates structures in specified chunk next to existing structures. Does *not* generate StructureStarts.
     */
    public boolean generateStructuresInChunk(World par1World, Random par2Random, int par3, int par4)
    {
        this.func_143027_a(par1World);
        int k = (par3 << 4) + 8;
        int l = (par4 << 4) + 8;
        boolean flag = false;
        Iterator iterator = this.getStructureMap().values().iterator();

        while (iterator.hasNext())
        {
            StructureStart structurestart = (StructureStart)iterator.next();

            if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().intersectsWith(k, l, k + 15, l + 15))
            {
                structurestart.generateStructure(par1World, par2Random, new StructureBoundingBox(k, l, k + 15, l + 15));
                flag = true;
                this.func_143026_a(structurestart.func_143019_e(), structurestart.func_143018_f(), structurestart);
            }
        }

        return flag;
    }

    /**
     * Returns true if the structure generator has generated a structure located at the given position tuple.
     */
    public boolean hasStructureAt(int par1, int par2, int par3)
    {
        this.func_143027_a(this.worldObj);
        return this.func_143028_c(par1, par2, par3) != null;
    }

    protected StructureStart func_143028_c(int par1, int par2, int par3)
    {
        Iterator iterator = this.getStructureMap().values().iterator();

        while (iterator.hasNext())
        {
            StructureStart structurestart = (StructureStart)iterator.next();

            if (structurestart.isSizeableStructure() && structurestart.getBoundingBox().intersectsWith(par1, par3, par1, par3))
            {
                Iterator iterator1 = structurestart.getComponents().iterator();

                while (iterator1.hasNext())
                {
                    StructureComponent structurecomponent = (StructureComponent)iterator1.next();

                    if (structurecomponent.getBoundingBox().isVecInside(par1, par2, par3))
                    {
                        return structurestart;
                    }
                }
            }
        }

        return null;
    }

    public boolean func_142038_b(int par1, int par2, int par3)
    {
        this.func_143027_a(this.worldObj);
        Iterator iterator = this.getStructureMap().values().iterator();
        StructureStart structurestart;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            structurestart = (StructureStart)iterator.next();
        }
        while (!structurestart.isSizeableStructure());

        return structurestart.getBoundingBox().intersectsWith(par1, par3, par1, par3);
    }

    public ChunkPosition getNearestInstance(World par1World, int par2, int par3, int par4)
    {
        this.worldObj = par1World;
        this.func_143027_a(par1World);
        this.rand.setSeed(par1World.getSeed());
        long l = this.rand.nextLong();
        long i1 = this.rand.nextLong();
        long j1 = (long)(par2 >> 4) * l;
        long k1 = (long)(par4 >> 4) * i1;
        this.rand.setSeed(j1 ^ k1 ^ par1World.getSeed());
        this.recursiveGenerate(par1World, par2 >> 4, par4 >> 4, 0, 0, (byte[])null);
        double d0 = Double.MAX_VALUE;
        ChunkPosition chunkposition = null;
        Iterator iterator = this.getStructureMap().values().iterator();
        ChunkPosition chunkposition1;
        int l1;
        int i2;
        double d1;
        int j2;

        while (iterator.hasNext())
        {
            StructureStart structurestart = (StructureStart)iterator.next();

            if (structurestart.isSizeableStructure())
            {
                StructureComponent structurecomponent = (StructureComponent)structurestart.getComponents().get(0);
                chunkposition1 = structurecomponent.getCenter();
                i2 = chunkposition1.x - par2;
                l1 = chunkposition1.y - par3;
                j2 = chunkposition1.z - par4;
                d1 = (double)(i2 * i2 + l1 * l1 + j2 * j2);

                if (d1 < d0)
                {
                    d0 = d1;
                    chunkposition = chunkposition1;
                }
            }
        }

        if (chunkposition != null)
        {
            return chunkposition;
        }
        else
        {
            List list = this.getCoordList();

            if (list != null)
            {
                ChunkPosition chunkposition2 = null;
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext())
                {
                    chunkposition1 = (ChunkPosition)iterator1.next();
                    i2 = chunkposition1.x - par2;
                    l1 = chunkposition1.y - par3;
                    j2 = chunkposition1.z - par4;
                    d1 = (double)(i2 * i2 + l1 * l1 + j2 * j2);

                    if (d1 < d0)
                    {
                        d0 = d1;
                        chunkposition2 = chunkposition1;
                    }
                }

                return chunkposition2;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Returns a list of other locations at which the structure generation has been run, or null if not relevant to this
     * structure generator.
     */
    protected List getCoordList()
    {
        return null;
    }

    private void func_143027_a(World par1World)
    {
        if (this.field_143029_e == null)
        {
            this.field_143029_e = (MapGenStructureData)par1World.perWorldStorage.loadData(MapGenStructureData.class, this.func_143025_a());

            if (this.field_143029_e == null)
            {
                this.field_143029_e = new MapGenStructureData(this.func_143025_a());
                par1World.perWorldStorage.setData(this.func_143025_a(), this.field_143029_e);
            }
            else
            {
                NBTTagCompound nbttagcompound = this.field_143029_e.func_143041_a();
                Iterator iterator = nbttagcompound.getTags().iterator();

                while (iterator.hasNext())
                {
                    NBTBase nbtbase = (NBTBase)iterator.next();

                    if (nbtbase.getId() == 10)
                    {
                        NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbtbase;

                        if (nbttagcompound1.hasKey("ChunkX") && nbttagcompound1.hasKey("ChunkZ"))
                        {
                            int i = nbttagcompound1.getInteger("ChunkX");
                            int j = nbttagcompound1.getInteger("ChunkZ");
                            StructureStart structurestart = MapGenStructureIO.func_143035_a(nbttagcompound1, par1World);
                            this.getStructureMap().put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(i, j)), structurestart);
                        }
                    }
                }
            }
        }
    }

    private void func_143026_a(int par1, int par2, StructureStart par3StructureStart)
    {
        this.field_143029_e.func_143043_a(par3StructureStart.func_143021_a(par1, par2), par1, par2);
        this.field_143029_e.markDirty();
    }

    protected abstract boolean canSpawnStructureAtCoords(int i, int j);

    protected abstract StructureStart getStructureStart(int i, int j);

	public Map getStructureMap() {
		return structureMap;
	}

	public void setStructureMap(Map structureMap) {
		this.structureMap = structureMap;
	}
}
