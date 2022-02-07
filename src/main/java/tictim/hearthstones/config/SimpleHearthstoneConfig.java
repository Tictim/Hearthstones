package tictim.hearthstones.config;

import net.minecraftforge.common.config.Config;

public class SimpleHearthstoneConfig implements HearthstoneConfig{
	@Config.Comment("Number of times the hearthstone can be used. Set to 0 if you don't want it to wear off.")
	@Config.RangeInt(min = 0)
	public int maxUse;
	@Config.Comment("Cooldown of the hearthstone, in seconds.")
	@Config.RangeInt(min = 0)
	public int cooldown;

	public SimpleHearthstoneConfig(int maxUse, int cooldown){
		this.maxUse = maxUse;
		this.cooldown = cooldown;
	}
	@Override public int maxUse(){
		return maxUse;
	}
	@Override public int cooldown(){
		return cooldown;
	}
}
