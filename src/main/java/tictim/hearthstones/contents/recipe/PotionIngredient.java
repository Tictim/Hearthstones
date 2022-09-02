package tictim.hearthstones.contents.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

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

	@Nullable private List<ItemStack> matchingStacks;
	@Override public ItemStack[] getMatchingStacks(){
		if(matchingStacks==null)
			matchingStacks = ForgeRegistries.POTION_TYPES.getValuesCollection().stream()
					.filter(p -> !p.getEffects().isEmpty())
					.map(p -> PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), p))
					.collect(Collectors.toList());
		return matchingStacks.toArray(new ItemStack[0]);
	}

	@Nullable private IntList matchingStacksPacked;
	@Override public IntList getValidItemStacksPacked(){
		if(this.matchingStacksPacked==null){
			this.matchingStacksPacked = new IntArrayList(1);
			this.matchingStacksPacked.add(RecipeItemHelper.pack(new ItemStack(Items.POTIONITEM)));
		}
		return this.matchingStacksPacked;
	}

	@Override protected void invalidate(){
		this.matchingStacks = null;
		this.matchingStacksPacked = null;
	}

	@Override public boolean isSimple(){
		return false;
	}
}
