package tictim.hearthstones.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantQuickcast extends Enchantment{
	public EnchantQuickcast(EnchantmentCategory type){
		super(Rarity.VERY_RARE, type, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
	}

	@Override
	public int getMaxLevel(){
		return 4;
	}
}
