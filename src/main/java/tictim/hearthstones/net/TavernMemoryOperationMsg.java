package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import tictim.hearthstones.tavern.TavernPos;

public record TavernMemoryOperationMsg(TavernPos pos, byte operation){
	public static TavernMemoryOperationMsg read(FriendlyByteBuf buf){
		return new TavernMemoryOperationMsg(
				new TavernPos(buf),
				buf.readByte());
	}

	public static final byte SELECT = 1;
	public static final byte DELETE = 2;
	public static final byte SET_HOME = 3;

	public void write(FriendlyByteBuf buf){
		pos().write(buf);
		buf.writeByte(operation());
	}
}
