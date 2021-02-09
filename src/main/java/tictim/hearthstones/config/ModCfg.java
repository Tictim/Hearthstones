package tictim.hearthstones.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ModCfg{
	private ModCfg(){}

	public static final HearthstoneConfig hearthstone;
	public static final HearthstoneConfig hearthingPlanks;
	public static final HearthingGemConfig hearthingGem;
	public static final HearthstoneConfig companionHearthstone;

	private static final BooleanValue easyMode;

	private static final ForgeConfigSpec spec;

	static{
		ForgeConfigSpec.Builder commonConfigSpecBuilder = new ForgeConfigSpec.Builder();
		hearthstone = new HearthstoneConfig(commonConfigSpecBuilder, "hearthstone", "Hearthstone", 0, 75);
		hearthingPlanks = new HearthstoneConfig(commonConfigSpecBuilder, "hearthingPlanks", "Hearthing Planks", 30, 75);
		hearthingGem = new HearthingGemConfig(commonConfigSpecBuilder, "hearthingGem", "Hearthing Gem", 0, 150);
		companionHearthstone = new HearthstoneConfig(commonConfigSpecBuilder, "companionHearthstone", "Companion Hearthstone", 0, 225);

		easyMode = commonConfigSpecBuilder.comment("Removes the recipe of Shabby Tavern/Hearthing Planks, and reverts the recipe of Tavern/Hearthstone to much cheaper version.")
				.worldRestart()
				.define("easyMode", false);
		spec = commonConfigSpecBuilder.build();
	}

	private static boolean initCalled = false;
	public static void init(){
		if(initCalled) throw new IllegalStateException("ModCfg#init() called twice");
		else initCalled = true;

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, spec, "hearthstones.toml");
	}

	public static boolean easyMode(){
		return easyMode.get();
	}
}
