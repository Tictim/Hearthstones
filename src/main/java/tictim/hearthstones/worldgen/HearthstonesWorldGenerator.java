package tictim.hearthstones.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.IWorldGenerator;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModBlocks;

import javax.annotation.Nullable;
import java.util.Random;

public class HearthstonesWorldGenerator implements IWorldGenerator{
	@Nullable private WorldGenMinable aquamarineGenCache;
	private int aquamarineBlockCountCache;
	@Nullable private WorldGenMinable amethystGenCache;
	private int amethystBlockCountCache;

	private WorldGenMinable getAquamarineGen(){
		if(aquamarineGenCache==null||aquamarineBlockCountCache!=ModCfg.aquamarineOreSize)
			aquamarineGenCache = new WorldGenMinable(ModBlocks.AQUAMARINE_ORE.getDefaultState(), aquamarineBlockCountCache = ModCfg.aquamarineOreSize);
		return aquamarineGenCache;
	}
	private WorldGenMinable getAmethystGen(){
		if(amethystGenCache==null||amethystBlockCountCache!=ModCfg.amethystOreSize)
			amethystGenCache = new WorldGenMinable(ModBlocks.AMETHYST_BLOCK.getDefaultState(), amethystBlockCountCache = ModCfg.amethystOreSize);
		return amethystGenCache;
	}

	@Override public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider){
		if(ModCfg.aquamarineGen){
			WorldGenMinable aquamarineGen = getAquamarineGen();
			if(TerrainGen.generateOre(world, random, aquamarineGen, new BlockPos(chunkX*16, 0, chunkZ*16), EventType.CUSTOM)){
				for(int i = 0, j = ModCfg.aquamarineCountInChunk; i<j; i++)
					aquamarineGen.generate(world, random, pos(random, chunkX, chunkZ, ModCfg.aquamarineMinY, ModCfg.aquamarineMaxY));
			}
		}
		if(ModCfg.amethystGen){
			WorldGenMinable amethystGen = getAmethystGen();
			if(TerrainGen.generateOre(world, random, amethystGen, new BlockPos(chunkX*16, 0, chunkZ*16), EventType.CUSTOM)){
				for(int i = 0, j = ModCfg.amethystCountInChunk; i<j; i++)
					amethystGen.generate(world, random, pos(random, chunkX, chunkZ, ModCfg.amethystMinY, ModCfg.amethystMaxY));
			}
		}
	}

	private static BlockPos pos(Random random, int chunkX, int chunkZ, int minY, int maxY){
		return new BlockPos(chunkX*16+random.nextInt(16),
				random.nextInt(maxY-minY)+minY,
				chunkZ*16+random.nextInt(16));
	}
}
