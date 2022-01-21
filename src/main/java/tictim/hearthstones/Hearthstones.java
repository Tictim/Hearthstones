package tictim.hearthstones;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
import tictim.hearthstones.contents.ModWorldgen;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;
import tictim.hearthstones.datagen.BlockTagGen;
import tictim.hearthstones.datagen.ItemTagGen;
import tictim.hearthstones.datagen.LootTableGen;
import tictim.hearthstones.datagen.RecipeGen;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.tavern.TavernMemories;

import java.util.List;

@Mod(Hearthstones.MODID)
@Mod.EventBusSubscriber(modid = Hearthstones.MODID, bus = Bus.MOD)
public class Hearthstones{
	public static final String MODID = "hearthstones";
	public static final Logger LOGGER = LogManager.getLogger("Hearthstones");

	public Hearthstones(){
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModCfg.init();
		ModNet.init();

		ModBlocks.REGISTER.register(eventBus);
		ModItems.REGISTER.register(eventBus);
		ModEnchantments.REGISTER.register(eventBus);
		ModBlockEntities.REGISTER.register(eventBus);
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event){
		event.enqueueWork(ModWorldgen::register);
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event){
		event.register(TavernMemories.class);
		event.register(HearthstoneItem.Data.class);
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
				@SuppressWarnings("deprecation")
				ItemPropertyFunction itemPropertyGetter = (s, w, e, wtf) -> {
					if(e instanceof Player){
						HearthstoneItem.Data data = HearthstoneItem.data(s);
						if(data!=null&&data.hasCooldown) return 1;
					}
					return 0;
				};

				ItemProperties.register(ModItems.HEARTHSTONE.get(), key, itemPropertyGetter);
				ItemProperties.register(ModItems.HEARTHING_PLANKS.get(), key, itemPropertyGetter);
				ItemProperties.register(ModItems.HEARTHING_GEM.get(), key, itemPropertyGetter);
				ItemProperties.register(ModItems.COMPANION_HEARTHSTONE.get(), key, itemPropertyGetter);
			});
		}
	}
}
