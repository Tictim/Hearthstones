package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.blockentity.BinderLecternBlockEntity;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernRecord;
import tictim.hearthstones.tavern.TavernTextFormat;

import javax.annotation.Nullable;
import java.util.List;

public class TavernWaypointItem extends Item{
	public TavernWaypointItem(Properties properties){
		super(properties);
	}

	@Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context){
		Level level = context.getLevel();
		if(level.isClientSide) return InteractionResult.PASS;
		BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
		if(blockEntity instanceof Tavern tavern){
			Player player = context.getPlayer();
			if(stack.getCount()==1){
				setTavern(stack, tavern);
			}else{
				ItemStack s2 = stack.split(1);
				setTavern(s2, tavern);
				if(player==null){
					Vec3 l = context.getClickLocation();
					level.addFreshEntity(new ItemEntity(level, l.x, l.y, l.z, s2));
				}else if(!player.getInventory().add(s2)){
					level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), s2));
				}
			}
			if(player!=null)
				player.displayClientMessage(new TranslatableComponent("info.hearthstones.waypoint.saved"), true);
		}else if(blockEntity instanceof BinderLecternBlockEntity binderLectern){
			TavernWaypointBinderItem.Data data = binderLectern.getData();
			if(data!=null){
				Tavern tavern = getTavern(stack);
				if(tavern==null){
					if(data.getWaypoints()<Integer.MAX_VALUE){
						data.setWaypoints(data.getWaypoints()+1);
						stack.shrink(1);
						binderLectern.setChanged();
					}
				}else if(!data.memory.has(tavern.pos())){
					data.memory.addOrUpdate(tavern);
					TavernBlock.playSyncSound(level, context.getClickedPos());
					stack.shrink(1);
					binderLectern.setChanged();
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(!hasTavern(stack))
			return InteractionResultHolder.pass(stack);
		if(!level.isClientSide){
			TavernRecord tavern = getTavern(stack);
			if(tavern!=null){
				PlayerTavernMemory m = TavernMemories.player(player);
				if(!m.has(tavern.pos())){
					m.addOrUpdate(tavern);
					TavernBlock.playSyncSound(level, player);
				}
			}
		}
		return InteractionResultHolder.fail(stack);
	}

	@Override public boolean isFoil(ItemStack stack){
		return hasTavern(stack);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag){
		TavernRecord tavern = getTavern(stack);
		if(tavern!=null)
			text.add(new TranslatableComponent("info.hearthstones.waypoint.tooltip.tavern",
					TavernTextFormat.name(tavern),
					TavernTextFormat.position(tavern)));
		text.add(new TranslatableComponent("info.hearthstones.waypoint.tooltip.0"));
		text.add(new TranslatableComponent("info.hearthstones.waypoint.tooltip.1"));
	}

	@Nullable public static TavernRecord getTavern(ItemStack stack){
		CompoundTag tag = stack.getTag();
		return tag==null||!tag.contains("Tavern", Tag.TAG_COMPOUND) ? null :
				new TavernRecord(tag.getCompound("Tavern"));
	}

	public static boolean hasTavern(ItemStack stack){
		CompoundTag tag = stack.getTag();
		return tag!=null&&tag.contains("Tavern", Tag.TAG_COMPOUND);
	}

	public static void setTavern(ItemStack stack, @Nullable Tavern tavern){
		if(tavern==null){
			CompoundTag tag = stack.getTag();
			if(tag!=null) tag.remove("Tavern");
		}else{
			CompoundTag tag = stack.getOrCreateTag();
			tag.put("Tavern", TavernRecord.write(tavern));
		}
	}
}
