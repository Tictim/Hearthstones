package tictim.hearthstones.logic;

import tictim.hearthstones.config.HearthstoneConfig;
import tictim.hearthstones.utils.HearthingContext;

public abstract class BaseHearthstone implements Hearthstone{
	private final HearthstoneConfig config;

	public BaseHearthstone(HearthstoneConfig config){
		this.config = config;
	}

	@Override public int getMaxDamage(){
		return config.maxUse();
	}
	@Override public int getCooldown(HearthingContext ctx){
		return config.cooldown();
	}
}
