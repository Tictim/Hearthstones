package tictim.hearthstones.contents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import tictim.hearthstones.Hearthstones;

public final class ModTags{
	private ModTags(){}

	public static final TagKey<Item> DUSTS_AQUAMARINE = ItemTags.create(new ResourceLocation("forge", "dusts/aquamarine"));
	public static final TagKey<Item> DUSTS_DIAMOND = ItemTags.create(new ResourceLocation("forge", "dusts/diamond"));
	public static final TagKey<Item> DUSTS_LAPIS = ItemTags.create(new ResourceLocation("forge", "dusts/lapis"));
	public static final TagKey<Item> DUSTS_AMETHYST = ItemTags.create(new ResourceLocation("forge", "dusts/amethyst"));

	public static final TagKey<Item> GEMS_AQUAMARINE = ItemTags.create(new ResourceLocation("forge", "gems/aquamarine"));

	public static final TagKey<Item> ORES_AQUAMARINE = ItemTags.create(new ResourceLocation("forge", "ores/aquamarine"));
	public static final TagKey<Item> STORAGE_BLOCKS_AQUAMARINE = ItemTags.create(new ResourceLocation("forge", "storage_blocks/aquamarine"));

	public static final TagKey<Item> TAVERNS = ItemTags.create(new ResourceLocation(Hearthstones.MODID, "taverns"));

	public static final TagKey<Item> HEARTHSTONE_MATERIAL = ItemTags.create(new ResourceLocation(Hearthstones.MODID, "hearthstone_material"));

	public static boolean has(TagKey<Item> tag, Item item){
		ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
		return tags!=null&&tags.getTag(tag).contains(item);
	}

	public static final class Blocks{
		private Blocks(){}

		public static final TagKey<Block> TAVERNS = BlockTags.create(new ResourceLocation(Hearthstones.MODID, "taverns"));
		public static final TagKey<Block> STORAGE_BLOCKS_AQUAMARINE = BlockTags.create(new ResourceLocation("forge", "storage_blocks/aquamarine"));

		public static final TagKey<Block> ORES_AQUAMARINE = BlockTags.create(new ResourceLocation("forge", "ores/aquamarine"));
	}
}
