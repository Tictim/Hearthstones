package tictim.hearthstones.tavern;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

import static net.minecraftforge.fml.common.network.ByteBufUtils.readUTF8String;
import static net.minecraftforge.fml.common.network.ByteBufUtils.writeUTF8String;
import static tictim.hearthstones.net.ByteBufIO.readUUID;
import static tictim.hearthstones.net.ByteBufIO.writeUUID;

public final class Owner{
	public static final Owner NO_OWNER = new Owner();

	public static Owner of(EntityPlayer player){
		return new Owner(player);
	}
	public static Owner of(UUID owner, String name){
		return new Owner(Objects.requireNonNull(owner), Objects.requireNonNull(name));
	}
	public static Owner read(NBTTagCompound tag){
		if(tag.hasUniqueId("id")) return new Owner(tag.getUniqueId("id"), tag.getString("name"));
		else return NO_OWNER;
	}
	public static Owner read(ByteBuf buf){
		if(buf.readBoolean()) return new Owner(readUUID(buf), readUTF8String(buf));
		else return NO_OWNER;
	}

	@Nullable private final UUID id;
	private final String name;

	private Owner(){
		this(null, "");
	}
	private Owner(EntityPlayer player){
		this.id = EntityPlayer.getUUID(player.getGameProfile());
		this.name = player.getGameProfile().getName();
	}
	private Owner(@Nullable UUID owner, String name){
		this.id = owner;
		this.name = owner==null ? "" : name;
	}

	public boolean isOwner(EntityPlayer player){
		return id==null||id.equals(EntityPlayer.getUUID(player.getGameProfile()));
	}
	public boolean isOwnerOrOp(EntityPlayer player){
		return isOwner(player)||isOp(player);
	}

	public boolean isSameTeam(EntityPlayer player){
		if(id==null) return false;
		Team t1 = player.world.getScoreboard().getPlayersTeam(name), t2 = player.getTeam();
		return t1!=null&&t1.isSameTeam(t2);
	}

	@Nullable public UUID getId(){
		return this.id;
	}
	public boolean hasOwner(){
		return this.id!=null;
	}
	public String getName(){
		return this.name;
	}

	public NBTTagCompound write(){
		NBTTagCompound nbt = new NBTTagCompound();
		if(this.id!=null){
			nbt.setUniqueId("id", this.id);
			nbt.setString("name", this.name);
		}
		return nbt;
	}
	public void write(ByteBuf buf){
		if(hasOwner()){
			buf.writeBoolean(true);
			writeUUID(buf, id);
			writeUTF8String(buf, name);
		}else buf.writeBoolean(false);
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Owner owner = (Owner)o;
		return Objects.equals(id, owner.id);
	}
	@Override public int hashCode(){
		return Objects.hash(id);
	}

	@Override public String toString(){
		return this.hasOwner() ? this.name+" ("+this.id+")" : "No Owner";
	}

	private static boolean isOp(EntityPlayer player){
		MinecraftServer server = player.getServer();
		if(server==null){
			if(player.world.isRemote)
				return Client.isOp(player); // TODO does this really work
			else return false;
		}else return server.getPlayerList().getOppedPlayers().getPermissionLevel(player.getGameProfile())>=1;
	}

	private static final class Client{
		public static boolean isOp(EntityPlayer player){
			if(player instanceof EntityPlayerSP){
				return ((EntityPlayerSP)player).getPermissionLevel()>=1;
			}
			return false;
		}
	}
}