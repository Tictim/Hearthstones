package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;

public class UpdateTavernMsg implements IMessage{
	private TavernPos pos;
	@Nullable private String name;
	private AccessModifier access;

	public UpdateTavernMsg(TavernPos pos, @Nullable String name, AccessModifier access){
		this.pos = pos;
		this.name = name;
		this.access = access;
	}
	public UpdateTavernMsg(){
		this.pos = TavernPos.ORIGIN;
		this.name = null;
		this.access = AccessModifier.PUBLIC;
	}

	public TavernPos getPos(){
		return pos;
	}
	@Nullable public String getName(){
		return name;
	}
	public AccessModifier getAccess(){
		return access;
	}

	@Override public void fromBytes(ByteBuf buf){
		this.pos = TavernPos.read(buf);
		this.name = buf.readBoolean() ? ByteBufUtils.readUTF8String(buf) : null;
		this.access = AccessModifier.of(buf.readUnsignedByte());
	}
	@Override public void toBytes(ByteBuf buf){
		pos.write(buf);
		buf.writeBoolean(name!=null);
		if(name!=null) ByteBufUtils.writeUTF8String(buf, name);
		buf.writeByte(access.ordinal());
	}
}
