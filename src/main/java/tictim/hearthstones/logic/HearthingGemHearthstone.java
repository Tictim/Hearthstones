package tictim.hearthstones.logic;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.stats.Stats;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.utils.HearthingContext;

public class HearthingGemHearthstone extends GuiHearthstone{
	public HearthingGemHearthstone(){
		super(ModCfg.hearthingGem);
	}

	@Override
	public void applyDamage(HearthingContext ctx){
		if(!ctx.getPlayer().isCreative()){
			if(ctx.getConvertedDistance()>ModCfg.hearthingGem.travelDistanceThreshold()){
				ctx.getPlayer().sendBreakAnimation(ctx.getHand());
				ctx.getStack().shrink(1);
				ctx.getPlayer().addStat(Stats.ITEM_BROKEN.get(ctx.getStack().getItem()));
				ctx.getStack().setDamage(0);
			}else ctx.getStack().damageItem(1, ctx.getPlayer(), player -> player.sendBreakAnimation(EquipmentSlotType.MAINHAND));
		}
	}
}
