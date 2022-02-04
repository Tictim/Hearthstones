package tictim.hearthstones.contents.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.item.hearthstone.TavernWaypointBinderItem;
import tictim.hearthstones.contents.item.hearthstone.TavernWaypointBinderItem.Data;

import javax.annotation.Nullable;
import java.util.UUID;

public class BinderLecternBlockEntity extends BlockEntity{
	@Nullable private UUID player;
	@Nullable private ItemStack item;
	@Nullable private Data data;

	public BinderLecternBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.BINDER_LECTERN.get(), pos, state);
	}

	@Nullable public UUID getPlayer(){
		return player;
	}
	@Nullable public ItemStack getItem(){
		return item;
	}
	@Nullable public Data getData(){
		return data;
	}

	public void setBinder(@Nullable Player player, @Nullable ItemStack binder){
		this.player = player!=null ? player.getUUID() : null;
		if(binder==null){
			this.item = null;
			this.data = null;
		}else{
			this.item = binder.copy();
			this.data = new Data();
			Data d2 = TavernWaypointBinderItem.data(binder);
			if(d2!=null) this.data.overwrite(d2);
		}
		this.setChanged();
	}

	@Override public void load(CompoundTag tag){
		super.load(tag);
		this.player = tag.hasUUID("player") ? tag.getUUID("player") : null;
		this.item = tag.contains("item", Tag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound("item")) : null;
		this.data = tag.contains("data", Tag.TAG_COMPOUND) ? new Data(tag.getCompound("data")) : null;
	}

	@Override protected void saveAdditional(CompoundTag tag){
		if(player!=null) tag.putUUID("player", player);
		if(item!=null) tag.put("item", item.save(new CompoundTag()));
		if(data!=null) tag.put("data", data.serializeNBT());
	}
}
