package tictim.hearthstones.hearthstone;

import tictim.hearthstones.config.HearthstoneConfig;

public abstract class ConfigurableHearthstone implements Hearthstone{
	protected final HearthstoneConfig config;

	public ConfigurableHearthstone(HearthstoneConfig config){
		this.config = config;
	}

	@Override public int getMaxDamage(){
		return config.maxUse();
	}
}
