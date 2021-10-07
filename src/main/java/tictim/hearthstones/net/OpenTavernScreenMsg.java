package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Accessibility;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;

public class OpenTavernScreenMsg{
	public static OpenTavernScreenMsg read(FriendlyByteBuf buf){
		return new OpenTavernScreenMsg(
				new TavernPos(buf),
				TavernType.of(buf.readByte()),
				ModNet.readOptionalName(buf),
				Accessibility.fromMeta(buf.readUnsignedByte()),
				Owner.read(buf),
				AccessModifier.of(buf.readByte()),
				buf.readBoolean()
		);
	}

	public final TavernPos pos;
	public final TavernType type;
	@Nullable public final Component name;
	public final Accessibility accessibility;
	public final Owner owner;
	public final AccessModifier access;
	public final boolean isHome;

	public OpenTavernScreenMsg(Tavern tavern, Player player, boolean isHome){
		this(tavern.pos(), tavern.type(), tavern.name(), tavern.getAccessibility(player), tavern.owner(), tavern.access(), isHome);
	}
	public OpenTavernScreenMsg(TavernPos pos, TavernType type, @Nullable Component name, Accessibility accessibility, Owner owner, AccessModifier access, boolean isHome){
		this.pos = pos;
		this.type = type;
		this.name = name;
		this.accessibility = accessibility;
		this.owner = owner;
		this.access = access;
		this.isHome = isHome;
	}

	public void write(FriendlyByteBuf buf){
		pos.write(buf);
		buf.writeByte(type.id);
		ModNet.writeOptionalName(buf, name);
		buf.writeByte(accessibility.ordinal());
		owner.write(buf);
		buf.writeByte(access.ordinal());
		buf.writeBoolean(isHome);
	}
}
