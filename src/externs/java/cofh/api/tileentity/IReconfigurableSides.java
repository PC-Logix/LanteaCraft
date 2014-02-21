
package cofh.api.tileentity;

public interface IReconfigurableSides {

    public boolean decrSide(int side);

    public boolean incrSide(int side);

    public boolean setSide(int side, int setting);

    public boolean resetSides();

    public int getNumSides();

}
