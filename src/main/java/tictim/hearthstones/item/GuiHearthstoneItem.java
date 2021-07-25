package tictim.hearthstones.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.logic.Hearthstone;

import net.minecraft.world.item.Item.Properties;

public class GuiHearthstoneItem extends BaseHearthstoneItem{
	public GuiHearthstoneItem(Properties properties, Hearthstone hearthstone){
		super(properties, hearthstone);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		if(player.isShiftKeyDown()){
			Hearthstones.PROXY.openHearthstoneGui(world, player);
			return new InteractionResultHolder<>(InteractionResult.CONSUME, player.getItemInHand(hand));
		}else return super.use(world, player, hand);
	}
}
