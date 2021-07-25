package tictim.hearthstones.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.logic.Hearthstone;

public class GuiHearthstoneItem extends BaseHearthstoneItem{
	public GuiHearthstoneItem(Properties properties, Hearthstone hearthstone){
		super(properties, hearthstone);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		if(player.isShiftKeyDown()){
			Hearthstones.PROXY.openHearthstoneGui(world, player);
			return new ActionResult<>(ActionResultType.CONSUME, player.getItemInHand(hand));
		}else return super.use(world, player, hand);
	}
}
