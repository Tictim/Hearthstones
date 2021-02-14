package tictim.hearthstones;

import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
		Registry.register(
				WorldGenRegistries.CONFIGURED_FEATURE,
				new ResourceLocation(MODID, "aquamarine"),
				Feature.ORE.withConfiguration(
						new OreFeatureConfig(
								OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
								ModBlocks.AQUAMARINE_ORE.get().getDefaultState(),
								5))
						.withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(0, 0, 50))
								.square() // TODO what does this shit do
								.func_242731_b(12)));

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
			BlockTagGen blockTagGen = new BlockTagGen(gen, event.getExistingFileHelper());
			gen.addProvider(blockTagGen);
			gen.addProvider(new ItemTagGen(gen, blockTagGen, event.getExistingFileHelper()));
			gen.addProvider(new LootTableGen(gen));
		}
	}


	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD, value = Dist.CLIENT)
	public static final class Client{
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event){
			ResourceLocation key = new ResourceLocation("has_cooldown");
			IItemPropertyGetter itemPropertyGetter = (s, w, e) -> {
				if(e instanceof PlayerEntity){
					CompoundNBT tag = s.getTag();
					return tag!=null&&tag.getBoolean("hasCooldown") ? 1 : 0;
				}else return 0;
			};

			ItemModelsProperties.registerProperty(ModItems.HEARTHSTONE.get(), key, itemPropertyGetter);
			ItemModelsProperties.registerProperty(ModItems.HEARTHING_PLANKS.get(), key, itemPropertyGetter);
			ItemModelsProperties.registerProperty(ModItems.HEARTHING_GEM.get(), key, itemPropertyGetter);
			ItemModelsProperties.registerProperty(ModItems.COMPANION_HEARTHSTONE.get(), key, itemPropertyGetter);
		}
	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static final class ForgeEventHandler{
		@SubscribeEvent
		public static void registerCommands(RegisterCommandsEvent event){
			ModCommands.init(event.getDispatcher());
		}

		@SubscribeEvent(priority = EventPriority.LOW)
		public static void loadBiome(BiomeLoadingEvent event){
			if(event.getCategory()==Biome.Category.NETHER||event.getCategory()==Biome.Category.THEEND) return;

			ConfiguredFeature<?, ?> aquamarine = WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(new ResourceLocation(MODID, "aquamarine"));
			if(aquamarine!=null){
				event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, aquamarine);
			}
		}
	}
}
