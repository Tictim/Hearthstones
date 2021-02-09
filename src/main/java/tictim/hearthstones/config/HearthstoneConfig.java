package tictim.hearthstones.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class HearthstoneConfig{
	private final IntValue maxUse;
	private final IntValue cooldown;

	public HearthstoneConfig(ForgeConfigSpec.Builder b, String configName, String readableName, int defaultMaxUse, int defaultCooldown){
		b.push(configName);
		maxUse = b.comment("Number of times the "+readableName+" can be used. Set to 0 if you don't want it to wear off.")
				.defineInRange("maxUse", defaultMaxUse, 0, Integer.MAX_VALUE);
		cooldown = b.comment("Cooldown of the "+readableName+", in seconds.")
				.defineInRange("cooldown", defaultCooldown, 0, Integer.MAX_VALUE);
		b.pop();
	}

	public int maxUse(){
		return maxUse.get();
	}
	public int cooldown(){
		return cooldown.get();
	}
}
