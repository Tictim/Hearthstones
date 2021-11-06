package tictim.hearthstones.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ModCfg{
	private ModCfg(){}

	private static HearthstoneConfig hearthstone;
	private static HearthstoneConfig hearthingPlanks;
	private static HearthingGemConfig hearthingGem;
	private static HearthstoneConfig companionHearthstone;

	private static BooleanValue easyMode;

	private static BooleanValue traceHearthstoneUsage;
	private static BooleanValue traceTavernUpdate;

	private static boolean initCalled = false;
	public static void init(){
		if(initCalled) throw new IllegalStateException("ModCfg#init() called twice");
		else initCalled = true;

		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();

		hearthstone = new HearthstoneConfig(server, "hearthstone", "Hearthstone", 0, 75);
		hearthingPlanks = new HearthstoneConfig(server, "hearthingPlanks", "Hearthing Planks", 30, 75);
		hearthingGem = new HearthingGemConfig(server, "hearthingGem", "Hearthing Gem", 0, 150);
		companionHearthstone = new HearthstoneConfig(server, "companionHearthstone", "Companion Hearthstone", 0, 225);

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server.build());

		ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();

		easyMode = common.comment("Removes the recipe of Shabby Tavern/Hearthing Planks, and reverts the recipe of Tavern/Hearthstone to much cheaper version. Requires world restart.")
				.worldRestart()
				.define("easyMode", false);

		traceHearthstoneUsage = common.define("traceHearthstoneUsage", false);
		traceTavernUpdate = common.define("traceTavernUpdate", false);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, common.build());
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

	public static HearthstoneConfig hearthstone(){
		return hearthstone;
	}
	public static HearthstoneConfig hearthingPlanks(){
		return hearthingPlanks;
	}
	public static HearthingGemConfig hearthingGem(){
		return hearthingGem;
	}
	public static HearthstoneConfig companionHearthstone(){
		return companionHearthstone;
	}
}
