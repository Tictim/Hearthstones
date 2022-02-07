package tictim.hearthstones.tavern;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraftforge.fml.common.network.ByteBufUtils.writeUTF8String;

public final class TavernRecord implements Tavern{
	private final TavernPos pos;
	@Nullable private final String name;
	private final Owner owner;
	private final TavernType type;
	private final AccessModifier access;
	private final boolean isMissing;

	@Nullable private final IBlockState skin;

	public TavernRecord(TavernPos pos,
	                    @Nullable String name,
	                    Owner owner,
	                    TavernType type,
	                    AccessModifier access,
	                    boolean isMissing,
	                    @Nullable IBlockState skin){
		this.pos = pos;
		this.name = name;
		this.owner = owner;
		this.type = type;
		this.access = access;
		this.isMissing = isMissing;
		this.skin = skin;
	}

	public TavernRecord(Tavern tavern){
		this(tavern.pos(), tavern.name(), tavern.owner(), tavern.type(), tavern.access(), tavern.isMissing(), tavern.skin());
	}
	public TavernRecord(Tavern tavern, boolean missing){
		this(tavern.pos(), tavern.name(), tavern.owner(), tavern.type(), tavern.access(), missing, tavern.skin());
	}
	public TavernRecord(NBTTagCompound nbt){
		this(new TavernPos(nbt.getCompoundTag("pos")),
				nbt.hasKey("name", Constants.NBT.TAG_STRING) ? nbt.getString("name") : null,
				Owner.read(nbt.getCompoundTag("owner")),
				TavernType.of(nbt.getByte("type")),
				AccessModifier.of(nbt.getByte("access")),
				nbt.getBoolean("missing"),
				Tavern.readSkin(nbt));
	}

	@Override public TavernPos pos(){
		return pos;
	}
	@Nullable @Override public String name(){
		return name;
	}
	@Override public Owner owner(){
		return owner;
	}
	@Override public TavernType type(){
		return type;
	}
	@Override public AccessModifier access(){
		return access;
	}
	@Override public boolean isMissing(){
		return isMissing;
	}

	@Nullable @Override public IBlockState skin(){
		return skin;
	}

	@Override public BlockPos blockPos(){
		return pos.pos();
	}

	@Override public TavernRecord toRecord(){
		return this;
	}

	public NBTTagCompound write(){
		return write(this);
	}

	public void write(ByteBuf buf){
		write(this, buf);
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		TavernRecord that = (TavernRecord)o;
		return isMissing()==that.isMissing()&&
				Objects.equals(pos, that.pos)&&
				Objects.equals(name, that.name)&&
				Objects.equals(owner, that.owner)&&
				type==that.type&&
				access==that.access;
	}
	@Override public int hashCode(){
		return Objects.hash(pos, name, owner, type, access, isMissing());
	}

	@Override public String toString(){
		return "TavernRecord{"+
				"pos="+pos+
				", name='"+name+'\''+
				", owner="+owner+
				", type="+type+
				", access="+access+
				", isMissing="+isMissing+
				", skin="+skin+
				'}';
	}

	public static TavernRecord read(ByteBuf buf){
		return new TavernRecord(
				TavernPos.read(buf),
				buf.readBoolean() ? ByteBufUtils.readUTF8String(buf) : null,
				Owner.read(buf),
				TavernType.of(buf.readUnsignedByte()),
				AccessModifier.of(buf.readUnsignedByte()),
				buf.readBoolean(),
				buf.readBoolean() ? Block.getStateById(buf.readInt()) : null);
	}

	public static NBTTagCompound write(Tavern tavern){
		NBTTagCompound nbt = new NBTTagCompound();

		String name = tavern.name();
		Owner owner = tavern.owner();
		TavernType type = tavern.type();
		AccessModifier access = tavern.access();
		IBlockState skin = tavern.skin();

		nbt.setTag("pos", tavern.pos().write());
		if(name!=null) nbt.setString("name", name);
		if(owner.hasOwner()) nbt.setTag("owner", owner.write());
		if(type!=TavernType.NORMAL) nbt.setByte("type", (byte)type.ordinal());
		if(access.ordinal()!=0) nbt.setByte("access", (byte)access.ordinal());
		if(tavern.isMissing()) nbt.setBoolean("missing", true);
		if(skin!=null) Tavern.writeSkin(nbt, skin);
		return nbt;
	}

	public static void write(Tavern tavern, ByteBuf buf){
		tavern.pos().write(buf);
		String name = tavern.name();
		buf.writeBoolean(name!=null);
		if(name!=null) writeUTF8String(buf, name);
		tavern.owner().write(buf);
		buf.writeByte(tavern.type().ordinal());
		buf.writeByte(tavern.access().ordinal());
		buf.writeBoolean(tavern.isMissing());
		IBlockState skin = tavern.skin();
		buf.writeBoolean(skin!=null);
		if(skin!=null) buf.writeInt(Block.getStateId(skin));
	}
}
