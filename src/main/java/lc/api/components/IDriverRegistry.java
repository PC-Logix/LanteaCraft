package lc.api.components;

import lc.api.drivers.IPowerDriver;

public interface IDriverRegistry {
	
	public void registerDriver(IPowerDriver driver);

}
