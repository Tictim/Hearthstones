package tictim.hearthstones.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class MultiItemCookingResult implements IFinishedRecipe{
	private final CookingRecipeBuilder.Result resultDelegate;
	private final int count;

	public MultiItemCookingResult(CookingRecipeBuilder.Result resultDelegate, int count){
		this.resultDelegate = resultDelegate;
		this.count = count;
	}

	@Override public void serializeRecipeData(JsonObject json){
		resultDelegate.serializeRecipeData(json);
		JsonObject o = new JsonObject();
		o.add("item", json.get("result"));
		o.addProperty("count", count);
		json.add("result", o);
	}

	@Override public IRecipeSerializer<?> getType(){return resultDelegate.getType();}
	@Override public ResourceLocation getId(){return resultDelegate.getId();}
	@Override @Nullable public JsonObject serializeAdvancement(){return resultDelegate.serializeAdvancement();}
	@Override @Nullable public ResourceLocation getAdvancementId(){return resultDelegate.getAdvancementId();}
}
