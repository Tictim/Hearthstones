package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import tictim.hearthstones.tavern.TavernMemory;

public record OpenBinderScreenMsg(
		int binderInventoryPosition,
		TavernMemory memory,
		int waypoints
){
	public static OpenBinderScreenMsg read(FriendlyByteBuf buf){
		int binderInventoryPosition = buf.readVarInt();
		TavernMemory memory = new TavernMemory();
		memory.read(buf);
		return new OpenBinderScreenMsg(binderInventoryPosition, memory, buf.readVarInt());
	}

	public void write(FriendlyByteBuf buf){
		buf.writeVarInt(binderInventoryPosition);
		memory.write(buf);
		buf.writeVarInt(waypoints);
	}
}
