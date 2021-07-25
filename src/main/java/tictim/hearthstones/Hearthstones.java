package tictim.hearthstones;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModEnchantments;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.datagen.BlockTagGen;
import tictim.hearthstones.datagen.ItemTagGen;
import tictim.hearthstones.datagen.LootTableGen;
import tictim.hearthstones.datagen.RecipeGen;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.proxy.ClientProxy;
import tictim.hearthstones.proxy.IProxy;
import tictim.hearthstones.proxy.ServerProxy;

@Mod(Hearthstones.MODID)
@Mod.EventBusSubscriber(modid = Hearthstones.MODID, bus = Bus.MOD)
public class Hearthstones{
	public static final String MODID = "hearthstones";
	public static final Logger LOGGER = LogManager.getLogger("Hearthstones");

	public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public Hearthstones(){
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModCfg.init();

		ModBlocks.REGISTER.register(eventBus);
		ModItems.REGISTER.register(eventBus);
		ModEnchantments.REGISTER.register(eventBus);
		ModBlockEntities.REGISTER.register(eventBus);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			Registry.register(
					BuiltinRegistries.CONFIGURED_FEATURE,
					new ResourceLocation(MODID, "aquamarine"),
					Feature.ORE.configured(new OreConfiguration(
									OreConfiguration.Predicates.NATURAL_STONE,
									ModBlocks.AQUAMARINE_ORE.get().defaultBlockState(),
									5))
							.rangeUniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(50))
							.squared()
							.count(12));
			ModNet.init();
		});
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event){
		event.register(PlayerTavernMemory.class);
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
			event.enqueueWork(() -> {
				ResourceLocation key = new ResourceLocation("has_cooldown");
				//noinspection deprecation
				ItemPropertyFunction itemPropertyGetter = (s, w, e, wtf) -> { // TODO wtf
					if(e instanceof Player){
						CompoundTag tag = s.getTag();
						return tag!=null&&tag.getBoolean("hasCooldown") ? 1 : 0;
					}else return 0;
				};

				ItemProperties.register(ModItems.HEARTHSTONE.get(), key, itemPropertyGetter);
				ItemProperties.register(ModItems.HEARTHING_PLANKS.get(), key, itemPropertyGetter);
				ItemProperties.register(ModItems.HEARTHING_GEM.get(), key, itemPropertyGetter);
				ItemProperties.register(ModItems.COMPANION_HEARTHSTONE.get(), key, itemPropertyGetter);
			});
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
			if(event.getCategory()==Biome.BiomeCategory.NETHER||event.getCategory()==Biome.BiomeCategory.THEEND) return;

			ConfiguredFeature<?, ?> aquamarine = BuiltinRegistries.CONFIGURED_FEATURE.get(new ResourceLocation(MODID, "aquamarine"));
			if(aquamarine!=null){
				event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, aquamarine);
			}
		}
	}
}
