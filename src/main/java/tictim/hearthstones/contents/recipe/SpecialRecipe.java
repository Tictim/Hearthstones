package tictim.hearthstones.contents.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class SpecialRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe{
	protected abstract int minimumSize();
	protected abstract ItemStack createRecipeOutput();
	protected abstract Ingredient[] createIngredients();

	@Override public boolean isDynamic(){
		return true;
	}

	@Override public boolean canFit(int width, int height){
		return width*height>=minimumSize();
	}
	private ItemStack recipeOutput;
	@Override public ItemStack getRecipeOutput(){
		if(recipeOutput==null) recipeOutput = createRecipeOutput();
		return recipeOutput;
	}
	private NonNullList<Ingredient> ingredients;
	@Override public NonNullList<Ingredient> getIngredients(){
		if(ingredients==null) ingredients = NonNullList.from(Ingredient.EMPTY, createIngredients());
		return ingredients;
	}
}
