package tictim.hearthstones.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
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
		tag(ORES_AQUAMARINE).add(ModBlocks.AQUAMARINE_ORE.get());
		tag(TAVERNS).add(ModBlocks.TAVERN.get(), ModBlocks.SHABBY_TAVERN.get(), ModBlocks.GLOBAL_TAVERN.get());
	}
}
