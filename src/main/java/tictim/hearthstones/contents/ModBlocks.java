package tictim.hearthstones.contents;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tictim.hearthstones.block.AquamarineOreBlock;
import tictim.hearthstones.block.GlobalTavernBlock;
import tictim.hearthstones.block.NormalTavernBlock;
import tictim.hearthstones.block.ShabbyTavernBlock;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModBlocks{
	private ModBlocks(){}

	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final RegistryObject<Block> AQUAMARINE_ORE = REGISTER.register("aquamarine_ore", () -> new AquamarineOreBlock(Properties.of(Material.STONE).strength(3, 5).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> AQUAMARINE_BLOCK = REGISTER.register("aquamarine_block", () -> new Block(Properties.of(Material.METAL).strength(5, 10).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> TAVERN = REGISTER.register("tavern", NormalTavernBlock::new);
	public static final RegistryObject<Block> SHABBY_TAVERN = REGISTER.register("shabby_tavern", ShabbyTavernBlock::new);
	public static final RegistryObject<Block> GLOBAL_TAVERN = REGISTER.register("global_tavern", GlobalTavernBlock::new);
}
