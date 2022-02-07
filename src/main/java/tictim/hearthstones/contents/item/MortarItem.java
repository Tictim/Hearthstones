package tictim.hearthstones.contents.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tictim.hearthstones.contents.ModOreDict;

public class MortarItem extends Item{
	private static final int MAX_DAMAGE = 155;

	public MortarItem(){
		this.setMaxDamage(MAX_DAMAGE).setMaxStackSize(1);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack){
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack){
		ItemStack copy = stack.copy();
		if(copy.attemptDamageItem(1, itemRand, null)){
			copy.shrink(1);
			copy.setItemDamage(0);
		}
		return copy;
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
		return OreDictionary.containsMatch(false, OreDictionary.getOres(ModOreDict.INGOT_IRON), repair);
	}
	@Override public boolean showDurabilityBar(ItemStack stack){
		return isDamaged(stack);
	}
	@Override public double getDurabilityForDisplay(ItemStack stack){
		return stack.getItemDamage()/155.0;
	}
}
