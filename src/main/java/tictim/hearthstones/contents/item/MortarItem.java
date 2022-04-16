package tictim.hearthstones.contents.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import tictim.hearthstones.contents.ModTags;

import java.util.Random;

public class MortarItem extends Item{
	private static final Random RNG = new Random();

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
		if(copy.hurt(1, RNG, null)){
			copy.shrink(1);
			copy.setDamageValue(0);
		}
		return copy;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.has(Tags.Items.INGOTS_IRON, repair.getItem());
	}
}
