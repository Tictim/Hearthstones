package tictim.hearthstones.logic;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.utils.HearthingContext;

import javax.annotation.Nullable;

public class HearthingPlanksHearthstone extends BaseHearthstone{
	public HearthingPlanksHearthstone(){
		super(ModCfg.hearthingPlanks);
	}

	@Nullable @Override public TavernRecord getDestination(HearthingContext ctx){
		return ctx.getMemory().getHomeTavern();
	}
	@Override public Component invalidDestinationError(){
		return new TranslatableComponent("info.hearthstones.hearthing_planks.no_home_tavern");
	}
	@Override public Component noSelectionError(){
		return new TranslatableComponent("info.hearthstones.hearthing_planks.no_home_tavern");
	}
	@Override public Component guideText(){
		return new TranslatableComponent("info.hearthstones.hearthing_planks.help");
	}
}
