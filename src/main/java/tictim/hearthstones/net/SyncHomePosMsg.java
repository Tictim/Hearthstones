package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;

public record SyncHomePosMsg(@Nullable TavernPos homePos){
	public static SyncHomePosMsg read(FriendlyByteBuf buf){
		return new SyncHomePosMsg(buf.readBoolean() ? TavernPos.read(buf) : null);
	}

	public void write(FriendlyByteBuf buf){
		buf.writeBoolean(homePos!=null);
		if(homePos!=null) homePos.write(buf);
	}
}
