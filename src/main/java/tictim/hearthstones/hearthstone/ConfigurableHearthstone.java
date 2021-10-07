package tictim.hearthstones.hearthstone;

import tictim.hearthstones.config.HearthstoneConfig;
import tictim.hearthstones.hearthstone.Hearthstone;

public abstract class ConfigurableHearthstone implements Hearthstone{
	protected final HearthstoneConfig config;

	public ConfigurableHearthstone(HearthstoneConfig config){
		this.config = config;
	}

	@Override public int getMaxDamage(){
		return config.maxUse();
	}
}
