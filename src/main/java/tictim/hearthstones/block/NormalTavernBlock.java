package tictim.hearthstones.block;

import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockReader;
import tictim.hearthstones.tileentity.BaseTavernTileEntity;
import tictim.hearthstones.tileentity.NormalTavernTileEntity;

public class NormalTavernBlock extends BaseTavernBlock{
	@Override public BaseTavernTileEntity createTileEntity(BlockState state, IBlockReader world){
		return new NormalTavernTileEntity();
	}
}
