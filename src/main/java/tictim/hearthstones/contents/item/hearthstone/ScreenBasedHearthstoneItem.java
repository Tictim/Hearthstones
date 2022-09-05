package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import tictim.hearthstones.hearthstone.Hearthstone;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenHearthstoneScreenMsg;

public class ScreenBasedHearthstoneItem extends HearthstoneItem{
	public ScreenBasedHearthstoneItem(Hearthstone hearthstone){
		super(hearthstone);
	}

	@Override public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		if(!player.isSneaking()) return super.onItemRightClick(world, player, hand);
		if(!world.isRemote&&player instanceof EntityPlayerMP){
			ModNet.CHANNEL.sendTo(new OpenHearthstoneScreenMsg(player, isHearthingGem()), (EntityPlayerMP)player);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	protected boolean isHearthingGem(){
		return false;
	}
}

