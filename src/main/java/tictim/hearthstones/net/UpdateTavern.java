package tictim.hearthstones.net;

import net.minecraft.util.text.ITextComponent;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.utils.AccessModifier;

import javax.annotation.Nullable;

public class UpdateTavern{
	public TavernPos pos;
	@Nullable public ITextComponent name;
	public AccessModifier access;

	public UpdateTavern(TavernPos pos, @Nullable ITextComponent name, AccessModifier access){
		this.pos = pos;
		this.name = name;
		this.access = access;
	}
}
