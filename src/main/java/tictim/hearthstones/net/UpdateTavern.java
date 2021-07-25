package tictim.hearthstones.net;

import net.minecraft.network.chat.Component;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.utils.AccessModifier;

import javax.annotation.Nullable;

public class UpdateTavern{
	public TavernPos pos;
	@Nullable public Component name;
	public AccessModifier access;

	public UpdateTavern(TavernPos pos, @Nullable Component name, AccessModifier access){
		this.pos = pos;
		this.name = name;
		this.access = access;
	}
}
