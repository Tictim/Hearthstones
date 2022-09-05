package tictim.hearthstones.hearthstone;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.FMLCommonHandler;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public final class HearthUtils{
	private HearthUtils(){}

	private static final Random SOUND_RNG = new Random();

	@Nullable
	public static TavernTile getTavernAt(TavernPos pos){
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(server==null) return null;
		World world = DimensionManager.getWorld(pos.dim(), true);
		if(world==null) return null;
		TileEntity te = world.getTileEntity(pos.pos());
		return te instanceof TavernTile ? (TavernTile)te : null;
	}

	public static BlockPos getWarpPos(TavernTile tavern){
		IBlockState state = tavern.getWorld().getBlockState(tavern.blockPos());
		EnumFacing facing = null;
		if(state.getPropertyKeys().contains(BlockHorizontal.FACING)){
			facing = state.getValue(BlockHorizontal.FACING);
			BlockPos pos2 = tavern.blockPos().offset(facing);
			if(hasRoomForPlayer(tavern.getWorld(), pos2)) return pos2;
		}
		MutableBlockPos mpos = new MutableBlockPos();
		for(int i = 0; i<4; i++){
			EnumFacing current = EnumFacing.byHorizontalIndex(i);
			if(facing!=current){
				mpos.setPos(tavern.blockPos()).move(current);
				if(hasRoomForPlayer(tavern.getWorld(), mpos)) return mpos.toImmutable();
			}
		}
		return hasRoomForPlayer(tavern.getWorld(),
				mpos.setPos(tavern.blockPos())) ? mpos.toImmutable() : mpos.up();
	}

	private static boolean hasRoomForPlayer(World world, BlockPos pos){
		BlockPos down = pos.down();
		return world.getBlockState(down).isSideSolid(world, down, EnumFacing.UP)&&
				isNotSolidNorLiquid(world.getBlockState(pos))&&
				isNotSolidNorLiquid(world.getBlockState(pos.up()));
	}
	private static boolean isNotSolidNorLiquid(IBlockState blockState){
		return !blockState.getMaterial().isSolid()&&!blockState.getMaterial().isLiquid();
	}

	public static void warp(Entity entity, int destDimension, BlockPos destPos, boolean playSound){
		if(ModCfg.traceHearthstoneUsage)
			log("Moving Entity {} to {}:{}", entity, destDimension, destPos);
		entity.fallDistance = 0f;

		World originalWorld = entity.world;
		double originX = entity.posX;
		double originY = entity.posY;
		double originZ = entity.posZ;
		double destX = destPos.getX()+.5;
		double destY = destPos.getY();
		double destZ = destPos.getZ()+.5;
		boolean inSameDimension = entity.world.provider.getDimension()==destDimension;
		WorldServer destWorld = inSameDimension ?
				originalWorld instanceof WorldServer ? (WorldServer)originalWorld : null :
				FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(destDimension);
		if(destWorld==null){
			Hearthstones.LOGGER.error("World {} doesn't exists.", destDimension);
			return;
		}

		if(entity instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP)entity;
			player.dismountRidingEntity();
			if(player.isPlayerSleeping()) player.wakeUpPlayer(true, true, false);
			if(inSameDimension) player.connection.setPlayerLocation(destX, destY, destZ, player.rotationYaw, player.rotationPitch, Collections.emptySet());
			else player.server.getPlayerList().transferPlayerToDimension(player, destDimension, new Tp(destX, destY, destZ));
		}else{
			entity.dismountRidingEntity();
			if(inSameDimension) entity.setLocationAndAngles(destX, destY, destZ, entity.rotationYaw, entity.rotationPitch);
			else entity.changeDimension(destDimension, new Tp(destX, destY, destZ));
		}
		if(playSound){
			playSound(originalWorld, originX, originY, originZ);
			playSound(destWorld, destX, destY, destZ);
		}
		if(ModCfg.traceHearthstoneUsage)
			log("Moved Entity {} to {}:{}", entity, destDimension, destPos);
	}

	private static void log(String message, Entity entity, int destDimension, BlockPos destPos){
		Hearthstones.LOGGER.info(message, entity, destDimension, destPos);
		if(entity.isBeingRidden())
			Hearthstones.LOGGER.info("Passengers: {}",
					entity.getPassengers().stream()
							.map(Entity::toString)
							.collect(Collectors.joining(", ")));
		if(entity.isRiding())
			Hearthstones.LOGGER.info("Entity riding: {}", entity.getRidingEntity());
	}

	private static void playSound(World world, double x, double y, double z){
		world.playSound(null, x, y, z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
	}

	private static final class Tp implements ITeleporter{
		private final double x, y, z;

		private Tp(double x, double y, double z){
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override public void placeEntity(World world, Entity entity, float yaw){
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			entity.motionX = 0.0D;
			entity.motionY = 0.0D;
			entity.motionZ = 0.0D;
		}
	}
}
