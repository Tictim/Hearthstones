package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tictim.hearthstones.hearthstone.Hearthstone;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenHearthstoneScreenMsg;

public class ScreenBasedHearthstoneItem extends HearthstoneItem{
	public ScreenBasedHearthstoneItem(Hearthstone hearthstone){
		super(hearthstone);
	}

	@Override public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!player.isSneaking()) return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
		if(!world.isRemote&&player instanceof EntityPlayerMP){
			ModNet.CHANNEL.sendTo(new OpenHearthstoneScreenMsg(player, isHearthingGem()), (EntityPlayerMP)player);
		}
		return EnumActionResult.SUCCESS;
	}

	protected boolean isHearthingGem(){
		return false;
	}
}

