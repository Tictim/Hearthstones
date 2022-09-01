package tictim.hearthstones.contents.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

public class ShapelessPotionRecipe extends ShapelessOreRecipe{
	public ShapelessPotionRecipe(ResourceLocation group, Block result, Object... recipe){
		super(group, result, recipe);
	}
	public ShapelessPotionRecipe(ResourceLocation group, Item result, Object... recipe){
		super(group, result, recipe);
	}
	public ShapelessPotionRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result){
		super(group, input, result);
	}
	public ShapelessPotionRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe){
		super(group, result, recipe);
	}

	@Override public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){
		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for(int i = 0; i<ret.size(); i++){
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty()&&stack.getItem()==Items.POTIONITEM){
				ret.set(i, new ItemStack(Items.GLASS_BOTTLE));
			}else ret.set(i, ForgeHooks.getContainerItem(stack));
		}
		return ret;
	}
}
