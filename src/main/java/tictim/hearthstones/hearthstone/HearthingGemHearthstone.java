package tictim.hearthstones.hearthstone;

import net.minecraft.entity.Entity;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;

import static tictim.hearthstones.hearthstone.HearthUtils.warp;

public class HearthingGemHearthstone extends SelectionHearthstone{
	public HearthingGemHearthstone(){
		super(ModCfg.hearthstones.hearthingGem);
	}

	protected WarpSetup createWarpSetup(WarpContext context, Tavern selectedTavern, BlockPos warpPos){
		return () -> {
			boolean thresholdExceeded = !context.getPlayer().isCreative()&&isTooFar(context.getPlayer(), selectedTavern.pos());
			warp(context.getPlayer(), selectedTavern.pos().dim(), warpPos, true);
			if(thresholdExceeded){
				// ItemStack#damageItem

				context.getPlayer().renderBrokenItemStack(context.getStack());
				context.getStack().shrink(1);

				//noinspection ConstantConditions // nullable actually
				context.getPlayer().addStat(StatList.getObjectBreakStats(context.getStack().getItem()));

				context.getStack().setItemDamage(0);
				context.getStack().setItemDamage(context.getStack().getMaxDamage());
			}else context.hurtItem(1);
			context.getMemory().addOrUpdate(selectedTavern);
			context.getMemory().setCooldown(config.cooldown());
		};
	}

	public static boolean isTooFar(Entity entity, TavernPos destination){
		return !destination.isSameDimension(entity.world)||
				Math.sqrt(entity.getDistanceSq(destination.pos().getX()+.5, destination.pos().getY(), destination.pos().getZ()+.5))>ModCfg.hearthstones.hearthingGem.travelDistanceThreshold;
	}
}
