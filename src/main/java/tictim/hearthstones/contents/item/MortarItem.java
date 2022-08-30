package tictim.hearthstones.contents.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import tictim.hearthstones.contents.ModTags;

public class MortarItem extends Item{
	private static final RandomSource RNG = RandomSource.create();

	public MortarItem(Properties properties){
		super(properties);
	}

	@Override public ItemStack getCraftingRemainingItem(ItemStack stack){
		ItemStack copy = stack.copy();
		if(copy.hurt(1, RNG, null)){
			copy.shrink(1);
			copy.setDamageValue(0);
		}
		return copy;
	}
	@Override public boolean hasCraftingRemainingItem(ItemStack stack){
		return true;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair){
		return ModTags.has(Tags.Items.INGOTS_IRON, repair.getItem());
	}
}
