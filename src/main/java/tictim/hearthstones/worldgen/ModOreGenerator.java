package tictim.hearthstones.worldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.IWorldGenerator;
import tictim.hearthstones.config.WorldgenConfig;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class ModOreGenerator implements IWorldGenerator{
	private final Supplier<IBlockState> oreBlockState;
	private final WorldgenConfig config;

	@Nullable private WorldGenMinable cache;
	private int oreSizeCache;

	public ModOreGenerator(Supplier<IBlockState> oreBlockState, WorldgenConfig config){
		this.oreBlockState = oreBlockState;
		this.config = config;
	}

	private WorldGenMinable getGenerator(){
		if(cache==null||oreSizeCache!=config.oreSize)
			cache = new WorldGenMinable(oreBlockState.get(), oreSizeCache = config.oreSize);
		return cache;
	}

	@Override public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider){
		if(config.enable){
			WorldGenMinable gen = getGenerator();
			if(TerrainGen.generateOre(world, random, gen, new BlockPos(chunkX*16, 0, chunkZ*16), EventType.CUSTOM)){
				for(int i = 0, j = config.countInChunk; i<j; i++)
					gen.generate(world, random, new BlockPos(chunkX*16+random.nextInt(16),
							random.nextInt(config.maxY-config.minY)+config.minY,
							chunkZ*16+random.nextInt(16)));
			}
		}
	}
}
