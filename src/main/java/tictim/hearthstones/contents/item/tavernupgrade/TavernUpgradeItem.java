package tictim.hearthstones.contents.item.tavernupgrade;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tictim.hearthstones.contents.item.RareItem;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernMemories;

import javax.annotation.Nullable;

public abstract class TavernUpgradeItem extends RareItem{
	@Override public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand){
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof TavernTile)) return EnumActionResult.PASS;
		if(!world.isRemote){
			TavernTile tavern = (TavernTile)te;
			IBlockState stateToReplace = getStateToReplace(tavern);
			if(stateToReplace!=null&&tavern.upgrade(stateToReplace, !player.isCreative())){
				PlayerTavernMemory memory = TavernMemories.player(player);
				TileEntity te2 = world.getTileEntity(pos);
				if(te2 instanceof TavernTile)
					memory.addOrUpdate(((TavernTile)te2));
				else memory.delete(tavern.pos());
				world.playSound(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 0.8f, 1);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Nullable protected abstract IBlockState getStateToReplace(TavernTile tavern);
}
