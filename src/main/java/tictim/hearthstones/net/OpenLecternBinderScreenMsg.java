package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.TavernMemory;

public class OpenLecternBinderScreenMsg implements IMessage{
	private BlockPos lecternPos;
	private final TavernMemory memory;
	private int blankWaypoints;
	private boolean infiniteWaypoints;

	public OpenLecternBinderScreenMsg(BlockPos lecternPos, TavernMemory memory, int blankWaypoints, boolean infiniteWaypoints){
		this.lecternPos = lecternPos;
		this.memory = memory;
		this.blankWaypoints = blankWaypoints;
		this.infiniteWaypoints = infiniteWaypoints;
	}
	public OpenLecternBinderScreenMsg(){
		this.lecternPos = BlockPos.ORIGIN;
		this.memory = new TavernMemory();
		this.blankWaypoints = 0;
		this.infiniteWaypoints = false;
	}

	public BlockPos getLecternPos(){
		return lecternPos;
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
		this.lecternPos = ByteBufIO.readPos(buf);
		this.memory.read(buf);
		this.blankWaypoints = buf.readInt();
		this.infiniteWaypoints = buf.readBoolean();
	}
	@Override public void toBytes(ByteBuf buf){
		ByteBufIO.writePos(buf, lecternPos);
		memory.write(buf);
		buf.writeInt(blankWaypoints);
		buf.writeBoolean(infiniteWaypoints);
	}
}