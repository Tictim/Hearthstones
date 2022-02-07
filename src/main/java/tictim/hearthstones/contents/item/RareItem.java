package tictim.hearthstones.contents.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RareItem extends Item{
	private EnumRarity rarity = EnumRarity.COMMON;

	public Item setRarity(EnumRarity rarity){
		this.rarity = rarity;
		return this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public EnumRarity getRarity(ItemStack stack){
		return stack.isItemEnchanted() ? upper(this.rarity) : this.rarity;
	}

	public static EnumRarity upper(EnumRarity rarity){
		switch(rarity){
			case COMMON:
			case UNCOMMON:
				return EnumRarity.RARE;
			case RARE:
				return EnumRarity.EPIC;
			case EPIC:
			default:
				return rarity;
		}
	}
}
