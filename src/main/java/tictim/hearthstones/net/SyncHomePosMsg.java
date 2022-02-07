package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;


public class SyncHomePosMsg implements IMessage{
	@Nullable private TavernPos homePos;

	public SyncHomePosMsg(@Nullable TavernPos homePos){
		this.homePos = homePos;
	}
	public SyncHomePosMsg(){
		this.homePos = null;
	}

	@Nullable public TavernPos getHomePos(){
		return homePos;
	}

	@Override public void fromBytes(ByteBuf buf){
		this.homePos = buf.readBoolean() ? TavernPos.read(buf) : null;
	}
	@Override public void toBytes(ByteBuf buf){
		buf.writeBoolean(homePos!=null);
		if(homePos!=null) homePos.write(buf);
	}
}