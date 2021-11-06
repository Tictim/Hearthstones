package tictim.hearthstones.hearthstone;

import net.minecraft.network.chat.TranslatableComponent;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.tavern.Tavern;

import javax.annotation.Nullable;

import static tictim.hearthstones.hearthstone.HearthUtils.getTavernAt;

public class HearthingPlanksHearthstone extends ConfigurableHearthstone{
	public HearthingPlanksHearthstone(){
		super(ModCfg.hearthingPlanks());
	}

	@Nullable @Override public Tavern previewWarp(WarpContext context){
		return context.getMemory().getHomeTavern();
	}

	@Nullable @Override public WarpSetup setupWarp(WarpContext context){
		Tavern homeTavern = context.getMemory().getHomeTavern();
		if(homeTavern==null){
			context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthing_planks.no_home_tavern"), true);
			return null;
		}
		TavernBlockEntity tavern = getTavernAt(homeTavern.pos());
		if(tavern==null){
			context.getMemory().updateIfPresent(homeTavern.withMissingSet(true));
			context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.tavern_missing"), true);
		}else{
			context.getMemory().updateIfPresent(tavern.withMissingSet(false));
			if(!tavern.canTeleportTo(context))
				context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.no_permission"), true);
			return () -> {
				HearthUtils.warp(context.getPlayer(), tavern.pos().dim(), HearthUtils.getWarpPos(tavern), true);
				context.hurtItem(1);
				context.getMemory().addOrUpdate(homeTavern);
				context.getMemory().setCooldown(config.cooldown());
			};
		}
		return null;
	}
}
