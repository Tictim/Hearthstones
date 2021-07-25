package tictim.hearthstones.logic;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
	@Override public Component invalidDestinationError(){
		return new TranslatableComponent("info.hearthstones.hearthstone.tavern_missing");
	}
	@Override public Component noSelectionError(){
		return new TranslatableComponent("info.hearthstones.hearthstone.no_selected");
	}
	@Override public Component guideText(){
		return new TranslatableComponent("info.hearthstones.hearthstone.help");
	}
}
