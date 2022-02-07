package tictim.hearthstones.tavern;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tictim.hearthstones.net.ByteBufIO;

import java.util.Objects;

public final class TavernPos{
	public static final TavernPos ORIGIN = new TavernPos(0, 0, 0, 0);

	private final int dim;
	private final BlockPos pos;

	public TavernPos(World world, int x, int y, int z){
		this(world.provider.getDimension(), x, y, z);
	}
	public TavernPos(int dim, int x, int y, int z){
		this(dim, new BlockPos(x, y, z));
	}
	public TavernPos(World world, BlockPos pos){
		this(world.provider.getDimension(), pos);
	}
	public TavernPos(TileEntity tileEntity){
		this(tileEntity.getWorld(), tileEntity.getPos());
	}
	public TavernPos(NBTTagCompound nbt){
		this(nbt.getInteger("dim"), NBTUtil.getPosFromTag(nbt.getCompoundTag("pos")));
	}

	public TavernPos(int dim, BlockPos pos){
		this.dim = dim;
		this.pos = pos.toImmutable();
	}

	public int dim(){
		return dim;
	}
	public BlockPos pos(){
		return pos;
	}

	public boolean isSameDimension(World world){
		return isSameDimension(world.provider.getDimension());
	}
	public boolean isSameDimension(int dim){
		return this.dim==dim;
	}

	public NBTTagCompound write(){
		NBTTagCompound nbt = new NBTTagCompound();
		if(dim!=0) nbt.setInteger("dim", dim);
		nbt.setTag("pos", NBTUtil.createPosTag(pos));
		return nbt;
	}

	public void write(ByteBuf buffer){
		buffer.writeInt(dim);
		ByteBufIO.writePos(buffer, pos);
	}

	@Override
	public boolean equals(Object obj){
		if(obj==this) return true;
		if(obj==null||obj.getClass()!=this.getClass()) return false;
		TavernPos that = (TavernPos)obj;
		return Objects.equals(this.dim, that.dim)&&
				Objects.equals(this.pos, that.pos);
	}
	@Override
	public int hashCode(){
		return Objects.hash(dim, pos);
	}

	@Override public String toString(){
		return String.format("[%s, %d %d %d]", dim, pos.getX(), pos.getY(), pos.getZ());
	}

	public static TavernPos read(ByteBuf buf){
		return new TavernPos(buf.readInt(), ByteBufIO.readPos(buf));
	}
}