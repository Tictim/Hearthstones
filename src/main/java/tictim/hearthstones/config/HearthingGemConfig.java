package tictim.hearthstones.config;

import net.minecraftforge.common.config.Config;

public class HearthingGemConfig implements HearthstoneConfig{
	@Config.Comment("Number of times the hearthstone can be used. Set to 0 if you don't want it to wear off.")
	@Config.RangeInt(min = 0)
	public int maxUse;
	@Config.Comment("Cooldown of the hearthstone, in seconds.")
	@Config.RangeInt(min = 0)
	public int cooldown;
	@Config.Comment("Maximum distance the Hearthing Gem can be used without breaking it.")
	@Config.RangeDouble(min = 0, max = Double.POSITIVE_INFINITY)
	public int travelDistanceThreshold;

	public HearthingGemConfig(int maxUse, int cooldown, int travelDistanceThreshold){
		this.maxUse = maxUse;
		this.cooldown = cooldown;
		this.travelDistanceThreshold = travelDistanceThreshold;
	}

	@Override public int maxUse(){
		return maxUse;
	}
	@Override public int cooldown(){
		return cooldown;
	}
	public int travelDistanceThreshold(){
		return travelDistanceThreshold;
	}
}
