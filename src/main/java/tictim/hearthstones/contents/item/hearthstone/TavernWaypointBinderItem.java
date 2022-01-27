package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import tictim.hearthstones.Caps;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TavernWaypointBinderItem extends Item{
	public TavernWaypointBinderItem(Properties properties){
		super(properties);
	}

	@Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context){
		if(context.getLevel().isClientSide) return InteractionResult.PASS;
		Data data = data(stack);
		if(data==null) return InteractionResult.PASS;
		BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
		if(blockEntity instanceof Tavern tavern){
			data.memory.addOrUpdate(tavern);
			if(context.getPlayer()!=null)
				context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.waypoint.synced"), true);
		}
		return InteractionResult.PASS;
	}

	@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide){
			Data data = data(stack);
			if(data!=null){
				PlayerTavernMemory m = TavernMemories.player(player);
				boolean f = false;
				for(TavernRecord t : data.memory.taverns().values()){
					if(!m.has(t.pos())) continue;
					m.addOrUpdate(t);
					f = true;
				}
				if(f) level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.5f, 1);
			}
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag){
		int waypoints = getWaypointsClient(stack);
		if(waypoints>0)
			text.add(new TranslatableComponent("info.hearthstones.binder.waypoints", waypoints));
		text.add(new TranslatableComponent("info.hearthstones.binder.tooltip.0"));
		text.add(new TranslatableComponent("info.hearthstones.binder.tooltip.1"));
	}

	@Nullable @Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
		return new Data();
	}

	@Nullable @Override public CompoundTag getShareTag(ItemStack stack){
		CompoundTag tag = stack.getTag();
		Data data = data(stack);
		if(data!=null&&data.getWaypoints()>0){
			if(tag==null) tag = new CompoundTag();
			tag.putInt("Waypoints", data.getWaypoints());
		}
		return tag;
	}
	@Override public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt){
		super.readShareTag(stack, nbt);
	}

	@SuppressWarnings("ConstantConditions") @Nullable public static Data data(ItemStack stack){
		return stack.getCapability(Caps.BINDER_DATA).orElse(null);
	}

	public static int getWaypointsClient(ItemStack stack){
		CompoundTag tag = stack.getTag();
		return tag!=null ? tag.getInt("Waypoints") : 0;
	}

	public static final class Data implements ICapabilitySerializable<CompoundTag>{
		private int waypoints;
		public final TavernMemory memory = new TavernMemory();

		public int getWaypoints(){
			return waypoints;
		}
		public void setWaypoints(int waypoints){
			this.waypoints = waypoints;
		}

		@Nullable private LazyOptional<Data> self;

		@Nonnull @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
			if(cap!=Caps.BINDER_DATA) return LazyOptional.empty();
			if(self==null) self = LazyOptional.of(() -> this);
			return self.cast();
		}
		@Override public CompoundTag serializeNBT(){
			CompoundTag tag = memory.write();
			if(waypoints>0)
				tag.putInt("Waypoints", waypoints);
			return tag;
		}
		@Override public void deserializeNBT(CompoundTag nbt){
			memory.read(nbt);
			this.waypoints = nbt.getInt("Waypoints");
		}
	}
}
