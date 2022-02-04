package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import tictim.hearthstones.tavern.TavernPos;

public record RemoveBinderWaypointMsg(int binderInventoryPosition, TavernPos pos){
	public static RemoveBinderWaypointMsg read(FriendlyByteBuf buf){
		return new RemoveBinderWaypointMsg(buf.readVarInt(), TavernPos.read(buf));
	}

	public void write(FriendlyByteBuf buf){
		buf.writeVarInt(binderInventoryPosition);
		pos().write(buf);
	}
}
