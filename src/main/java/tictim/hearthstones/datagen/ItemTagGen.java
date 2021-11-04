package tictim.hearthstones.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.ModTags;

public class ItemTagGen extends ItemTagsProvider{
	public ItemTagGen(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper){
		super(generatorIn, blockTagProvider, Hearthstones.MODID, existingFileHelper);
	}

	@Override protected void addTags(){
		copy(ModTags.Blocks.TAVERNS, ModTags.TAVERNS);
		copy(ModTags.Blocks.STORAGE_BLOCKS_AQUAMARINE, ModTags.STORAGE_BLOCKS_AQUAMARINE);
		copy(ModTags.Blocks.ORES_AQUAMARINE, ModTags.ORES_AQUAMARINE);

		//noinspection unchecked
		tag(Tags.Items.DUSTS).addTags(ModTags.DUSTS_AQUAMARINE, ModTags.DUSTS_DEEP_BLUE, ModTags.DUSTS_DIAMOND, ModTags.DUSTS_LAPIS);
		tag(ModTags.DUSTS_AQUAMARINE).add(ModItems.AQUAMARINE_DUST.get());
		tag(ModTags.DUSTS_DEEP_BLUE).add(ModItems.DEEP_BLUE.get());
		tag(ModTags.DUSTS_DIAMOND).add(ModItems.DIAMOND_DUST.get());
		tag(ModTags.DUSTS_LAPIS).add(ModItems.LAPIS_DUST.get());

		tag(Tags.Items.GEMS).addTag(ModTags.GEMS_AQUAMARINE);
		tag(ModTags.GEMS_AQUAMARINE).add(ModItems.AQUAMARINE.get());

		tag(ModTags.HEARTHSTONE_MATERIAL).add(Items.GRANITE, Items.DIORITE, Items.ANDESITE);
	}
}
