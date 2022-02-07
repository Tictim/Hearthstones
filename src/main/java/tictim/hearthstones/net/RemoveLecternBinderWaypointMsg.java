package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.TavernPos;

public class RemoveLecternBinderWaypointMsg implements IMessage{
	private BlockPos lecternPos;
	private TavernPos tavernPos;

	public RemoveLecternBinderWaypointMsg(BlockPos lecternPos, TavernPos tavernPos){
		this.lecternPos = lecternPos;
		this.tavernPos = tavernPos;
	}
	public RemoveLecternBinderWaypointMsg(){
		this.lecternPos = BlockPos.ORIGIN;
		this.tavernPos = TavernPos.ORIGIN;
	}

	public BlockPos getLecternPos(){
		return lecternPos;
	}
	public TavernPos getTavernPos(){
		return tavernPos;
	}

	@Override public void fromBytes(ByteBuf buf){
		this.lecternPos = ByteBufIO.readPos(buf);
		this.tavernPos = TavernPos.read(buf);
	}
	@Override public void toBytes(ByteBuf buf){
		ByteBufIO.writePos(buf, lecternPos);
		this.tavernPos.write(buf);
	}
}