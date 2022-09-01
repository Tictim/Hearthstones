package tictim.hearthstones;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tictim.hearthstones.contents.ModOreDict;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;
import tictim.hearthstones.contents.tileentity.GlobalTavernTile;
import tictim.hearthstones.contents.tileentity.NormalTavernTile;
import tictim.hearthstones.contents.tileentity.ShabbyTavernTile;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.worldgen.HearthstonesWorldGenerator;

@Mod(modid = Hearthstones.MODID,
		name = Hearthstones.NAME,
		version = Hearthstones.VERSION,
		guiFactory = "tictim.hearthstones.config.GuiFactory")
public class Hearthstones{
	public static final String MODID = "hearthstones";
	public static final String NAME = "Hearthstones";
	public static final String VERSION = "1.0.1.0-SNAPSHOT";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	@Mod.Instance(MODID)
	public static Hearthstones instance;

	@SidedProxy(clientSide = "tictim.hearthstones.Proxy$Client", serverSide = "tictim.hearthstones.Proxy", modId = MODID)
	public static Proxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		ModNet.init();
		Caps.register();

		GameRegistry.registerTileEntity(NormalTavernTile.class, new ResourceLocation(MODID, "tavern"));
		GameRegistry.registerTileEntity(ShabbyTavernTile.class, new ResourceLocation(MODID, "tavern_shabby"));
		GameRegistry.registerTileEntity(GlobalTavernTile.class, new ResourceLocation(MODID, "tavern_global"));
		GameRegistry.registerTileEntity(BinderLecternTile.class, new ResourceLocation(MODID, "binder_lectern"));
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		ModOreDict.register();
		GameRegistry.registerWorldGenerator(new HearthstonesWorldGenerator(), 0);
		proxy.registerRenderer();
	}
}