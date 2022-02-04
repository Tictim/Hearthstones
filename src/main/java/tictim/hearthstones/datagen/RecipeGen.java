package tictim.hearthstones.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import tictim.hearthstones.contents.ModRecipes;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.contents.recipes.EasyModeCondition;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;
import static net.minecraft.data.recipes.SpecialRecipeBuilder.special;
import static tictim.hearthstones.Hearthstones.MODID;
import static tictim.hearthstones.contents.ModItems.*;

public class RecipeGen extends RecipeProvider{
	public RecipeGen(DataGenerator generatorIn){
		super(generatorIn);
	}

	private final EasyModeCondition easyModeCondition = new EasyModeCondition();
	private final NotCondition notEasyModeCondition = new NotCondition(easyModeCondition);

	private void easyMode(Consumer<FinishedRecipe> consumer, String name, Consumer<Consumer<FinishedRecipe>> callable){
		ConditionalRecipe.builder().addCondition(easyModeCondition).addRecipe(callable).build(consumer, MODID, "easy_mode/"+name);
	}
	private void notEasyMode(Consumer<FinishedRecipe> consumer, String name, Consumer<Consumer<FinishedRecipe>> callable){
		ConditionalRecipe.builder().addCondition(notEasyModeCondition).addRecipe(callable).build(consumer, MODID, "not_easy_mode/"+name);
	}

