package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Accessibility;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;

public class OpenTavernScreenMsg implements IMessage{
	private TavernPos pos;
	private TavernType type;
	private @Nullable String name;
	private Accessibility accessibility;
	private Owner owner;
	private AccessModifier access;
	private boolean isHome;

	public OpenTavernScreenMsg(Tavern tavern, EntityPlayer player, boolean isHome){
		this(tavern.pos(), tavern.type(), tavern.name(), tavern.getAccessibility(player), tavern.owner(), tavern.access(), isHome);
	}
	public OpenTavernScreenMsg(TavernPos pos, TavernType type, @Nullable String name, Accessibility accessibility, Owner owner, AccessModifier access, boolean isHome){
		this.pos = pos;
		this.type = type;
		this.name = name;
		this.accessibility = accessibility;
		this.owner = owner;
		this.access = access;
		this.isHome = isHome;
	}
	public OpenTavernScreenMsg(){
		this(TavernPos.ORIGIN, TavernType.NORMAL, null, Accessibility.MODIFIABLE, Owner.NO_OWNER, AccessModifier.PUBLIC, false);
	}

	public TavernPos getPos(){
		return pos;
	}
	public TavernType getType(){
		return type;
	}
	@Nullable public String getName(){
		return name;
	}
	public Accessibility getAccessibility(){
		return accessibility;
	}
	public Owner getOwner(){
		return owner;
	}
	public AccessModifier getAccess(){
		return access;
	}
	public boolean isHome(){
		return isHome;
	}

	@Override public void fromBytes(ByteBuf buf){
		this.pos = TavernPos.read(buf);
		this.type = TavernType.of(buf.readByte());
		this.name = buf.readBoolean() ? ByteBufUtils.readUTF8String(buf) : null;
		this.accessibility = Accessibility.fromMeta(buf.readUnsignedByte());
		this.owner = Owner.read(buf);
		this.access = AccessModifier.of(buf.readByte());
		this.isHome = buf.readBoolean();
	}
	@Override public void toBytes(ByteBuf buf){
		pos.write(buf);
		buf.writeByte(type.ordinal());
		buf.writeBoolean(name!=null);
		if(name!=null) ByteBufUtils.writeUTF8String(buf, name);
		buf.writeByte(accessibility.ordinal());
		owner.write(buf);
		buf.writeByte(access.ordinal());
		buf.writeBoolean(isHome);
	}
}
