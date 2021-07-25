package tictim.hearthstones.logic;

import net.minecraft.world.entity.EquipmentSlot;
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
				ctx.getPlayer().broadcastBreakEvent(ctx.getHand());
				ctx.getStack().shrink(1);
				ctx.getPlayer().awardStat(Stats.ITEM_BROKEN.get(ctx.getStack().getItem()));
				ctx.getStack().setDamageValue(0);
			}else ctx.getStack().hurtAndBreak(1, ctx.getPlayer(), player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
	}
}