	@Override protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer){
		// Shaped Recipe
		shaped(AQUAMARINE_BLOCK.get())
				.pattern("111")
				.pattern("111")
				.pattern("111")
				.define('1', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(ModTags.GEMS_AQUAMARINE))
				.save(consumer);

		shaped(BLUE_TAVERNCLOTH.get())
				.pattern("111")
				.pattern("121")
				.pattern("1 1")
				.define('1', BLUE_LEATHER.get())
				.define('2', Tags.Items.GEMS_EMERALD)
				.unlockedBy("has_blue_leather", has(BLUE_LEATHER.get()))
				.save(consumer);

		shaped(COMPANION_HEARTHSTONE.get())
				.pattern("111")
				.pattern("121")
				.pattern("1 1")
				.define('1', BLUE_LEATHER.get())
				.define('2', HEARTHSTONE.get())
				.unlockedBy("has_blue_leather", has(BLUE_LEATHER.get()))
				.save(consumer);

		shaped(COMPANION_STONE.get())
				.pattern(" 11")
				.pattern("111")
				.pattern("11 ")
				.define('1', ModTags.HEARTHSTONE_MATERIAL)
				.unlockedBy("has_blue_leather", has(BLUE_LEATHER.get()))
				.save(consumer);

		shaped(HEARTHING_GEM.get())
				.pattern(" 11")
				.pattern("111")
				.pattern("11 ")
				.define('1', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(ModTags.GEMS_AQUAMARINE))
				.save(consumer);

		shaped(MORTAR.get())
				.pattern("  3")
				.pattern("121")
				.pattern("111")
				.define('1', Tags.Items.STONE)
				.define('2', Tags.Items.INGOTS_IRON)
				.define('3', Tags.Items.NUGGETS_IRON)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		// Shapeless Recipe
		shapeless(AQUAMARINE.get(), 9)
				.requires(AQUAMARINE_BLOCK.get())
				.unlockedBy("has_aquamarine_block", has(AQUAMARINE_BLOCK.get()))
				.save(consumer);

		shapeless(BLUE_LEATHER.get())
				.requires(DEEP_BLUE.get())
				.requires(DEEP_BLUE.get())
				.requires(DEEP_BLUE.get())
				.requires(DEEP_BLUE.get())
				.requires(Tags.Items.LEATHER)
				.requires(Items.MILK_BUCKET)
				.unlockedBy("has_deep_blue", has(DEEP_BLUE.get()))
				.save(consumer);

		shapeless(DEEP_BLUE.get(), 3)
				.requires(ModTags.DUSTS_AQUAMARINE)
				.requires(ModTags.DUSTS_DIAMOND)
				.requires(ModTags.DUSTS_LAPIS)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		shapeless(DEEP_PURPLE.get(), 3)
				.requires(ModTags.DUSTS_AQUAMARINE)
				.requires(ModTags.DUSTS_DIAMOND)
				.requires(ModTags.DUSTS_AMETHYST)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		shapeless(RED_LEATHER.get())
				.requires(Tags.Items.DYES_RED)
				.requires(Tags.Items.DYES_RED)
				.requires(Tags.Items.DYES_RED)
				.requires(Tags.Items.LEATHER)
				.requires(Items.WATER_BUCKET)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		shapeless(TAVERN.get())
				.requires(TAVERNCLOTH.get())
				.requires(ItemTags.LOGS)
				.unlockedBy("has_taverncloth", has(TAVERNCLOTH.get()))
				.save(consumer, new ResourceLocation(MODID, "tavern_from_taverncloth"));

		addMortarRecipe(Ingredient.of(ModTags.GEMS_AQUAMARINE), AQUAMARINE_DUST.get(), consumer);
		addMortarRecipe(Ingredient.of(Tags.Items.GEMS_DIAMOND), DIAMOND_DUST.get(), consumer);
		addMortarRecipe(Ingredient.of(Tags.Items.GEMS_LAPIS), LAPIS_DUST.get(), consumer);
		addMortarRecipe(Ingredient.of(Items.AMETHYST_SHARD), AMETHYST_DUST.get(), consumer);

		// Easy Mode
		easyMode(consumer, "hearthstone", c -> shaped(HEARTHSTONE.get())
				.pattern(" 11")
				.pattern("121")
				.pattern("11 ")
				.define('1', ModTags.HEARTHSTONE_MATERIAL)
				.define('2', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		easyMode(consumer, "tavern", c -> shaped(TAVERN.get())
				.pattern("111")
				.pattern("121")
				.pattern("131")
				.define('1', RED_LEATHER.get())
				.define('2', ModTags.GEMS_AQUAMARINE)
				.define('3', ItemTags.LOGS)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		easyMode(consumer, "taverncloth", c -> shaped(TAVERNCLOTH.get())
				.pattern("111")
				.pattern("121")
				.pattern("1 1")
				.define('1', RED_LEATHER.get())
				.define('2', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		// Not Easy Mode
		notEasyMode(consumer, "hearthstone", c -> shaped(HEARTHSTONE.get())
				.pattern(" 11")
				.pattern("121")
				.pattern("11 ")
				.define('1', ModTags.HEARTHSTONE_MATERIAL)
				.define('2', DEEP_BLUE.get())
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		notEasyMode(consumer, "hearthing_planks", c -> shaped(HEARTHING_PLANKS.get())
				.pattern(" 11")
				.pattern("121")
				.pattern("11 ")
				.define('1', ItemTags.LOGS)
				.define('2', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		notEasyMode(consumer, "shabby_tavern", c -> shaped(SHABBY_TAVERN.get())
				.pattern("111")
				.pattern("121")
				.pattern("131")
				.define('1', TATTERED_LEATHER.get())
				.define('2', ModTags.GEMS_AQUAMARINE)
				.define('3', ItemTags.LOGS)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		notEasyMode(consumer, "shabby_tavern_from_taverncloth", c -> shapeless(SHABBY_TAVERN.get())
				.requires(TATTERED_TAVERNCLOTH.get())
				.requires(ItemTags.LOGS)
				.unlockedBy("has_tattered_taverncloth", has(TATTERED_TAVERNCLOTH.get()))
				.save(c));

		notEasyMode(consumer, "tattered_leather", c -> shapeless(TATTERED_LEATHER.get(), 7)
				.requires(Items.BONE_MEAL)
				.requires(Items.BONE_MEAL)
				.requires(Items.BONE_MEAL)
				.requires(Tags.Items.LEATHER)
				.requires(Items.WATER_BUCKET)
				.unlockedBy("has_leather", has(Tags.Items.LEATHER))
				.save(c));

		notEasyMode(consumer, "tattered_leather_from_grass", c -> shapeless(TATTERED_LEATHER.get())
				.requires(Items.BONE_MEAL)
				.requires(Ingredient.of(Items.GRASS, Items.FERN))
				.requires(Ingredient.of(Items.GRASS, Items.FERN))
				.requires(Ingredient.of(Items.GRASS, Items.FERN))
				.requires(Items.WATER_BUCKET)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		notEasyMode(consumer, "tattered_taverncloth", c -> shaped(TATTERED_TAVERNCLOTH.get())
				.pattern("111")
				.pattern("121")
				.pattern("1 1")
				.define('1', TATTERED_LEATHER.get())
				.define('2', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		notEasyMode(consumer, "tavern", c -> shaped(TAVERN.get())
				.pattern("111")
				.pattern("121")
				.pattern("131")
				.define('1', RED_LEATHER.get())
				.define('2', ModTags.STORAGE_BLOCKS_AQUAMARINE)
				.define('3', ItemTags.LOGS)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		notEasyMode(consumer, "taverncloth", c -> shaped(TAVERNCLOTH.get())
				.pattern("111")
				.pattern("121")
				.pattern("1 1")
				.define('1', RED_LEATHER.get())
				.define('2', ModTags.STORAGE_BLOCKS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(c));

		shaped(WAYPOINT.get(), 4)
				.pattern(" 13")
				.pattern("121")
				.pattern("11 ")
				.define('1', Items.AMETHYST_SHARD)
				.define('2', ModTags.GEMS_AQUAMARINE)
				.define('3', Items.STRING)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		shapeless(WAYPOINT.get())
				.requires(WAYPOINT.get())
				.unlockedBy("has_waypoint", has(WAYPOINT.get()))
				.save(consumer, MODID+":clear_waypoint");

		shapeless(WAYPOINT_BINDER.get())
				.requires(Items.BOOK)
				.requires(DEEP_PURPLE.get())
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		special(ModRecipes.CHARGE_BINDER_RECIPE.get())
				.save(consumer, MODID+":charge_binder");

		// Furnace Recipe
		Consumer<FinishedRecipe> c = result -> consumer.accept(result instanceof SimpleCookingRecipeBuilder.Result ?
				new MultiItemCookingResult((SimpleCookingRecipeBuilder.Result)result, 9) : result);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(AQUAMARINE_ORE.get()), AQUAMARINE.get(), 0.5f, 200)
				.unlockedBy("has_aquamarine_ore", has(AQUAMARINE_ORE.get()))
				.save(c, new ResourceLocation(MODID, "smelting/aquamarine"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(AQUAMARINE_ORE.get()), AQUAMARINE.get(), 0.5f, 100)
				.unlockedBy("has_aquamarine_ore", has(AQUAMARINE_ORE.get()))
				.save(c, new ResourceLocation(MODID, "smelting/aquamarine_blasting"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(DEEPSLATE_AQUAMARINE_ORE.get()), AQUAMARINE.get(), 0.5f, 200)
				.unlockedBy("has_deepslate_aquamarine_ore", has(DEEPSLATE_AQUAMARINE_ORE.get()))
				.save(c, new ResourceLocation(MODID, "smelting/deepslate_aquamarine"));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(DEEPSLATE_AQUAMARINE_ORE.get()), AQUAMARINE.get(), 0.5f, 100)
				.unlockedBy("has_deepslate_aquamarine_ore", has(DEEPSLATE_AQUAMARINE_ORE.get()))
				.save(c, new ResourceLocation(MODID, "smelting/deepslate_aquamarine_blasting"));
	}

	private void addMortarRecipe(Ingredient in, ItemLike out, Consumer<FinishedRecipe> consumer){
		addMortarRecipe(in, out, null, consumer);
	}

	private void addMortarRecipe(Ingredient in, ItemLike out, @Nullable String save, Consumer<FinishedRecipe> consumer){
		ShapelessRecipeBuilder b = shapeless(out)
				.requires(MORTAR.get())
				.requires(in)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()));
		if(save!=null) b.save(consumer, new ResourceLocation(MODID, save));
		else b.save(consumer);
	}
}
