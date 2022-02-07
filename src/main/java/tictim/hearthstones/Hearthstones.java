package tictim.hearthstones;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModOreDict;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;
import tictim.hearthstones.contents.tileentity.GlobalTavernTile;
import tictim.hearthstones.contents.tileentity.NormalTavernTile;
import tictim.hearthstones.contents.tileentity.ShabbyTavernTile;
import tictim.hearthstones.net.ModNet;

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

		WorldGenMinable worldGen = new WorldGenMinable(ModBlocks.AQUAMARINE_ORE.getDefaultState(), ModCfg.aquamarineOreSize);
		GameRegistry.registerWorldGenerator((random, chunkX, chunkZ, world, chunkGenerator, chunkProvider) -> {
			if(ModCfg.aquamarineGen&&TerrainGen.generateOre(world, random, worldGen, new BlockPos(chunkX, 0, chunkZ), OreGenEvent.GenerateMinable.EventType.CUSTOM)){
				for(int i = 0, j = ModCfg.aquamarineCountInChunk; i<j; i++){
					BlockPos blockpos = new BlockPos(chunkX*16+random.nextInt(16), random.nextInt(ModCfg.aquamarineMaxY-ModCfg.aquamarineMinY)+ModCfg.aquamarineMinY, chunkZ*16+random.nextInt(16));
					worldGen.generate(world, random, blockpos);
				}
			}
		}, 0);
		proxy.registerRenderer();
	}
}