package tictim.hearthstones.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
@Config(modid = MODID, category = "")
public class ModCfg{
	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.getModID().equals(MODID))
			ConfigManager.sync(MODID, Config.Type.INSTANCE);
	}

	public static final General general = new General();
	public static final Hearthstones hearthstones = new Hearthstones();
	public static final Worldgen worldgen = new Worldgen();

	public static boolean easyMode(){
		return general.easyMode;
	}
	public static boolean traceHearthstoneUsage(){
		return general.debug.traceHearthstoneUsage;
	}
	public static boolean traceTavernUpdate(){
		return general.debug.traceTavernUpdate;
	}
	public static boolean logModelWarnings(){
		return general.debug.logModelWarnings;
	}

	public static final class General{
		@Config.Comment("Removes the recipe of Shabby Tavern/Hearthing Planks, and reverts the recipe of Tavern/Hearthstone to much cheaper version.")
		@Config.RequiresMcRestart
		public boolean easyMode;

		public final Debug debug = new Debug();

		public static final class Debug{
			public boolean traceHearthstoneUsage;
			public boolean traceTavernUpdate;
			public boolean logModelWarnings;
		}
	}

	public static final class Hearthstones{
		public final SimpleHearthstoneConfig hearthstone = new SimpleHearthstoneConfig(0, 75);
		public final SimpleHearthstoneConfig hearthingPlanks = new SimpleHearthstoneConfig(30, 75);
		public final HearthingGemConfig hearthingGem = new HearthingGemConfig(0, 150, 500);
		public final SimpleHearthstoneConfig companionHearthstone = new SimpleHearthstoneConfig(0, 225);
	}

	public static final class Worldgen{
		public final WorldgenConfig aquamarine = new WorldgenConfig(5, 50, 3, 12);
		public final WorldgenConfig amethyst = new WorldgenConfig(3, 20, 7, 3);
	}
}
