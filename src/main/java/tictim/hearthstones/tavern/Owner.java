package tictim.hearthstones.tavern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import tictim.hearthstones.Hearthstones;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public final class Owner{
	public static final Owner NO_OWNER = new Owner();

	public static Owner of(Player player){
		return new Owner(player);
	}
	public static Owner read(CompoundTag tag){
		if(tag.hasUUID("id")) return new Owner(tag.getUUID("id"), tag.getString("name"));
		else return NO_OWNER;
	}
	public static Owner read(FriendlyByteBuf buf){
		if(buf.readBoolean()) return new Owner(buf.readUUID(), buf.readUtf());
		else return NO_OWNER;
	}

	@Nullable private final UUID id;
	private final String name;

	private Owner(){
		this(null, "");
	}
	private Owner(Player player){
		this.id = Player.createPlayerUUID(player.getGameProfile());
		this.name = player.getGameProfile().getName();
	}
	private Owner(@Nullable UUID owner, String name){
		this.id = owner;
		this.name = owner==null ? "" : name;
	}

	public boolean isOwner(Player player){
		return id==null||id.equals(Player.createPlayerUUID(player.getGameProfile()));
	}
	public boolean isOwnerOrOp(Player player){
		return isOwner(player)||player.hasPermissions(1); // TODO test if it works
	}

	public boolean isSameTeam(Player player){
		if(id==null) return false;
		Team t1 = player.level.getScoreboard().getPlayersTeam(name), t2 = player.getTeam();
		return t1!=null&&t1.isAlliedTo(t2);
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

	public CompoundTag write(){
		CompoundTag nbt = new CompoundTag();
		if(this.id!=null){
			nbt.putUUID("id", this.id);
			nbt.putString("name", this.name);
		}
		return nbt;
	}
	public void write(FriendlyByteBuf buf){
		if(hasOwner()){
			buf.writeBoolean(true);
			buf.writeUUID(id);
			buf.writeUtf(name);
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
}
