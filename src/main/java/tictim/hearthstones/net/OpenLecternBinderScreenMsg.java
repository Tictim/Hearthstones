package tictim.hearthstones.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import tictim.hearthstones.tavern.TavernMemory;

public record OpenLecternBinderScreenMsg(
		BlockPos lecternPos,
		TavernMemory memory,
		int waypoints
){
	public static OpenLecternBinderScreenMsg read(FriendlyByteBuf buf){
		BlockPos pos = buf.readBlockPos();
		TavernMemory memory = new TavernMemory();
		memory.read(buf);
		return new OpenLecternBinderScreenMsg(pos, memory, buf.readVarInt());
	}

	public void write(FriendlyByteBuf buf){
		buf.writeBlockPos(lecternPos);
		memory.write(buf);
		buf.writeVarInt(waypoints);
	}
}
