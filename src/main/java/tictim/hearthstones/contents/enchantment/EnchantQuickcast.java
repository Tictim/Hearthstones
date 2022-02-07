package tictim.hearthstones.contents.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantQuickcast extends Enchantment{
	public EnchantQuickcast(EnumEnchantmentType type){
		super(Rarity.VERY_RARE, type, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}

	@Override
	public int getMaxLevel(){
		return 4;
	}
}