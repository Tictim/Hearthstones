package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;

public record UpdateTavernMsg(TavernPos pos, @Nullable String name, AccessModifier access){
	public static UpdateTavernMsg read(FriendlyByteBuf buf){
		return new UpdateTavernMsg(TavernPos.read(buf),
				buf.readBoolean() ? buf.readUtf() : null,
				AccessModifier.of(buf.readUnsignedByte()));
	}

	public void write(FriendlyByteBuf buf){
		pos.write(buf);
		buf.writeBoolean(name!=null);
		if(name!=null) buf.writeUtf(name);
		buf.writeByte(access.ordinal());
	}
}
