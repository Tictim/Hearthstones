package tictim.hearthstones.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class EnchantQuickcast extends Enchantment{
	public EnchantQuickcast(EnchantmentType type){
		super(Rarity.VERY_RARE, type, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}

	@Override
	public int getMaxLevel(){
		return 4;
	}
}
