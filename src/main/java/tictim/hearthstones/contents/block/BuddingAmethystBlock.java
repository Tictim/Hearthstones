package tictim.hearthstones.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModSoundTypes;

import java.util.Random;

@SuppressWarnings("deprecation")
public class BuddingAmethystBlock extends AmethystBlock{
	public static final int GROWTH_CHANCE = 5;

	public BuddingAmethystBlock(){
		this.setSoundType(ModSoundTypes.AMETHYST_SOUND);
		this.setTickRandomly(true);
	}

	@Override public void randomTick(World world, BlockPos pos, IBlockState state, Random random){
		if(random.nextInt(GROWTH_CHANCE)!=0) return;
		EnumFacing facing = EnumFacing.random(random);
		BlockPos offset = pos.offset(facing);
		IBlockState at = world.getBlockState(offset);
		Block b = at.getBlock();
		Block newBlock;
		if(b.isAir(at, world, offset)) newBlock = ModBlocks.SMALL_AMETHYST_BUD;
		else if(b==ModBlocks.SMALL_AMETHYST_BUD&&at.getValue(BlockDirectional.FACING)==facing) newBlock = ModBlocks.MEDIUM_AMETHYST_BUD;
		else if(b==ModBlocks.MEDIUM_AMETHYST_BUD&&at.getValue(BlockDirectional.FACING)==facing) newBlock = ModBlocks.LARGE_AMETHYST_BUD;
		else if(b==ModBlocks.LARGE_AMETHYST_BUD&&at.getValue(BlockDirectional.FACING)==facing) newBlock = ModBlocks.AMETHYST_CLUSTER;
		else return;
		world.setBlockState(offset, newBlock.getDefaultState().withProperty(BlockDirectional.FACING, facing));
	}

	@Override public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune){}

	@Override protected boolean canSilkHarvest(){
		return false;
	}

	@Override public EnumPushReaction getPushReaction(IBlockState state){
		return EnumPushReaction.DESTROY;
	}
}
