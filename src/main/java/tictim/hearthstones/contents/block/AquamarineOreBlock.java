package tictim.hearthstones.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class AquamarineOreBlock extends Block{
	public AquamarineOreBlock(){
		super(Material.ROCK);
		this.setHardness(3.0F).setResistance(5.0F);
		this.setSoundType(SoundType.STONE);
	}

	@Override public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune){
		return MathHelper.getInt(world instanceof World ? ((World)world).rand : new Random(), 3, 7);
	}
}
