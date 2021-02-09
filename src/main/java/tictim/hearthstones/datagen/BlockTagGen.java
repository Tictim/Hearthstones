package tictim.hearthstones.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import tictim.hearthstones.contents.ModBlocks;

import static net.minecraftforge.common.Tags.Blocks.ORES;
import static net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS;
import static tictim.hearthstones.contents.ModTags.Blocks.*;

public class BlockTagGen extends BlockTagsProvider{
	public BlockTagGen(DataGenerator generatorIn){
		super(generatorIn);
	}

	@Override protected void registerTags(){
		getBuilder(STORAGE_BLOCKS).add(STORAGE_BLOCKS_AQUAMARINE);
		getBuilder(STORAGE_BLOCKS_AQUAMARINE).add(ModBlocks.AQUAMARINE_BLOCK.get());
		getBuilder(ORES).add(ORES_AQUAMARINE);
		getBuilder(ORES_AQUAMARINE).add(ModBlocks.AQUAMARINE_ORE.get());
		getBuilder(TAVERNS).add(ModBlocks.TAVERN.get(), ModBlocks.SHABBY_TAVERN.get(), ModBlocks.GLOBAL_TAVERN.get());
	}
}
