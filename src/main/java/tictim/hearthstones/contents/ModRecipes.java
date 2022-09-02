package tictim.hearthstones.contents;

import com.google.common.base.Preconditions;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.item.TavernItem;
import tictim.hearthstones.contents.recipe.ChargeBinderRecipe;
import tictim.hearthstones.contents.recipe.PotionIngredient;
import tictim.hearthstones.contents.recipe.ShapelessPotionRecipe;
import tictim.hearthstones.contents.recipe.TavernSkinRecipe;

import javax.annotation.Nonnull;
import java.util.Objects;

import static tictim.hearthstones.Hearthstones.MODID;
import static tictim.hearthstones.contents.ModItems.*;

@Mod.EventBusSubscriber(modid = MODID)
public class ModRecipes{
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event){
		IForgeRegistry<IRecipe> r = event.getRegistry();

		// Shaped Recipe
		shaped(r, AQUAMARINE_BLOCK, "111", "111", "111", '1', ModOreDict.GEM_AQUAMARINE);
		shaped(r, BLUE_TAVERNCLOTH, "111", "121", "1 1", '1', BLUE_LEATHER, '2', ModOreDict.GEM_EMERALD);
		shaped(r, COMPANION_HEARTHSTONE, "111", "121", "1 1", '1', BLUE_LEATHER, '2', HEARTHSTONE);
		shaped(r, COMPANION_STONE, " 11", "111", "11 ", '1', ModOreDict.HEARTHSTONE_MATERIAL);
		shaped(r, HEARTHING_GEM, " 11", "111", "11 ", '1', ModOreDict.GEM_AQUAMARINE);
		shaped(r, MORTAR, "  2", "121", "111", '1', ModOreDict.STONE, '2', ModOreDict.INGOT_IRON);
		shaped(r, new ItemStack(WAYPOINT, 4), " 13", "121", "11 ", '1', AMETHYST_SHARD, '2', ModOreDict.GEM_AQUAMARINE, '3', Items.STRING);
		shaped(r, AMETHYST_BLOCK, "11", "11", '1', AMETHYST_SHARD);
		shaped(r, BUDDING_AMETHYST, " 1 ", "121", " 1 ", '1', AMETHYST_SHARD, '2', Items.GHAST_TEAR); // temp recipe

		// Shapeless Recipe
		shapeless(r, new ItemStack(AQUAMARINE, 9), AQUAMARINE_BLOCK);
		shapeless(r, BLUE_LEATHER, DEEP_BLUE, DEEP_BLUE, DEEP_BLUE, DEEP_BLUE, ModOreDict.LEATHER, Items.MILK_BUCKET);
		shapeless(r, new ItemStack(DEEP_BLUE, 3), ModOreDict.DUST_AQUAMARINE, ModOreDict.DUST_DIAMOND, ModOreDict.DUST_LAPIS);
		shapeless(r, new ItemStack(DEEP_PURPLE, 3), ModOreDict.DUST_AQUAMARINE, ModOreDict.DUST_DIAMOND, ModOreDict.DUST_AMETHYST);
		shapeless(r, RED_LEATHER, ModOreDict.DYE_RED, ModOreDict.DYE_RED, ModOreDict.DYE_RED, ModOreDict.LEATHER, Items.WATER_BUCKET);

		shapeless(r, "tavern_from_taverncloth", TavernItem.normal(), TAVERNCLOTH, ModOreDict.LOG_WOOD);
		shapeless(r, "clear_waypoint", WAYPOINT, WAYPOINT);

		r.register(new ChargeBinderRecipe().setRegistryName("charge_binder"));
		r.register(new TavernSkinRecipe(Ingredient.fromItem(TAVERN)).setRegistryName("tavern_skin"));

		shapeless(r, AQUAMARINE_DUST, MORTAR, ModOreDict.GEM_AQUAMARINE);
		shapeless(r, DIAMOND_DUST, MORTAR, ModOreDict.GEM_DIAMOND);
		shapeless(r, LAPIS_DUST, MORTAR, ModOreDict.GEM_LAPIS);
		shapeless(r, AMETHYST_DUST, MORTAR, AMETHYST_SHARD);

		shapeless(r, new ItemStack(AMETHYST_SHARD, 4), AMETHYST_BLOCK);

		if(ModCfg.easyMode){
			shaped(r, "easy_mode/hearthstone", HEARTHSTONE, " 11", "121", "11 ", '1', ModOreDict.HEARTHSTONE_MATERIAL, '2', ModOreDict.GEM_AQUAMARINE);
			shaped(r, "easy_mode/tavern", TavernItem.normal(), "111", "121", "131", '1', RED_LEATHER, '2', ModOreDict.GEM_AQUAMARINE, '3', ModOreDict.LOG_WOOD);
			shaped(r, "easy_mode/taverncloth", TAVERNCLOTH, "111", "121", "1 1", '1', RED_LEATHER, '2', ModOreDict.GEM_AQUAMARINE);
			shapeless(r, "easy_mode/binder", INFINITE_WAYPOINT_BINDER, Items.BOOK, ModOreDict.DUST_AMETHYST, ModOreDict.DUST_AMETHYST, ModOreDict.DUST_AMETHYST);
		}else{
			shaped(r, "not_easy_mode/hearthstone", HEARTHSTONE, " 11", "121", "11 ", '1', ModOreDict.HEARTHSTONE_MATERIAL, '2', DEEP_BLUE);
			shaped(r, "not_easy_mode/hearthing_planks", SHABBY_HEARTHSTONE, " 11", "121", "11 ", '1', ModOreDict.LOG_WOOD, '2', ModOreDict.GEM_AQUAMARINE);
			shaped(r, "not_easy_mode/shabby_tavern", TavernItem.shabby(), "111", "121", "131", '1', TATTERED_LEATHER, '2', ModOreDict.GEM_AQUAMARINE, '3', ModOreDict.LOG_WOOD);
			shapeless(r, "not_easy_mode/shabby_tavern_from_taverncloth", TavernItem.shabby(), TATTERED_TAVERNCLOTH, ModOreDict.LOG_WOOD);
			ItemStack boneMeal = new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage());
			shapeless(r, "not_easy_mode/tattered_leather", new ItemStack(TATTERED_LEATHER, 7), boneMeal, boneMeal, boneMeal, ModOreDict.LEATHER, Items.WATER_BUCKET);
			Ingredient grass = Ingredient.fromStacks(new ItemStack(Blocks.TALLGRASS, 1, EnumType.GRASS.getMeta()), new ItemStack(Blocks.TALLGRASS, 1, EnumType.FERN.getMeta()));
			shapeless(r, "not_easy_mode/tattered_leather_from_grass", TATTERED_LEATHER, grass, grass, grass, Items.WATER_BUCKET);
			shaped(r, "not_easy_mode/tattered_taverncloth", TATTERED_TAVERNCLOTH, "111", "121", "1 1", '1', TATTERED_LEATHER, '2', ModOreDict.GEM_AQUAMARINE);
			shaped(r, "not_easy_mode/tavern", TAVERN, "111", "121", "131", '1', RED_LEATHER, '2', ModOreDict.BLOCK_AQUAMARINE, '3', ModOreDict.LOG_WOOD);
			shaped(r, "not_easy_mode/taverncloth", TAVERNCLOTH, "111", "121", "1 1", '1', RED_LEATHER, '2', ModOreDict.BLOCK_AQUAMARINE);
			shapeless(r, "not_easy_mode/binder", WAYPOINT_BINDER, Items.BOOK, ModOreDict.DUST_AMETHYST, ModOreDict.DUST_AMETHYST, ModOreDict.DUST_AMETHYST);
			r.register(new ShapelessPotionRecipe(null, new ItemStack(PURPLE_LEATHER), DEEP_PURPLE, DEEP_PURPLE, DEEP_PURPLE, DEEP_PURPLE, ModOreDict.LEATHER, PotionIngredient.of())
					.setRegistryName("not_easy_mode/purple_leather"));
			shapeless(r, "not_easy_mode/infinite_binder", INFINITE_WAYPOINT_BINDER, Items.BOOK, PURPLE_LEATHER, PURPLE_LEATHER, PURPLE_LEATHER);
		}

		registerFurnaceRecipes();
	}

	public static void registerFurnaceRecipes(){
		FurnaceRecipes.instance().addSmeltingRecipeForBlock(ModBlocks.AQUAMARINE_ORE, new ItemStack(ModItems.AQUAMARINE, 9), .5f);
	}

	private static void shaped(IForgeRegistry<IRecipe> registry, Item item, Object... params){
		shaped(registry, Objects.requireNonNull(item.getRegistryName()).getPath(), new ItemStack(item), params);
	}
	private static void shaped(IForgeRegistry<IRecipe> registry, String name, Item item, Object... params){
		shaped(registry, name, new ItemStack(item), params);
	}
	private static void shaped(IForgeRegistry<IRecipe> registry, ItemStack output, Object... params){
		Preconditions.checkState(!output.isEmpty(), "Empty output");
		shaped(registry, Objects.requireNonNull(output.getItem().getRegistryName()).getPath(), output, params);
	}
	private static void shaped(IForgeRegistry<IRecipe> registry, String name, @Nonnull ItemStack output, Object... params){
		registry.register(new ShapedOreRecipe(null, output, params).setRegistryName(name));
	}

	private static void shapeless(IForgeRegistry<IRecipe> registry, Item item, Object... params){
		shapeless(registry, Objects.requireNonNull(item.getRegistryName()).getPath(), new ItemStack(item), params);
	}
	private static void shapeless(IForgeRegistry<IRecipe> registry, String name, Item item, Object... params){
		shapeless(registry, name, new ItemStack(item), params);
	}
	private static void shapeless(IForgeRegistry<IRecipe> registry, ItemStack output, Object... params){
		Preconditions.checkState(!output.isEmpty(), "Empty output");
		shapeless(registry, Objects.requireNonNull(output.getItem().getRegistryName()).getPath(), output, params);
	}
	private static void shapeless(IForgeRegistry<IRecipe> registry, String name, @Nonnull ItemStack output, Object... params){
		registry.register(new ShapelessOreRecipe(null, output, params).setRegistryName(name));
	}
}
