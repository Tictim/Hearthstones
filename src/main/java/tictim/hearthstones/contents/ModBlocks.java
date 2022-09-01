package tictim.hearthstones.contents;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import tictim.hearthstones.contents.block.AmethystClusterBlock;
import tictim.hearthstones.contents.block.AquamarineOreBlock;
import tictim.hearthstones.contents.block.BinderLecternBlock;
import tictim.hearthstones.contents.block.BuddingAmethystBlock;
import tictim.hearthstones.contents.block.SoundBlock;
import tictim.hearthstones.contents.block.TavernBlock;

import javax.annotation.Nonnull;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
@GameRegistry.ObjectHolder(MODID)
public class ModBlocks{
	@SuppressWarnings("ConstantConditions") @Nonnull private static <T> T definitelyNotNull(){
		return null;
	}

	public static final AquamarineOreBlock AQUAMARINE_ORE = definitelyNotNull();
	public static final Block AQUAMARINE_BLOCK = definitelyNotNull();

	public static final Block AMETHYST_BLOCK = definitelyNotNull();
	public static final Block BUDDING_AMETHYST = definitelyNotNull();
	public static final Block AMETHYST_CLUSTER = definitelyNotNull();
	public static final Block SMALL_AMETHYST_BUD = definitelyNotNull();
	public static final Block MEDIUM_AMETHYST_BUD = definitelyNotNull();
	public static final Block LARGE_AMETHYST_BUD = definitelyNotNull();

	public static final Block TAVERN = definitelyNotNull();
	public static final Block BINDER_LECTERN = definitelyNotNull();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		IForgeRegistry<Block> registry = event.getRegistry();
		register(registry, "aquamarine_ore", new AquamarineOreBlock().setHardness(3).setResistance(5));
		register(registry, "aquamarine_block", new SoundBlock(Material.IRON, SoundType.METAL)
				.setHardness(5).setResistance(10));

		register(registry, "amethyst_block", new SoundBlock(ModMaterials.AMETHYST, ModSoundTypes.AMETHYST_SOUND).setHardness(1.5f));
		register(registry, "budding_amethyst", new BuddingAmethystBlock().setHardness(1.5f));
		register(registry, "amethyst_cluster", new AmethystClusterBlock(ModSoundTypes.AMETHYST_CLUSTER_SOUND, true, 7/16.0, 3/16.0)
				.setLightLevel(5/15f));
		register(registry, "small_amethyst_bud", new AmethystClusterBlock(ModSoundTypes.SMALL_AMETHYST_BUD_SOUND, false, 5/16.0, 3/16.0)
				.setLightLevel(4/15f));
		register(registry, "medium_amethyst_bud", new AmethystClusterBlock(ModSoundTypes.MEDIUM_AMETHYST_BUD_SOUND, false, 4/16.0, 3/16.0)
				.setLightLevel(2/15f));
		register(registry, "large_amethyst_bud", new AmethystClusterBlock(ModSoundTypes.LARGE_AMETHYST_BUD_SOUND, false, 3/16.0, 4/16.0)
				.setLightLevel(1/15f));

		register(registry, "tavern", new TavernBlock());
		register(registry, "binder_lectern", new BinderLecternBlock());
	}

	private static void register(IForgeRegistry<Block> registry, String name, Block block){
		registry.register(block.setRegistryName(name)
				.setTranslationKey(MODID+"."+name)
				.setCreativeTab(HearthstoneTab.get()));
	}

	public static class ModMaterials{
		public static final Material AMETHYST = new Material(MapColor.PURPLE){{setRequiresTool();}};
		public static final Material AMETHYST_CRYSTAL = new Material(MapColor.PURPLE);
	}
}
