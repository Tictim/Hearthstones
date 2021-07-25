package tictim.hearthstones.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import tictim.hearthstones.contents.ModTileEntities;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class BaseTavernTileEntity extends TileEntity implements Tavern{
	private final Owner owner = new Owner();
	private ITextComponent name;

	public BaseTavernTileEntity(){
		super(ModTileEntities.TAVERN.get());
	}
	protected BaseTavernTileEntity(TileEntityType<?> type){
		super(type);
	}

	@Override public World world(){
		return Objects.requireNonNull(getLevel());
	}
	@Override public BlockPos pos(){
		return getBlockPos();
	}

	@Override public ITextComponent getName(){
		return hasCustomName() ? name : new StringTextComponent("");
	}
	@Nullable @Override public ITextComponent getCustomName(){
		return name;
	}
	public void setName(@Nullable ITextComponent name){
		this.name = name;
	}

	public Owner owner(){
		return this.owner;
	}
	public TavernType tavernType(){
		return TavernType.NORMAL;
	}

	@Override public SUpdateTileEntityPacket getUpdatePacket(){
		return new SUpdateTileEntityPacket(this.worldPosition, 0, this.getUpdateTag());
	}
	@Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		read(pkt.getTag());
	}
	@Override public CompoundNBT getUpdateTag(){
		return save(new CompoundNBT());
	}

	@Override public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		read(nbt);
	}

	private void read(CompoundNBT nbt){
		this.name = nbt.contains("name", NBT.TAG_STRING) ? ITextComponent.Serializer.fromJson(nbt.getString("name")) : null;
		if(nbt.contains("owner", NBT.TAG_COMPOUND)) this.owner.deserializeNBT(nbt.getCompound("owner"));
		else this.owner.reset();
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		nbt = super.save(nbt);
		if(this.name!=null) nbt.putString("name", ITextComponent.Serializer.toJson(this.name));
		if(owner.hasOwner()) nbt.put("owner", this.owner.serializeNBT());
		return nbt;
	}

	public CompoundNBT writeNBTForStack(){
		CompoundNBT nbt = new CompoundNBT();
		if(owner.hasOwner()) nbt.put("owner", this.owner.serializeNBT());
		return nbt;
	}

	@Override public String toString(){
		return "BaseTavernTileEntity{"+
				"owner="+owner+
				", name="+(name!=null ? name : null)+
				'}';
	}
}
