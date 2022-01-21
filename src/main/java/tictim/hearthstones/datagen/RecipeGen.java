package tictim.hearthstones.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
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
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.contents.recipes.EasyModeCondition;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static tictim.hearthstones.Hearthstones.MODID;
import static tictim.hearthstones.contents.ModItems.*;

public class RecipeGen extends RecipeProvider{
	public RecipeGen(DataGenerator generatorIn){
		super(generatorIn);
	}

	@Override protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer){
		// Shaped Recipe
		ShapedRecipeBuilder.shaped(AQUAMARINE_BLOCK.get())
				.pattern("111")
				.pattern("111")
				.pattern("111")
				.define('1', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(ModTags.GEMS_AQUAMARINE))
				.save(consumer);

		ShapedRecipeBuilder.shaped(BLUE_TAVERNCLOTH.get())
				.pattern("111")
				.pattern("121")
				.pattern("1 1")
				.define('1', BLUE_LEATHER.get())
				.define('2', Tags.Items.GEMS_EMERALD)
				.unlockedBy("has_blue_leather", has(BLUE_LEATHER.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(COMPANION_HEARTHSTONE.get())
				.pattern("111")
				.pattern("121")
				.pattern("1 1")
				.define('1', BLUE_LEATHER.get())
				.define('2', HEARTHSTONE.get())
				.unlockedBy("has_blue_leather", has(BLUE_LEATHER.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(COMPANION_STONE.get())
				.pattern(" 11")
				.pattern("111")
				.pattern("11 ")
				.define('1', ModTags.HEARTHSTONE_MATERIAL)
				.unlockedBy("has_blue_leather", has(BLUE_LEATHER.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(HEARTHING_GEM.get())
				.pattern(" 11")
				.pattern("111")
				.pattern("11 ")
				.define('1', ModTags.GEMS_AQUAMARINE)
				.unlockedBy("has_aquamarine", has(ModTags.GEMS_AQUAMARINE))
				.save(consumer);

		ShapedRecipeBuilder.shaped(MORTAR.get())
				.pattern("  3")
				.pattern("121")
				.pattern("111")
				.define('1', Tags.Items.STONE)
				.define('2', Tags.Items.INGOTS_IRON)
				.define('3', Tags.Items.NUGGETS_IRON)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		// Shapeless Recipe
		ShapelessRecipeBuilder.shapeless(AQUAMARINE.get(), 9)
				.requires(AQUAMARINE_BLOCK.get())
				.unlockedBy("has_aquamarine_block", has(AQUAMARINE_BLOCK.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(BLUE_LEATHER.get())
				.requires(ModTags.DUSTS_DEEP_BLUE)
				.requires(ModTags.DUSTS_DEEP_BLUE)
				.requires(ModTags.DUSTS_DEEP_BLUE)
				.requires(ModTags.DUSTS_DEEP_BLUE)
				.requires(Tags.Items.LEATHER)
				.requires(Items.MILK_BUCKET)
				.unlockedBy("has_deep_blue", has(DEEP_BLUE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(DEEP_BLUE.get(), 3)
				.requires(ModTags.DUSTS_AQUAMARINE)
				.requires(ModTags.DUSTS_DIAMOND)
				.requires(ModTags.DUSTS_LAPIS)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(RED_LEATHER.get())
				.requires(Tags.Items.DYES_RED)
				.requires(Tags.Items.DYES_RED)
				.requires(Tags.Items.DYES_RED)
				.requires(Tags.Items.LEATHER)
				.requires(Items.WATER_BUCKET)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(TAVERN.get())
				.requires(TAVERNCLOTH.get())
				.requires(ItemTags.LOGS)
				.unlockedBy("has_taverncloth", has(TAVERNCLOTH.get()))
				.save(consumer, new ResourceLocation(MODID, "tavern_from_taverncloth"));

		addMortarRecipe(Ingredient.of(ModTags.GEMS_AQUAMARINE), AQUAMARINE_DUST.get(), consumer);
		addMortarRecipe(Ingredient.of(Tags.Items.GEMS_DIAMOND), DIAMOND_DUST.get(), consumer);
		addMortarRecipe(Ingredient.of(Tags.Items.GEMS_LAPIS), LAPIS_DUST.get(), consumer);

		// Easy Mode
		EasyModeCondition easyModeCondition = new EasyModeCondition();
		ConditionalRecipe.builder()
				.addCondition(easyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(HEARTHSTONE.get())
						.pattern(" 11")
						.pattern("121")
						.pattern("11 ")
						.define('1', ModTags.HEARTHSTONE_MATERIAL)
						.define('2', ModTags.GEMS_AQUAMARINE)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "easy_mode/hearthstone");

		ConditionalRecipe.builder()
				.addCondition(easyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(TAVERN.get())
						.pattern("111")
						.pattern("121")
						.pattern("131")
						.define('1', RED_LEATHER.get())
						.define('2', ModTags.GEMS_AQUAMARINE)
						.define('3', ItemTags.LOGS)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "easy_mode/tavern");

		ConditionalRecipe.builder()
				.addCondition(easyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(TAVERNCLOTH.get())
						.pattern("111")
						.pattern("121")
						.pattern("1 1")
						.define('1', RED_LEATHER.get())
						.define('2', ModTags.GEMS_AQUAMARINE)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "easy_mode/taverncloth");

		// Not Easy Mode
		NotCondition notEasyModeCondition = new NotCondition(easyModeCondition);

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(HEARTHSTONE.get())
						.pattern(" 11")
						.pattern("121")
						.pattern("11 ")
						.define('1', ModTags.HEARTHSTONE_MATERIAL)
						.define('2', ModTags.DUSTS_DEEP_BLUE)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/hearthstone");
		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(HEARTHING_PLANKS.get())
						.pattern(" 11")
						.pattern("121")
						.pattern("11 ")
						.define('1', ItemTags.LOGS)
						.define('2', ModTags.GEMS_AQUAMARINE)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/hearthing_planks");
		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(SHABBY_TAVERN.get())
						.pattern("111")
						.pattern("121")
						.pattern("131")
						.define('1', TATTERED_LEATHER.get())
						.define('2', ModTags.GEMS_AQUAMARINE)
						.define('3', ItemTags.LOGS)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/shabby_tavern");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapelessRecipeBuilder.shapeless(SHABBY_TAVERN.get())
						.requires(TATTERED_TAVERNCLOTH.get())
						.requires(ItemTags.LOGS)
						.unlockedBy("has_tattered_taverncloth", has(TATTERED_TAVERNCLOTH.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/shabby_tavern_from_taverncloth");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapelessRecipeBuilder.shapeless(TATTERED_LEATHER.get(), 7)
						.requires(Items.BONE_MEAL)
						.requires(Items.BONE_MEAL)
						.requires(Items.BONE_MEAL)
						.requires(Tags.Items.LEATHER)
						.requires(Items.WATER_BUCKET)
						.unlockedBy("has_leather", has(Tags.Items.LEATHER))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/tattered_leather");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapelessRecipeBuilder.shapeless(TATTERED_LEATHER.get())
						.requires(Items.BONE_MEAL)
						.requires(Ingredient.of(Items.GRASS, Items.FERN))
						.requires(Ingredient.of(Items.GRASS, Items.FERN))
						.requires(Ingredient.of(Items.GRASS, Items.FERN))
						.requires(Items.WATER_BUCKET)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/tattered_leather_from_grass");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(TATTERED_TAVERNCLOTH.get())
						.pattern("111")
						.pattern("121")
						.pattern("1 1")
						.define('1', TATTERED_LEATHER.get())
						.define('2', ModTags.GEMS_AQUAMARINE)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/tattered_taverncloth");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(TAVERN.get())
						.pattern("111")
						.pattern("121")
						.pattern("131")
						.define('1', RED_LEATHER.get())
						.define('2', ModTags.STORAGE_BLOCKS_AQUAMARINE)
						.define('3', ItemTags.LOGS)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/tavern");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shaped(TAVERNCLOTH.get())
						.pattern("111")
						.pattern("121")
						.pattern("1 1")
						.define('1', RED_LEATHER.get())
						.define('2', ModTags.STORAGE_BLOCKS_AQUAMARINE)
						.unlockedBy("has_aquamarine", has(AQUAMARINE.get()))
						.save(c))
				.build(consumer, MODID, "not_easy_mode/taverncloth");

		// Furnace Recipe
		Consumer<FinishedRecipe> c = result -> consumer.accept(result instanceof SimpleCookingRecipeBuilder.Result ? new MultiItemCookingResult((SimpleCookingRecipeBuilder.Result)result, 9) : result);
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
		ShapelessRecipeBuilder b = ShapelessRecipeBuilder.shapeless(out)
				.requires(MORTAR.get())
				.requires(in)
				.unlockedBy("has_aquamarine", has(AQUAMARINE.get()));
		if(save!=null) b.save(consumer, new ResourceLocation(MODID, save));
		else b.save(consumer);
	}
}
