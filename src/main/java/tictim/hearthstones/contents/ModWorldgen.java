package tictim.hearthstones.contents;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class ModWorldgen{
	private ModWorldgen(){}

	private static PlacedFeature aquamarineOreFeature;

	public static void register(){
		List<OreConfiguration.TargetBlockState> aquamarineTargetList = List.of(
				OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.AQUAMARINE_ORE.get().defaultBlockState()),
				OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_AQUAMARINE_ORE.get().defaultBlockState()));

		ConfiguredFeature<OreConfiguration, ?> aquamarineOre = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(MODID, "aquamarine"),
				Feature.ORE.configured(new OreConfiguration(aquamarineTargetList, 5)));
		aquamarineOreFeature = Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(MODID, "aquamarine"),
				aquamarineOre.placed(List.of(
						CountPlacement.of(20),
						InSquarePlacement.spread(),
						HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(15), VerticalAnchor.absolute(50)),
						BiomeFilter.biome())));
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBiomeLoading(BiomeLoadingEvent event){
		if(event.getCategory()==Biome.BiomeCategory.NETHER||event.getCategory()==Biome.BiomeCategory.THEEND) return;

		if(aquamarineOreFeature==null) return;
		event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, aquamarineOreFeature);
	}
}
