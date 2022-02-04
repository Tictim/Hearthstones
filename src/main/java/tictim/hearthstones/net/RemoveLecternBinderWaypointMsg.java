package tictim.hearthstones.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import tictim.hearthstones.tavern.TavernPos;

public record RemoveLecternBinderWaypointMsg(BlockPos lecternPos, TavernPos tavernPos){
	public static RemoveLecternBinderWaypointMsg read(FriendlyByteBuf buf){
		return new RemoveLecternBinderWaypointMsg(buf.readBlockPos(), TavernPos.read(buf));
	}

	public void write(FriendlyByteBuf buf){
		buf.writeBlockPos(lecternPos);
		tavernPos().write(buf);
	}
}
