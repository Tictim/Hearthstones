package tictim.hearthstones.contents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import tictim.hearthstones.Hearthstones;

public final class ModTags{
	private ModTags(){}

	public static final Tags.IOptionalNamedTag<Item> DUSTS_AQUAMARINE = ItemTags.createOptional(new ResourceLocation("forge", "dusts/aquamarine"));
	public static final Tags.IOptionalNamedTag<Item> DUSTS_DEEP_BLUE = ItemTags.createOptional(new ResourceLocation("forge", "dusts/deep_blue"));
	public static final Tags.IOptionalNamedTag<Item> DUSTS_DIAMOND = ItemTags.createOptional(new ResourceLocation("forge", "dusts/diamond"));
	public static final Tags.IOptionalNamedTag<Item> DUSTS_LAPIS = ItemTags.createOptional(new ResourceLocation("forge", "dusts/lapis"));

	public static final Tags.IOptionalNamedTag<Item> GEMS_AQUAMARINE = ItemTags.createOptional(new ResourceLocation("forge", "gems/aquamarine"));

	public static final Tags.IOptionalNamedTag<Item> ORES_AQUAMARINE = ItemTags.createOptional(new ResourceLocation("forge", "ores/aquamarine"));
	public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_AQUAMARINE = ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/aquamarine"));

	public static final Tags.IOptionalNamedTag<Item> TAVERNS = ItemTags.createOptional(new ResourceLocation(Hearthstones.MODID, "taverns"));

	public static final Tags.IOptionalNamedTag<Item> HEARTHSTONE_MATERIAL = ItemTags.createOptional(new ResourceLocation(Hearthstones.MODID, "hearthstone_material"));

	public static final class Blocks{
		private Blocks(){}

		public static final Tags.IOptionalNamedTag<Block> TAVERNS = BlockTags.createOptional(new ResourceLocation(Hearthstones.MODID, "taverns"));
		public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_AQUAMARINE = BlockTags.createOptional(new ResourceLocation("forge", "storage_blocks/aquamarine"));

		public static final Tags.IOptionalNamedTag<Block> ORES_AQUAMARINE = BlockTags.createOptional(new ResourceLocation("forge", "ores/aquamarine"));
	}
}
