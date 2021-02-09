package tictim.hearthstones.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.ModTags;

public class ItemTagGen extends ItemTagsProvider{
	public ItemTagGen(DataGenerator generatorIn){
		super(generatorIn);
	}

	@Override protected void registerTags(){
		copy(ModTags.Blocks.TAVERNS, ModTags.TAVERNS);
		copy(ModTags.Blocks.STORAGE_BLOCKS_AQUAMARINE, ModTags.STORAGE_BLOCKS_AQUAMARINE);
		copy(ModTags.Blocks.ORES_AQUAMARINE, ModTags.ORES_AQUAMARINE);

		getBuilder(Tags.Items.DUSTS).add(ModTags.DUSTS_AQUAMARINE, ModTags.DUSTS_DEEP_BLUE, ModTags.DUSTS_DIAMOND, ModTags.DUSTS_LAPIS);
		getBuilder(ModTags.DUSTS_AQUAMARINE).add(ModItems.AQUAMARINE_DUST.get());
		getBuilder(ModTags.DUSTS_DEEP_BLUE).add(ModItems.DEEP_BLUE.get());
		getBuilder(ModTags.DUSTS_DIAMOND).add(ModItems.DIAMOND_DUST.get());
		getBuilder(ModTags.DUSTS_LAPIS).add(ModItems.LAPIS_DUST.get());

		getBuilder(Tags.Items.GEMS).add(ModTags.GEMS_AQUAMARINE);
		getBuilder(ModTags.GEMS_AQUAMARINE).add(ModItems.AQUAMARINE.get());

		getBuilder(ModTags.HEARTHSTONE_MATERIAL).add(Items.GRANITE, Items.DIORITE, Items.ANDESITE);
	}
}
