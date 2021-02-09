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

	@Override public void serialize(JsonObject json){
		resultDelegate.serialize(json);
		JsonObject o = new JsonObject();
		o.add("item", json.get("result"));
		o.addProperty("count", count);
		json.add("result", o);
	}

	@Override public IRecipeSerializer<?> getSerializer(){return resultDelegate.getSerializer();}
	@Override public ResourceLocation getID(){return resultDelegate.getID();}
	@Override @Nullable public JsonObject getAdvancementJson(){return resultDelegate.getAdvancementJson();}
	@Override @Nullable public ResourceLocation getAdvancementID(){return resultDelegate.getAdvancementID();}
}
