package tictim.hearthstones.contents.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

import javax.annotation.Nullable;

public final class PotionIngredient extends Ingredient{
	private static final PotionIngredient INSTANCE = new PotionIngredient();
	public static PotionIngredient of(){
		return INSTANCE;
	}

	private PotionIngredient(){}

	@Override public boolean apply(@Nullable ItemStack input){
		if(input==null||input.getItem()!=Items.POTIONITEM) return false;
		PotionType potionType = PotionUtils.getPotionFromItem(input);
		return !potionType.getEffects().isEmpty();
	}

	private ItemStack sample;
	private ItemStack getSample(){
		if(this.sample==null) this.sample = new ItemStack(Items.POTIONITEM);
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
