package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;

public class UpdateTavernMsg{
	public static UpdateTavernMsg read(FriendlyByteBuf buf){
		return new UpdateTavernMsg(new TavernPos(buf),
				ModNet.readOptionalName(buf),
				AccessModifier.of(buf.readUnsignedByte()));
	}

	public TavernPos pos;
	@Nullable public Component name;
	public AccessModifier access;

	public UpdateTavernMsg(TavernPos pos, @Nullable Component name, AccessModifier access){
		this.pos = pos;
		this.name = name;
		this.access = access;
	}

	public void write(FriendlyByteBuf buf){
		pos.write(buf);
		ModNet.writeOptionalName(buf, name);
		buf.writeByte(access.ordinal());
	}
}
