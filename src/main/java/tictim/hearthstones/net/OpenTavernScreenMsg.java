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

public record OpenTavernScreenMsg(
		TavernPos pos,
		TavernType type,
		@Nullable String name,
		Accessibility accessibility,
		Owner owner,
		AccessModifier access,
		boolean isHome
){
	public static OpenTavernScreenMsg read(FriendlyByteBuf buf){
		return new OpenTavernScreenMsg(
				TavernPos.read(buf),
				TavernType.of(buf.readByte()),
				buf.readBoolean() ? buf.readUtf() : null,
				Accessibility.fromMeta(buf.readUnsignedByte()),
				Owner.read(buf),
				AccessModifier.of(buf.readByte()),
				buf.readBoolean()
		);
	}

	public OpenTavernScreenMsg(Tavern tavern, Player player, boolean isHome){
		this(tavern.pos(), tavern.type(), tavern.name(), tavern.getAccessibility(player), tavern.owner(), tavern.access(), isHome);
	}

	public void write(FriendlyByteBuf buf){
		pos.write(buf);
		buf.writeByte(type.id);
		buf.writeBoolean(name!=null);
		if(name!=null) buf.writeUtf(name);
		buf.writeByte(accessibility.ordinal());
		owner.write(buf);
		buf.writeByte(access.ordinal());
		buf.writeBoolean(isHome);
	}
}
