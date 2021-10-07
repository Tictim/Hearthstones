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
		Tavern selectedTavern = context.getMemory().getSelectedTavern();
		if(selectedTavern==null){
			context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthing_planks.no_home_tavern"), true);
			return null;
		}
		TavernBlockEntity tavern = getTavernAt(selectedTavern.pos());
		if(tavern==null){
			context.getMemory().addOrUpdate(selectedTavern.withMissingSet(true));
			context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.tavern_missing"), true);
		}else{
			context.getMemory().addOrUpdate(tavern.withMissingSet(false));
			if(!tavern.canTeleportTo(context))
				context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.no_permission"), true);
			return () -> {
				HearthUtils.warp(context.getPlayer(), tavern.pos().dim, HearthUtils.getWarpPos(tavern), true);
				context.getStack().hurtAndBreak(1,
						context.getPlayer(),
						player -> {
							if(context.getHand()!=null) player.broadcastBreakEvent(context.getHand());
						});
				context.getMemory().setCooldown(config.cooldown());
			};
		}
		context.getMemory().sync();
		return null;
	}
}
