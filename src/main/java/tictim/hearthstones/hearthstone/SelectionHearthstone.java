package tictim.hearthstones.hearthstone;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import tictim.hearthstones.config.HearthstoneConfig;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.tavern.Tavern;

import javax.annotation.Nullable;

import static tictim.hearthstones.hearthstone.HearthUtils.getTavernAt;
import static tictim.hearthstones.hearthstone.HearthUtils.warp;

public class SelectionHearthstone extends ConfigurableHearthstone{
	public SelectionHearthstone(HearthstoneConfig type){
		super(type);
	}

	@Nullable @Override public Tavern previewWarp(WarpContext context){
		return context.getSelectedTavern();
	}

	@Nullable @Override public WarpSetup setupWarp(WarpContext context){
		Tavern selectedTavern = context.getSelectedTavern();
		if(selectedTavern==null){
			context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.no_selected"), true);
			return null;
		}
		TavernBlockEntity tavern = getTavernAt(selectedTavern.pos());
		if(tavern==null){
			context.getMemory().updateIfPresent(selectedTavern.withMissingSet(true));
			context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.tavern_missing"), true);
		}else{
			context.getMemory().updateIfPresent(tavern.withMissingSet(false));
			if(tavern.canTeleportTo(context))
				return createWarpSetup(context, selectedTavern, HearthUtils.getWarpPos(tavern));
			context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.no_permission"), true);
		}
		return null;
	}

	protected WarpSetup createWarpSetup(WarpContext context, Tavern selectedTavern, BlockPos warpPos){
		return () -> {
			warp(context.getPlayer(), selectedTavern.pos().dim(), warpPos, true);
			context.hurtItem(1);
			context.getMemory().addOrUpdate(selectedTavern);
			context.getMemory().setCooldown(config.cooldown());
		};
	}
}
