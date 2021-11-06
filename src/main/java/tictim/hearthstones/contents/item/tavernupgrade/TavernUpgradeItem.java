package tictim.hearthstones.contents.item.tavernupgrade;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernMemories;

import javax.annotation.Nullable;

public abstract class TavernUpgradeItem extends Item{
	public TavernUpgradeItem(Properties properties){
		super(properties);
	}

	@Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context){
		Level level = context.getLevel();
		if(!(level.getBlockEntity(context.getClickedPos()) instanceof TavernBlockEntity tavern))
			return InteractionResult.PASS;
		if(!level.isClientSide){
			BlockState stateToReplace = getStateToReplace(tavern);
			if(stateToReplace!=null&&tavern.upgrade(stateToReplace, context.getPlayer()==null||!context.getPlayer().isCreative())){
				if(context.getPlayer()!=null){
					PlayerTavernMemory memory = TavernMemories.player(context.getPlayer());
					if(level.getBlockEntity(context.getClickedPos()) instanceof TavernBlockEntity t2) memory.addOrUpdate(t2);
					else memory.delete(tavern.pos());
				}
				level.playSound(null, context.getClickedPos().getX()+0.5, context.getClickedPos().getY()+0.5, context.getClickedPos().getZ()+0.5, SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.BLOCKS, 0.8f, 1);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable protected abstract BlockState getStateToReplace(TavernBlockEntity tavern);
}
