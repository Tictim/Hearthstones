package tictim.hearthstones.logic;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
	@Override public ITextComponent invalidDestinationError(){
		return new TranslationTextComponent("info.hearthstones.hearthing_planks.no_home_tavern");
	}
	@Override public ITextComponent noSelectionError(){
		return new TranslationTextComponent("info.hearthstones.hearthing_planks.no_home_tavern");
	}
	@Override public ITextComponent guideText(){
		return new TranslationTextComponent("info.hearthstones.hearthing_planks.help");
	}
}
