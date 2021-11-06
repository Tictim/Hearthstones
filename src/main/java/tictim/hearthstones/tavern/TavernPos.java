package tictim.hearthstones.tavern;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public record TavernPos(ResourceLocation dim, BlockPos pos){
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

	public TavernPos(ResourceLocation dim, BlockPos pos){
		this.dim = Objects.requireNonNull(dim);
		this.pos = pos.immutable();
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

	public CompoundTag write(){
		CompoundTag nbt = new CompoundTag();
		nbt.putString("dim", dim.toString());
		nbt.put("pos", NbtUtils.writeBlockPos(pos));
		return nbt;
	}

	public void write(FriendlyByteBuf buffer){
		buffer.writeResourceLocation(dim);
		buffer.writeBlockPos(pos);
	}

	@Override public String toString(){
		return String.format("[%s, %d %d %d]", dim, pos.getX(), pos.getY(), pos.getZ());
	}

	public static TavernPos read(FriendlyByteBuf buf){
		return new TavernPos(buf.readResourceLocation(), buf.readBlockPos());
	}
}
