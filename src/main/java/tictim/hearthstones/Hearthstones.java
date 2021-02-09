package tictim.hearthstones;

import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModEnchantments;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.ModTileEntities;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.datagen.BlockTagGen;
import tictim.hearthstones.datagen.ItemTagGen;
import tictim.hearthstones.datagen.LootTableGen;
import tictim.hearthstones.datagen.RecipeGen;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.proxy.ClientProxy;
import tictim.hearthstones.proxy.IProxy;
import tictim.hearthstones.proxy.ServerProxy;

import javax.annotation.Nullable;

@Mod(Hearthstones.MODID)
@Mod.EventBusSubscriber(modid = Hearthstones.MODID, bus = Bus.MOD)
public class Hearthstones{
	public static final String MODID = "hearthstones";
	public static final Logger LOGGER = LogManager.getLogger("Hearthstones");

	public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public Hearthstones(){
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModCfg.init();

		ModBlocks.BLOCKS.register(eventBus);
		ModItems.ITEMS.register(eventBus);
		ModEnchantments.ENCHANTMENTS.register(eventBus);
		ModTileEntities.TILE_ENTITIES.register(eventBus);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event){
		for(Biome b : ForgeRegistries.BIOMES){
			OreFeatureConfig c = new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ModBlocks.AQUAMARINE_ORE.get().getDefaultState(), 5);
			ConfiguredFeature<?, ?> v = Feature.ORE.withConfiguration(c)
					.withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(12, 0, 0, 50)));
			b.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, v);
		}
		Hearthstones.LOGGER.info("Injected aquamarine oregen feature in {} biomes", ForgeRegistries.BIOMES.getEntries().size());

		CapabilityManager.INSTANCE.register(PlayerTavernMemory.class, new Capability.IStorage<PlayerTavernMemory>(){
			@Nullable @Override public INBT writeNBT(Capability<PlayerTavernMemory> capability, PlayerTavernMemory instance, Direction side){
				return null;
			}
			@Override public void readNBT(Capability<PlayerTavernMemory> capability, PlayerTavernMemory instance, Direction side, INBT nbt){}
		}, () -> {
			throw new UnsupportedOperationException();
		});

		ModNet.init();
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event){
		DataGenerator gen = event.getGenerator();
		if(event.includeServer()){
			gen.addProvider(new RecipeGen(gen));
			gen.addProvider(new BlockTagGen(gen));
			gen.addProvider(new ItemTagGen(gen));
			gen.addProvider(new LootTableGen(gen));
		}
	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static final class ServerEventHandler{
		@SubscribeEvent
		public static void onServerStarting(FMLServerStartingEvent event){
			ModCommands.init(event.getCommandDispatcher());
		}
	}
}
