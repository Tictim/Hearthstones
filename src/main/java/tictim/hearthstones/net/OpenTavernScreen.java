package tictim.hearthstones.net;

import net.minecraft.util.text.ITextComponent;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.utils.Accessibility;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;

public class OpenTavernScreen{
	@Nullable public TavernPos pos;
	public TavernType type;
	@Nullable public ITextComponent name;
	public Accessibility access;
	public Owner owner;
	public boolean isHome;

	public OpenTavernScreen(@Nullable TavernPos pos, TavernType type, @Nullable ITextComponent name, Accessibility access, Owner owner, boolean isHome){
		this.pos = pos;
		this.type = type;
		this.name = name;
		this.access = access;
		this.owner = owner;
		this.isHome = isHome;
	}
}
