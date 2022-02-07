package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.TavernPos;

public final class TavernMemoryOperationMsg implements IMessage{
	public static final byte SELECT = 1;
	public static final byte DELETE = 2;
	public static final byte SET_HOME = 3;

	private TavernPos pos;
	private byte operation;

	public TavernMemoryOperationMsg(TavernPos pos, byte operation){
		this.pos = pos;
		this.operation = operation;
	}
	public TavernMemoryOperationMsg(){
		this.pos = TavernPos.ORIGIN;
		this.operation = 0;
	}

	public TavernPos getPos(){
		return pos;
	}
	public byte getOperation(){
		return operation;
	}

	@Override public void fromBytes(ByteBuf buf){
		this.pos = TavernPos.read(buf);
		this.operation = buf.readByte();
	}
	@Override public void toBytes(ByteBuf buf){
		pos.write(buf);
		buf.writeByte(operation);
	}
}
