package tictim.hearthstones.contents;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import tictim.hearthstones.contents.block.AquamarineOreBlock;
import tictim.hearthstones.contents.block.BinderLecternBlock;
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
	public static final Block TAVERN = definitelyNotNull();
	public static final Block BINDER_LECTERN = definitelyNotNull();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		IForgeRegistry<Block> registry = event.getRegistry();
		register(registry, "aquamarine_ore", new AquamarineOreBlock());
		register(registry, "aquamarine_block", new Block(Material.IRON){{setSoundType(SoundType.METAL);}}
				.setHardness(5)
				.setResistance(10));
		register(registry, "tavern", new TavernBlock());
		register(registry, "binder_lectern", new BinderLecternBlock());
	}

	private static void register(IForgeRegistry<Block> registry, String name, Block block){
		registry.register(block.setRegistryName(name)
				.setTranslationKey(MODID+"."+name)
				.setCreativeTab(HearthstoneTab.get()));
	}
}
