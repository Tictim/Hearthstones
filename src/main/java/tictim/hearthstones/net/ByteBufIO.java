package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.UUID;

public class ByteBufIO{
	public static void writeID(ByteBuf buf, ResourceLocation resourceLocation){
		ByteBufUtils.writeUTF8String(buf, resourceLocation.toString());
	}
	public static void writePos(ByteBuf buf, BlockPos pos){
		buf.writeInt(pos.getX()).writeInt(pos.getY()).writeInt(pos.getZ());
	}
	public static void writeUUID(ByteBuf buf, UUID uuid){
		buf.writeLong(uuid.getMostSignificantBits()).writeLong(uuid.getLeastSignificantBits());
	}

	public static ResourceLocation readID(ByteBuf buf){
		return new ResourceLocation(ByteBufUtils.readUTF8String(buf));
	}
	public static BlockPos readPos(ByteBuf buf){
		return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
	public static UUID readUUID(ByteBuf buf){
		return new UUID(buf.readLong(), buf.readLong());
	}
}
