package tictim.hearthstones.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;

import java.util.Objects;

public final class TavernPos{
	public static final TavernPos OVERWORLD_ORIGIN = new TavernPos(Dimension.OVERWORLD.getLocation(), 0, 0, 0);

	public final ResourceLocation dim;
	public final BlockPos pos;

	public TavernPos(World world, int x, int y, int z){
		this(world.getDimensionKey().getLocation(), x, y, z);
	}
	public TavernPos(ResourceLocation dim, int x, int y, int z){
		this(dim, new BlockPos(x, y, z));
	}
	public TavernPos(World world, BlockPos pos){
		this(world.getDimensionKey().getLocation(), pos);
	}
	public TavernPos(TileEntity tileEntity){
		this(Objects.requireNonNull(tileEntity.getWorld()), tileEntity.getPos());
	}
	public TavernPos(CompoundNBT nbt){
		this(new ResourceLocation(nbt.getString("dim")), NBTUtil.readBlockPos(nbt.getCompound("pos")));
	}
	public TavernPos(PacketBuffer buffer){
		this(buffer.readResourceLocation(), buffer.readBlockPos());
	}

	public TavernPos(ResourceLocation dim, BlockPos pos){
		this.dim = Objects.requireNonNull(dim);
		this.pos = pos.toImmutable();
	}

	public boolean isSameTile(TileEntity te){
		return Objects.requireNonNull(te.getWorld()).getDimensionKey().getLocation()==dim&&pos.equals(te.getPos());
	}

	public boolean isSameDimension(World world){
		return isSameDimension(world.getDimensionKey());
	}
	public boolean isSameDimension(RegistryKey<World> registryKey){
		return isSameDimension(registryKey.getLocation());
	}
	public boolean isSameDimension(ResourceLocation dim){
		return this.dim.equals(dim);
	}

	public CompoundNBT serialize(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("dim", dim.toString());
		nbt.put("pos", NBTUtil.writeBlockPos(pos));
		return nbt;
	}

	public void write(PacketBuffer buffer){
		buffer.writeResourceLocation(dim);
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
		return String.format("[%s, %d %d %d]", dim, pos.getX(), pos.getY(), pos.getZ());
	}
}
