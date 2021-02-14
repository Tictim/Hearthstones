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

	private static final BooleanValue traceHearthstoneUsage;
	private static final BooleanValue traceTavernUpdate;

	private static final ForgeConfigSpec spec;

	static{
		ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
		hearthstone = new HearthstoneConfig(b, "hearthstone", "Hearthstone", 0, 75);
		hearthingPlanks = new HearthstoneConfig(b, "hearthingPlanks", "Hearthing Planks", 30, 75);
		hearthingGem = new HearthingGemConfig(b, "hearthingGem", "Hearthing Gem", 0, 150);
		companionHearthstone = new HearthstoneConfig(b, "companionHearthstone", "Companion Hearthstone", 0, 225);

		easyMode = b.comment("Removes the recipe of Shabby Tavern/Hearthing Planks, and reverts the recipe of Tavern/Hearthstone to much cheaper version.")
				.worldRestart()
				.define("easyMode", false);

		traceHearthstoneUsage = b.define("traceHearthstoneUsage", false);
		traceTavernUpdate = b.define("traceTavernUpdate", false);

		spec = b.build();
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
	public static boolean traceHearthstoneUsage(){
		return traceHearthstoneUsage.get();
	}
	public static boolean traceTavernUpdate(){
		return traceTavernUpdate.get();
	}
}
