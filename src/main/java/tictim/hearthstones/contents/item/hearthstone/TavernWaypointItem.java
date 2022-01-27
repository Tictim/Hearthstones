package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nullable;
import java.util.List;

public class TavernWaypointItem extends Item{
	public TavernWaypointItem(Properties properties){
		super(properties);
	}

	@Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context){
		if(context.getLevel().isClientSide) return InteractionResult.PASS;
		BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
		if(blockEntity instanceof Tavern tavern){
			setTavern(stack, tavern);
			if(context.getPlayer()!=null)
				context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.waypoint.synced"), true);
		}
		return InteractionResult.PASS;
	}

	@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide){
			TavernRecord tavern = getTavern(stack);
			if(tavern!=null){
				PlayerTavernMemory m = TavernMemories.player(player);
				if(m.has(tavern.pos())){
					m.addOrUpdate(tavern);
					level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.5f, 1);
				}
			}
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag){
		TavernRecord tavern = getTavern(stack);
		if(tavern!=null)
			text.add(new TranslatableComponent("info.hearthstones.waypoint.tooltip.tavern", tavern.pos()));
		text.add(new TranslatableComponent("info.hearthstones.waypoint.tooltip.0"));
	}

	@Nullable public static TavernRecord getTavern(ItemStack stack){
		CompoundTag tag = stack.getTag();
		return tag==null||!tag.contains("Tavern", Tag.TAG_COMPOUND) ? null :
				new TavernRecord(tag.getCompound("Tavern"));
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
