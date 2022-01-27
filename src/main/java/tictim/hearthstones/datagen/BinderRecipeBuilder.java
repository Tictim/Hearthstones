package tictim.hearthstones.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import tictim.hearthstones.contents.ModRecipes;

import javax.annotation.Nullable;

public record BinderRecipeBuilder(FinishedRecipe result) implements FinishedRecipe{
	@Override public void serializeRecipeData(JsonObject pJson){
		result.serializeRecipeData(pJson);
	}
	@Override public ResourceLocation getId(){
		return result.getId();
	}
	@Override public RecipeSerializer<?> getType(){
		return ModRecipes.BINDER_RECIPE.get();
	}
	@Nullable @Override public JsonObject serializeAdvancement(){
		return result.serializeAdvancement();
	}
	@Nullable @Override public ResourceLocation getAdvancementId(){
		return result.getAdvancementId();
	}
}
