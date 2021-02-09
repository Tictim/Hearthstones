package tictim.hearthstones.logic;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import tictim.hearthstones.config.HearthstoneConfig;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.utils.HearthingContext;

import javax.annotation.Nullable;

public class GuiHearthstone extends BaseHearthstone{
	public GuiHearthstone(HearthstoneConfig type){
		super(type);
	}

	@Nullable @Override public TavernRecord getDestination(HearthingContext ctx){
		return ctx.getMemory().getSelectedTavern();
	}
	@Override public ITextComponent invalidDestinationError(){
		return new TranslationTextComponent("info.hearthstones.hearthstone.tavern_missing");
	}
	@Override public ITextComponent noSelectionError(){
		return new TranslationTextComponent("info.hearthstones.hearthstone.no_selected");
	}
	@Override public ITextComponent guideText(){
		return new TranslationTextComponent("info.hearthstones.hearthstone.help");
	}
}
