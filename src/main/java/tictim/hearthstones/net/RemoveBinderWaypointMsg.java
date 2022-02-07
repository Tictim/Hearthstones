package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.TavernPos;

public class RemoveBinderWaypointMsg implements IMessage{
	private int binderInventoryPosition;
	private TavernPos pos;

	public RemoveBinderWaypointMsg(int binderInventoryPosition, TavernPos pos){
		this.binderInventoryPosition = binderInventoryPosition;
		this.pos = pos;
	}
	public RemoveBinderWaypointMsg(){
		this.binderInventoryPosition = 0;
		this.pos = TavernPos.ORIGIN;
	}

	public int getBinderInventoryPosition(){
		return binderInventoryPosition;
	}
	public TavernPos getPos(){
		return pos;
	}

	@Override public void fromBytes(ByteBuf buf){
		this.binderInventoryPosition = buf.readInt();
		this.pos = TavernPos.read(buf);
	}
	@Override public void toBytes(ByteBuf buf){
		buf.writeInt(binderInventoryPosition);
		pos.write(buf);
	}
}
