package tictim.hearthstones.contents.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;

public final class TavernSkinIngredient extends Ingredient{
	private static final TavernSkinIngredient INSTANCE = new TavernSkinIngredient();
	public static TavernSkinIngredient of(){
		return INSTANCE;
	}

	private TavernSkinIngredient(){}

	@Override public boolean apply(@Nullable ItemStack input){
		return input!=null&&TavernSkinRecipe.getSkinFromItem(input)!=null;
	}

	private ItemStack sample;
	private ItemStack getSample(){
		if(this.sample==null) this.sample = new ItemStack(Blocks.LOG);
		return this.sample;
	}

	@Override public ItemStack[] getMatchingStacks(){
		return new ItemStack[]{this.getSample()};
	}

	private IntList matchingStacks;
	@Override public IntList getValidItemStacksPacked(){
		if(this.matchingStacks==null){
			this.matchingStacks = new IntArrayList(1);
			this.matchingStacks.add(RecipeItemHelper.pack(this.getSample()));
		}

		return this.matchingStacks;
	}

	@Override protected void invalidate(){
		this.matchingStacks = null;
	}

	@Override public boolean isSimple(){
		return false;
	}
}
