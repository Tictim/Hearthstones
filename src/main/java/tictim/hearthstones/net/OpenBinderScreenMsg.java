package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.TavernMemory;

public class OpenBinderScreenMsg implements IMessage{
	private int binderInventoryPosition;
	private final TavernMemory memory;
	private int blankWaypoints;
	private boolean infiniteWaypoints;

	public OpenBinderScreenMsg(int binderInventoryPosition, TavernMemory memory, int blankWaypoints, boolean infiniteWaypoints){
		this.binderInventoryPosition = binderInventoryPosition;
		this.memory = memory;
		this.blankWaypoints = blankWaypoints;
		this.infiniteWaypoints = infiniteWaypoints;
	}
	public OpenBinderScreenMsg(){
		this.binderInventoryPosition = 0;
		this.memory = new TavernMemory();
		this.blankWaypoints = 0;
	}

	public int getBinderInventoryPosition(){
		return binderInventoryPosition;
	}
	public TavernMemory getMemory(){
		return memory;
	}
	public int getBlankWaypoints(){
		return blankWaypoints;
	}
	public boolean isInfiniteWaypoints(){
		return infiniteWaypoints;
	}

	@Override public void fromBytes(ByteBuf buf){
		this.binderInventoryPosition = buf.readInt();
		this.memory.read(buf);
		this.blankWaypoints = buf.readInt();
		this.infiniteWaypoints = buf.readBoolean();
	}
	@Override public void toBytes(ByteBuf buf){
		buf.writeInt(binderInventoryPosition);
		this.memory.write(buf);
		buf.writeInt(blankWaypoints);
		buf.writeBoolean(infiniteWaypoints);
	}
}
