package tictim.hearthstones.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.Level;

import java.util.Objects;

public final class TavernPos{
	public static final TavernPos OVERWORLD_ORIGIN = new TavernPos(LevelStem.OVERWORLD.location(), 0, 0, 0);

	public final ResourceLocation dim;
	public final BlockPos pos;

	public TavernPos(Level world, int x, int y, int z){
		this(world.dimension().location(), x, y, z);
	}
	public TavernPos(ResourceLocation dim, int x, int y, int z){
		this(dim, new BlockPos(x, y, z));
	}
	public TavernPos(Level world, BlockPos pos){
		this(world.dimension().location(), pos);
	}
	public TavernPos(BlockEntity tileEntity){
		this(Objects.requireNonNull(tileEntity.getLevel()), tileEntity.getBlockPos());
	}
	public TavernPos(CompoundTag nbt){
		this(new ResourceLocation(nbt.getString("dim")), NbtUtils.readBlockPos(nbt.getCompound("pos")));
	}
	public TavernPos(FriendlyByteBuf buffer){
		this(buffer.readResourceLocation(), buffer.readBlockPos());
	}

	public TavernPos(ResourceLocation dim, BlockPos pos){
		this.dim = Objects.requireNonNull(dim);
		this.pos = pos.immutable();
	}

	public boolean isSameTile(BlockEntity te){
		return Objects.requireNonNull(te.getLevel()).dimension().location()==dim&&pos.equals(te.getBlockPos());
	}

	public boolean isSameDimension(Level world){
		return isSameDimension(world.dimension());
	}
	public boolean isSameDimension(ResourceKey<Level> registryKey){
		return isSameDimension(registryKey.location());
	}
	public boolean isSameDimension(ResourceLocation dim){
		return this.dim.equals(dim);
	}

	public CompoundTag serialize(){
		CompoundTag nbt = new CompoundTag();
		nbt.putString("dim", dim.toString());
		nbt.put("pos", NbtUtils.writeBlockPos(pos));
		return nbt;
	}

	public void write(FriendlyByteBuf buffer){
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
