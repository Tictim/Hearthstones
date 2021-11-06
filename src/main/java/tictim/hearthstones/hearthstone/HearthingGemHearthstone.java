package tictim.hearthstones.hearthstone;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;

import static tictim.hearthstones.hearthstone.HearthUtils.warp;

public class HearthingGemHearthstone extends SelectionHearthstone{
	public HearthingGemHearthstone(){
		super(ModCfg.hearthingGem());
	}

	protected WarpSetup createWarpSetup(WarpContext context, Tavern selectedTavern, BlockPos warpPos){
		return () -> {
			boolean thresholdExceeded = !context.getPlayer().isCreative()&&isTooFar(context.getPlayer(), selectedTavern.pos());
			warp(context.getPlayer(), selectedTavern.pos().dim(), warpPos, true);
			if(thresholdExceeded){
				context.onItemBreak();
				context.getStack().shrink(1);
				context.getPlayer().awardStat(Stats.ITEM_BROKEN.get(context.getStack().getItem()));

				context.getStack().setDamageValue(0);
				context.getStack().setDamageValue(context.getStack().getMaxDamage());
			}else context.hurtItem(1);
			context.getMemory().addOrUpdate(selectedTavern);
			context.getMemory().setCooldown(config.cooldown());
		};
	}

	public static boolean isTooFar(Entity entity, TavernPos destination){
		return !destination.isSameDimension(entity.level)||
				Math.sqrt(entity.distanceToSqr(destination.pos().getX()+.5, destination.pos().getY(), destination.pos().getZ()+.5))>ModCfg.hearthingGem().travelDistanceThreshold();
	}
}
