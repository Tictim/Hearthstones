package tictim.hearthstones.contents;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tictim.hearthstones.contents.block.BinderLecternBlock;
import tictim.hearthstones.contents.block.GlobalTavernBlock;
import tictim.hearthstones.contents.block.NormalTavernBlock;
import tictim.hearthstones.contents.block.ShabbyTavernBlock;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModBlocks{
	private ModBlocks(){}

	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final RegistryObject<Block> AQUAMARINE_ORE = REGISTER.register("aquamarine_ore", () -> new DropExperienceBlock(Properties.copy(Blocks.IRON_ORE), UniformInt.of(3, 7)));
	public static final RegistryObject<Block> DEEPSLATE_AQUAMARINE_ORE = REGISTER.register("deepslate_aquamarine_ore", () -> new DropExperienceBlock(Properties.copy(Blocks.DEEPSLATE_IRON_ORE), UniformInt.of(3, 7)));
	public static final RegistryObject<Block> AQUAMARINE_BLOCK = REGISTER.register("aquamarine_block", () -> new Block(Properties.of(Material.METAL).strength(5, 10).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> TAVERN = REGISTER.register("tavern", NormalTavernBlock::new);
	public static final RegistryObject<Block> SHABBY_TAVERN = REGISTER.register("shabby_tavern", ShabbyTavernBlock::new);
	public static final RegistryObject<Block> GLOBAL_TAVERN = REGISTER.register("global_tavern", GlobalTavernBlock::new);
	public static final RegistryObject<Block> BINDER_LECTERN = REGISTER.register("binder_lectern", () -> new BinderLecternBlock(Properties.copy(Blocks.LECTERN)));
}
