package tictim.hearthstones.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModSounds;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class AmethystBlock extends Block{
	public AmethystBlock(){
		this(ModBlocks.ModMaterials.AMETHYST);
	}
	public AmethystBlock(Material material){
		super(material);
		this.setHarvestLevel("pickaxe", 0);
	}

	@Override public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity){
		if(world.isRemote) return;
		if(entity instanceof EntityFishHook) playFunnySound(world, pos); // yes it doesn't work great. yes it could spam sound like mf. no it's a feature.
	}

	public static void playFunnySound(World world, BlockPos pos){
		world.playSound(null, pos, ModSounds.AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1, 0.5f+world.rand.nextFloat()*1.2f);
		world.playSound(null, pos, ModSounds.AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 0.5f+world.rand.nextFloat()*1.2f);
	}

	@SubscribeEvent
	public static void onAmethystProjectileHit(ProjectileImpactEvent event){
		RayTraceResult r = event.getRayTraceResult();
		if(r.typeOfHit==RayTraceResult.Type.BLOCK){
			World world = event.getEntity().world;
			BlockPos pos = r.getBlockPos();
			Block block = world.getBlockState(pos).getBlock();
			if(block==ModBlocks.AMETHYST_BLOCK||
					block==ModBlocks.BUDDING_AMETHYST||
					block==ModBlocks.AMETHYST_CLUSTER||
					block==ModBlocks.SMALL_AMETHYST_BUD||
					block==ModBlocks.MEDIUM_AMETHYST_BUD||
					block==ModBlocks.LARGE_AMETHYST_BUD)
				playFunnySound(world, pos);
		}
	}
}
