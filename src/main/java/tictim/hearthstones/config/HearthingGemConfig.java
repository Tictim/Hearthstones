package tictim.hearthstones.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class HearthingGemConfig extends HearthstoneConfig{
	private final DoubleValue travelDistanceThreshold;

	public HearthingGemConfig(ForgeConfigSpec.Builder b, String configName, String readableName, int defaultMaxUse, int defaultCooldown){
		super(b, configName, readableName, defaultMaxUse, defaultCooldown);
		b.push(configName);
		travelDistanceThreshold = b.comment("Maximum distance the Hearthing Gem can be used without breaking it.")
				.defineInRange("travelDistanceThreshold", 500, 0, Double.POSITIVE_INFINITY);
		b.pop();
	}

	public double travelDistanceThreshold(){
		return travelDistanceThreshold.get();
	}
}
