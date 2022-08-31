package tictim.hearthstones.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
@Config(modid = MODID, name = "ttmp/hearthstones", category = "master")
public class ModCfg{
	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.getModID().equals(MODID))
			ConfigManager.sync(MODID, Config.Type.INSTANCE);
	}

	public static final SimpleHearthstoneConfig hearthstone = new SimpleHearthstoneConfig(0, 75);
	public static final SimpleHearthstoneConfig hearthingPlanks = new SimpleHearthstoneConfig(30, 75);
	public static final HearthingGemConfig hearthingGem = new HearthingGemConfig(0, 150, 500);
	public static final SimpleHearthstoneConfig companionHearthstone = new SimpleHearthstoneConfig(0, 225);

	@Config.Comment("Removes the recipe of Shabby Tavern/Hearthing Planks, and reverts the recipe of Tavern/Hearthstone to much cheaper version.")
	@Config.RequiresMcRestart
	public static boolean easyMode;

	public static boolean traceHearthstoneUsage;
	public static boolean traceTavernUpdate;
	public static boolean logModelWarnings;

	public static boolean aquamarineGen = true;
	public static int aquamarineMinY = 5;
	public static int aquamarineMaxY = 50;
	@Config.RangeInt(min = 0)
	public static int aquamarineOreSize = 3;
	@Config.RangeInt(min = 0)
	public static int aquamarineCountInChunk = 12;
}
