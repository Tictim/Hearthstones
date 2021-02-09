package tictim.hearthstones.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.Objects;

public final class TavernPos{
	public static final TavernPos OVERWORLD_ORIGIN = new TavernPos(DimensionType.OVERWORLD, 0, 0, 0);

	@Nullable
	public static TavernPos tryParse(CompoundNBT nbt){
		DimensionType type = DimensionType.byName(new ResourceLocation(nbt.getString("dim")));
		return type==null ? null : new TavernPos(type, NBTUtil.readBlockPos(nbt.getCompound("pos")));
	}
	@Nullable
	public static TavernPos tryRead(PacketBuffer buffer){
		DimensionType type = DimensionType.getById(buffer.readInt());
		return type==null ? null : new TavernPos(type, buffer.readBlockPos());
	}

	public final DimensionType dim;
	public final BlockPos pos;

	public TavernPos(World world, int x, int y, int z){
		this(world.getDimension().getType(), x, y, z);
	}
	public TavernPos(Dimension dim, int x, int y, int z){
		this(dim.getType(), x, y, z);
	}
	public TavernPos(DimensionType dimensionType, int x, int y, int z){
		this(dimensionType, new BlockPos(x, y, z));
	}
	public TavernPos(World world, BlockPos pos){
		this(world.getDimension().getType(), pos);
	}
	public TavernPos(Dimension dim, BlockPos pos){
		this(dim.getType(), pos);
	}
	public TavernPos(TileEntity tileEntity){
		this(tileEntity.getWorld(), tileEntity.getPos());
	}
	public TavernPos(CompoundNBT nbt){
		this(DimensionType.byName(new ResourceLocation(nbt.getString("dim"))), NBTUtil.readBlockPos(nbt));
	}
	public TavernPos(PacketBuffer buffer){
		this(DimensionType.getById(buffer.readInt()), buffer.readBlockPos());
	}

	public TavernPos(DimensionType dimensionType, BlockPos pos){
		this.dim = Objects.requireNonNull(dimensionType);
		this.pos = pos.toImmutable();
	}

	public boolean isSameTile(TileEntity te){
		return te.getWorld().getDimension().getType()==dim&&pos.equals(te.getPos());
	}

	public CompoundNBT serialize(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("dim", DimensionType.getKey(dim).toString());
		nbt.put("pos", NBTUtil.writeBlockPos(pos));
		return nbt;
	}

	public void write(PacketBuffer buffer){
		buffer.writeInt(dim.getId());
		buffer.writeBlockPos(pos);
	}

	@Override
	public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		TavernPos tavernPos = (TavernPos)o;
		return dim.equals(tavernPos.dim)&&pos.equals(tavernPos.pos);
	}
	@Override
	public int hashCode(){
		return Objects.hash(dim, pos);
	}
	@Override
	public String toString(){
		return String.format("[%s (%d), %d %d %d]", DimensionType.getKey(dim), dim.getId(), pos.getX(), pos.getY(), pos.getZ());
	}
}
