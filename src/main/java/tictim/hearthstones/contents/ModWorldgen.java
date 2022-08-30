package tictim.hearthstones.contents;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class ModWorldgen{
	private ModWorldgen(){}

	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, MODID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MODID);

	public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_AQUAMARINE = CONFIGURED_FEATURES.register("aquamarine",
			() -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
					OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.AQUAMARINE_ORE.get().defaultBlockState()),
					OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_AQUAMARINE_ORE.get().defaultBlockState())), 5)
			));
	public static final RegistryObject<PlacedFeature> AQUAMARINE = PLACED_FEATURES.register("aquamarine", () -> new PlacedFeature(
			CONFIGURED_AQUAMARINE.getHolder().orElseThrow(), List.of(
			CountPlacement.of(20),
			InSquarePlacement.spread(),
			HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(15), VerticalAnchor.absolute(50)),
			BiomeFilter.biome()
	)));
}
