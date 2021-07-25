package tictim.hearthstones.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class AquamarineOreBlock extends Block{
	public AquamarineOreBlock(){
		super(Properties.of(Material.STONE).strength(3, 5).harvestTool(ToolType.PICKAXE).harvestLevel(1));
	}

	@Override
	public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch){
		return MathHelper.nextInt(world instanceof World ? ((World)world).random : new Random(), 3, 7);
	}
}
