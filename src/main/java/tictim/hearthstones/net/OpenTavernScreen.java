package tictim.hearthstones.net;

import net.minecraft.network.chat.Component;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.utils.Accessibility;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;

public class OpenTavernScreen{
	public TavernPos pos;
	public TavernType type;
	@Nullable public Component name;
	public Accessibility access;
	public Owner owner;
	public boolean isHome;

	public OpenTavernScreen(TavernPos pos, TavernType type, @Nullable Component name, Accessibility access, Owner owner, boolean isHome){
		this.pos = pos;
		this.type = type;
		this.name = name;
		this.access = access;
		this.owner = owner;
		this.isHome = isHome;
	}
}
