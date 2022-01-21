package tictim.hearthstones.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.ModBlocks;

import static net.minecraftforge.common.Tags.Blocks.ORES;
import static net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS;
import static tictim.hearthstones.contents.ModTags.Blocks.*;

public class BlockTagGen extends BlockTagsProvider{
	public BlockTagGen(DataGenerator generatorIn, ExistingFileHelper existingFileHelper){
		super(generatorIn, Hearthstones.MODID, existingFileHelper);
	}

	@Override protected void addTags(){
		tag(STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_AQUAMARINE);
		tag(STORAGE_BLOCKS_AQUAMARINE).add(ModBlocks.AQUAMARINE_BLOCK.get());
		tag(ORES).addTag(ORES_AQUAMARINE);
		tag(ORES_AQUAMARINE).add(ModBlocks.AQUAMARINE_ORE.get(), ModBlocks.DEEPSLATE_AQUAMARINE_ORE.get());
		tag(TAVERNS).add(ModBlocks.TAVERN.get(), ModBlocks.SHABBY_TAVERN.get(), ModBlocks.GLOBAL_TAVERN.get());

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.AQUAMARINE_ORE.get(), ModBlocks.AQUAMARINE_BLOCK.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.AQUAMARINE_ORE.get(), ModBlocks.AQUAMARINE_BLOCK.get());
	}
}
