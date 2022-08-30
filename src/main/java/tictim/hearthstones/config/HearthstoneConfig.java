package tictim.hearthstones.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import javax.annotation.Nullable;

public class HearthstoneConfig{
	private final IntValue maxUse;
	private final IntValue cooldown;

	@Nullable private ForgeConfigSpec spec;

	public HearthstoneConfig(ForgeConfigSpec.Builder b, String configName, String readableName, int defaultMaxUse, int defaultCooldown){
		b.push(configName);
		maxUse = b.comment("Number of times the "+readableName+" can be used. Set to 0 if you don't want it to wear off.")
				.defineInRange("maxUse", defaultMaxUse, 0, Integer.MAX_VALUE);
		cooldown = b.comment("Cooldown of the "+readableName+", in seconds.")
				.defineInRange("cooldown", defaultCooldown, 0, Integer.MAX_VALUE);
		b.pop();
	}

	public void setSpec(ForgeConfigSpec spec){
		if(this.spec!=null) throw new IllegalStateException("Spec already defined");
		this.spec = spec;
	}

	public int maxUse(){
		return getOrDefault(maxUse);
	}
	public int cooldown(){
		return getOrDefault(cooldown);
	}

	protected final <T> T getOrDefault(ForgeConfigSpec.ConfigValue<T> val){
		return spec==null||spec.isLoaded() ? val.get() : val.getDefault();
	}
}
