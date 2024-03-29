package datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;

public record MultiItemCookingResult(SimpleCookingRecipeBuilder.Result resultDelegate, int count) implements FinishedRecipe{
	@Override public void serializeRecipeData(JsonObject json){
		resultDelegate.serializeRecipeData(json);
		JsonObject o = new JsonObject();
		o.add("item", json.get("result"));
		o.addProperty("count", count);
		json.add("result", o);
	}

	@Override public RecipeSerializer<?> getType(){return resultDelegate.getType();}
	@Override public ResourceLocation getId(){return resultDelegate.getId();}
	@Override @Nullable public JsonObject serializeAdvancement(){return resultDelegate.serializeAdvancement();}
	@Override @Nullable public ResourceLocation getAdvancementId(){return resultDelegate.getAdvancementId();}
}
