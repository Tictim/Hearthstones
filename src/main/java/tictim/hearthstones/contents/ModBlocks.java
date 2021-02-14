package tictim.hearthstones.contents;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tictim.hearthstones.block.AquamarineOreBlock;
import tictim.hearthstones.block.GlobalTavernBlock;
import tictim.hearthstones.block.NormalTavernBlock;
import tictim.hearthstones.block.ShabbyTavernBlock;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModBlocks{
	private ModBlocks(){}

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final RegistryObject<Block> AQUAMARINE_ORE = BLOCKS.register("aquamarine_ore", AquamarineOreBlock::new);
	public static final RegistryObject<Block> AQUAMARINE_BLOCK = BLOCKS.register("aquamarine_block", () -> new Block(Properties.create(Material.IRON).hardnessAndResistance(5, 10)));
	public static final RegistryObject<Block> TAVERN = BLOCKS.register("tavern", () -> new NormalTavernBlock());
	public static final RegistryObject<Block> SHABBY_TAVERN = BLOCKS.register("shabby_tavern", () -> new ShabbyTavernBlock());
	public static final RegistryObject<Block> GLOBAL_TAVERN = BLOCKS.register("global_tavern", () -> new GlobalTavernBlock());
}
