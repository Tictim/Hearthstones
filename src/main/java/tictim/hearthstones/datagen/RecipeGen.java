package tictim.hearthstones.datagen;

import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import tictim.hearthstones.contents.ModTags;
import tictim.hearthstones.recipes.EasyModeCondition;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static tictim.hearthstones.Hearthstones.MODID;
import static tictim.hearthstones.contents.ModItems.*;

public class RecipeGen extends RecipeProvider{
	public RecipeGen(DataGenerator generatorIn){
		super(generatorIn);
	}

	@Override protected void registerRecipes(Consumer<IFinishedRecipe> consumer){
		// Shaped Recipe
		ShapedRecipeBuilder.shapedRecipe(AQUAMARINE_BLOCK.get())
				.patternLine("111")
				.patternLine("111")
				.patternLine("111")
				.key('1', ModTags.GEMS_AQUAMARINE)
				.addCriterion("has_aquamarine", hasItem(ModTags.GEMS_AQUAMARINE))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(BLUE_TAVERNCLOTH.get())
				.patternLine("111")
				.patternLine("121")
				.patternLine("1 1")
				.key('1', BLUE_LEATHER.get())
				.key('2', Tags.Items.GEMS_EMERALD)
				.addCriterion("has_blue_leather", hasItem(BLUE_LEATHER.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(COMPANION_HEARTHSTONE.get())
				.patternLine("111")
				.patternLine("121")
				.patternLine("1 1")
				.key('1', BLUE_LEATHER.get())
				.key('2', HEARTHSTONE.get())
				.addCriterion("has_blue_leather", hasItem(BLUE_LEATHER.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(COMPANION_STONE.get())
				.patternLine(" 11")
				.patternLine("111")
				.patternLine("11 ")
				.key('1', ModTags.HEARTHSTONE_MATERIAL)
				.addCriterion("has_blue_leather", hasItem(BLUE_LEATHER.get()))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(HEARTHING_GEM.get())
				.patternLine(" 11")
				.patternLine("111")
				.patternLine("11 ")
				.key('1', ModTags.GEMS_AQUAMARINE)
				.addCriterion("has_aquamarine", hasItem(ModTags.GEMS_AQUAMARINE))
				.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(MORTAR.get())
				.patternLine("  3")
				.patternLine("121")
				.patternLine("111")
				.key('1', Tags.Items.STONE)
				.key('2', Tags.Items.INGOTS_IRON)
				.key('3', Tags.Items.NUGGETS_IRON)
				.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
				.build(consumer);

		// Shapeless Recipe
		ShapelessRecipeBuilder.shapelessRecipe(AQUAMARINE.get(), 9)
				.addIngredient(AQUAMARINE_BLOCK.get())
				.addCriterion("has_aquamarine_block", hasItem(AQUAMARINE_BLOCK.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(BLUE_LEATHER.get())
				.addIngredient(ModTags.DUSTS_DEEP_BLUE)
				.addIngredient(ModTags.DUSTS_DEEP_BLUE)
				.addIngredient(ModTags.DUSTS_DEEP_BLUE)
				.addIngredient(ModTags.DUSTS_DEEP_BLUE)
				.addIngredient(Tags.Items.LEATHER)
				.addIngredient(Items.MILK_BUCKET)
				.addCriterion("has_deep_blue", hasItem(DEEP_BLUE.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(DEEP_BLUE.get(), 3)
				.addIngredient(ModTags.DUSTS_AQUAMARINE)
				.addIngredient(ModTags.DUSTS_DIAMOND)
				.addIngredient(ModTags.DUSTS_LAPIS)
				.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(RED_LEATHER.get())
				.addIngredient(Tags.Items.DYES_RED)
				.addIngredient(Tags.Items.DYES_RED)
				.addIngredient(Tags.Items.DYES_RED)
				.addIngredient(Tags.Items.LEATHER)
				.addIngredient(Items.WATER_BUCKET)
				.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
				.build(consumer);

		ShapelessRecipeBuilder.shapelessRecipe(TAVERN.get())
				.addIngredient(TAVERNCLOTH.get())
				.addIngredient(ItemTags.LOGS)
				.addCriterion("has_taverncloth", hasItem(TAVERNCLOTH.get()))
				.build(consumer, new ResourceLocation(MODID, "tavern_from_taverncloth"));

		addMortarRecipe(Ingredient.fromTag(ModTags.GEMS_AQUAMARINE), AQUAMARINE_DUST.get(), consumer);
		addMortarRecipe(Ingredient.fromTag(Tags.Items.GEMS_DIAMOND), DIAMOND_DUST.get(), consumer);
		addMortarRecipe(Ingredient.fromTag(Tags.Items.GEMS_LAPIS), LAPIS_DUST.get(), consumer);

		// Easy Mode
		EasyModeCondition easyModeCondition = new EasyModeCondition();
		ConditionalRecipe.builder()
				.addCondition(easyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(HEARTHSTONE.get())
						.patternLine(" 11")
						.patternLine("121")
						.patternLine("11 ")
						.key('1', ModTags.HEARTHSTONE_MATERIAL)
						.key('2', ModTags.GEMS_AQUAMARINE)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "easy_mode/hearthstone");

		ConditionalRecipe.builder()
				.addCondition(easyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(TAVERN.get())
						.patternLine("111")
						.patternLine("121")
						.patternLine("131")
						.key('1', RED_LEATHER.get())
						.key('2', ModTags.GEMS_AQUAMARINE)
						.key('3', ItemTags.LOGS)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "easy_mode/tavern");

		ConditionalRecipe.builder()
				.addCondition(easyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(TAVERNCLOTH.get())
						.patternLine("111")
						.patternLine("121")
						.patternLine("1 1")
						.key('1', RED_LEATHER.get())
						.key('2', ModTags.GEMS_AQUAMARINE)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "easy_mode/taverncloth");

		// Not Easy Mode
		NotCondition notEasyModeCondition = new NotCondition(easyModeCondition);

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(HEARTHSTONE.get())
						.patternLine(" 11")
						.patternLine("121")
						.patternLine("11 ")
						.key('1', ModTags.HEARTHSTONE_MATERIAL)
						.key('2', ModTags.DUSTS_DEEP_BLUE)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/hearthstone");
		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(SHABBY_TAVERN.get())
						.patternLine("111")
						.patternLine("121")
						.patternLine("131")
						.key('1', TATTERED_LEATHER.get())
						.key('2', ModTags.GEMS_AQUAMARINE)
						.key('3', ItemTags.LOGS)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/shabby_tavern");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapelessRecipeBuilder.shapelessRecipe(SHABBY_TAVERN.get())
						.addIngredient(TATTERED_TAVERNCLOTH.get())
						.addIngredient(ItemTags.LOGS)
						.addCriterion("has_tattered_taverncloth", hasItem(TATTERED_TAVERNCLOTH.get()))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/shabby_tavern_from_taverncloth");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapelessRecipeBuilder.shapelessRecipe(TATTERED_LEATHER.get(), 7)
						.addIngredient(Items.BONE_MEAL)
						.addIngredient(Items.BONE_MEAL)
						.addIngredient(Items.BONE_MEAL)
						.addIngredient(Tags.Items.LEATHER)
						.addIngredient(Items.WATER_BUCKET)
						.addCriterion("has_leather", hasItem(Tags.Items.LEATHER))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/tattered_leather");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapelessRecipeBuilder.shapelessRecipe(TATTERED_LEATHER.get())
						.addIngredient(Items.BONE_MEAL)
						.addIngredient(Ingredient.fromItems(Items.GRASS, Items.FERN))
						.addIngredient(Ingredient.fromItems(Items.GRASS, Items.FERN))
						.addIngredient(Ingredient.fromItems(Items.GRASS, Items.FERN))
						.addIngredient(Items.WATER_BUCKET)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/tattered_leather_from_grass");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(TATTERED_TAVERNCLOTH.get())
						.patternLine("111")
						.patternLine("121")
						.patternLine("1 1")
						.key('1', TATTERED_LEATHER.get())
						.key('2', ModTags.GEMS_AQUAMARINE)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/tattered_taverncloth");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(TAVERN.get())
						.patternLine("111")
						.patternLine("121")
						.patternLine("131")
						.key('1', RED_LEATHER.get())
						.key('2', ModTags.STORAGE_BLOCKS_AQUAMARINE)
						.key('3', ItemTags.LOGS)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/tavern");

		ConditionalRecipe.builder()
				.addCondition(notEasyModeCondition).addRecipe(c ->
				ShapedRecipeBuilder.shapedRecipe(TAVERNCLOTH.get())
						.patternLine("111")
						.patternLine("121")
						.patternLine("1 1")
						.key('1', RED_LEATHER.get())
						.key('2', ModTags.STORAGE_BLOCKS_AQUAMARINE)
						.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()))
						.build(c))
				.build(consumer, MODID, "not_easy_mode/taverncloth");

		// Furnace Recipe
		Consumer<IFinishedRecipe> c = result -> consumer.accept(result instanceof CookingRecipeBuilder.Result ? new MultiItemCookingResult((CookingRecipeBuilder.Result)result, 9) : result);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(AQUAMARINE_ORE.get()), AQUAMARINE.get(), 0.5f, 200)
				.addCriterion("has_aquamarine_ore", hasItem(AQUAMARINE_ORE.get()))
				.build(c, new ResourceLocation(MODID, "smelting/aquamarine"));
		CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(AQUAMARINE_ORE.get()), AQUAMARINE.get(), 0.5f, 100)
				.addCriterion("has_aquamarine_ore", hasItem(AQUAMARINE_ORE.get()))
				.build(c, new ResourceLocation(MODID, "smelting/aquamarine_blasting"));
	}

	private void addMortarRecipe(Ingredient in, IItemProvider out, Consumer<IFinishedRecipe> consumer){
		addMortarRecipe(in, out, null, consumer);
	}

	private void addMortarRecipe(Ingredient in, IItemProvider out, @Nullable String save, Consumer<IFinishedRecipe> consumer){
		ShapelessRecipeBuilder b = ShapelessRecipeBuilder.shapelessRecipe(out)
				.addIngredient(MORTAR.get())
				.addIngredient(in)
				.addCriterion("has_aquamarine", hasItem(AQUAMARINE.get()));
		if(save!=null) b.build(consumer, new ResourceLocation(MODID, save));
		else b.build(consumer);
	}
}
