package tictim.hearthstones.contents;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModOreDict{
	public static final String HEARTHSTONE_MATERIAL = "stoneHearthstoneMaterial";
	public static final String GEM_AQUAMARINE = "gemAquamarine";
	public static final String DUST_AQUAMARINE = "dustAquamarine";
	public static final String ORE_AQUAMARINE = "oreAquamarine";
	public static final String BLOCK_AQUAMARINE = "blockAquamarine";
	public static final String DUST_DIAMOND = "dustDiamond";
	public static final String DUST_LAPIS = "dustLapis";
	public static final String DUST_AMETHYST = "dustAmethyst";

	public static final String INGOT_IRON = "ingotIron";
	public static final String LOG_WOOD = "logWood";
	public static final String GEM_EMERALD = "gemEmerald";
	public static final String STONE = "stone";
	public static final String LEATHER = "leather";
	public static final String DYE_RED = "dyeRed";
	public static final String GEM_DIAMOND = "gemDiamond";
	public static final String GEM_LAPIS = "gemLapis";
	public static final String SLAB_WOOD = "slabWood";

	public static void register(){
		OreDictionary.registerOre(HEARTHSTONE_MATERIAL, new ItemStack(Blocks.STONE, 1, 1));
		OreDictionary.registerOre(HEARTHSTONE_MATERIAL, new ItemStack(Blocks.STONE, 1, 3));
		OreDictionary.registerOre(HEARTHSTONE_MATERIAL, new ItemStack(Blocks.STONE, 1, 5));
		OreDictionary.registerOre(GEM_AQUAMARINE, new ItemStack(ModItems.AQUAMARINE));
		OreDictionary.registerOre(DUST_AQUAMARINE, new ItemStack(ModItems.AQUAMARINE_DUST));
		OreDictionary.registerOre(ORE_AQUAMARINE, new ItemStack(ModBlocks.AQUAMARINE_ORE));
		OreDictionary.registerOre(BLOCK_AQUAMARINE, new ItemStack(ModBlocks.AQUAMARINE_BLOCK));
		OreDictionary.registerOre(DUST_DIAMOND, new ItemStack(ModItems.DIAMOND_DUST));
		OreDictionary.registerOre(DUST_LAPIS, new ItemStack(ModItems.LAPIS_DUST));
		OreDictionary.registerOre(DUST_AMETHYST, new ItemStack(ModItems.AMETHYST_DUST));
	}
}
