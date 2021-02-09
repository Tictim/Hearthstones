package tictim.hearthstones.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.tileentity.BaseTavernTileEntity;
import tictim.hearthstones.tileentity.GlobalTavernTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class GlobalTavernBlock extends BaseTavernBlock{
	@Override public BaseTavernTileEntity createTileEntity(BlockState state, IBlockReader world){
		return new GlobalTavernTileEntity();
	}

	@Override public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("info.hearthstones.tavern.global.tooltip.1"));
	}

	@Override protected void addTipInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		super.addTipInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("info.hearthstones.tavern.global.tooltip.0"));
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player){
		super.onBlockHarvested(world, pos, state, player);
		if(!world.isRemote){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof GlobalTavernTileEntity){
				GlobalTavernMemory.get().delete(new TavernPos(te));
			}
		}
	}
}
