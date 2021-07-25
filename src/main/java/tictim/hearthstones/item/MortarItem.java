package tictim.hearthstones.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Tags;

public class MortarItem extends Item{
	public MortarItem(Properties properties){
		super(properties);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack){
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack){
		ItemStack copy = stack.copy();
		if(copy.hurt(1, Item.random, null)){
			copy.shrink(1);
			copy.setDamageValue(0);
		}
		return copy;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return Tags.Items.INGOTS_IRON.contains(repair.getItem());
	}
}
