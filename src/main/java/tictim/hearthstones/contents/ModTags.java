package tictim.hearthstones.contents;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import tictim.hearthstones.Hearthstones;

public final class ModTags{
	private ModTags(){}

	public static final Tag<Item> DUSTS_AQUAMARINE = new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/aquamarine"));
	public static final Tag<Item> DUSTS_DEEP_BLUE = new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/deep_blue"));
	public static final Tag<Item> DUSTS_DIAMOND = new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/diamond"));
	public static final Tag<Item> DUSTS_LAPIS = new ItemTags.Wrapper(new ResourceLocation("forge", "dusts/lapis"));

	public static final Tag<Item> GEMS_AQUAMARINE = new ItemTags.Wrapper(new ResourceLocation("forge", "gems/aquamarine"));

	public static final Tag<Item> ORES_AQUAMARINE = new ItemTags.Wrapper(new ResourceLocation("forge", "ores/aquamarine"));
	public static final Tag<Item> STORAGE_BLOCKS_AQUAMARINE = new ItemTags.Wrapper(new ResourceLocation("forge", "storage_blocks/aquamarine"));

	public static final Tag<Item> TAVERNS = new ItemTags.Wrapper(new ResourceLocation(Hearthstones.MODID, "taverns"));

	public static final Tag<Item> HEARTHSTONE_MATERIAL = new ItemTags.Wrapper(new ResourceLocation(Hearthstones.MODID, "hearthstone_material"));

	public static final class Blocks{
		private Blocks(){}

		public static final Tag<Block> TAVERNS = new BlockTags.Wrapper(new ResourceLocation(Hearthstones.MODID, "taverns"));
		public static final Tag<Block> STORAGE_BLOCKS_AQUAMARINE = new BlockTags.Wrapper(new ResourceLocation("forge", "storage_blocks/aquamarine"));

		public static final Tag<Block> ORES_AQUAMARINE = new BlockTags.Wrapper(new ResourceLocation("forge", "ores/aquamarine"));
	}
}
