package tictim.hearthstones.data;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.util.INBTSerializable;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.utils.AccessModifier;
import tictim.hearthstones.utils.Accessibility;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public final class Owner implements INBTSerializable<CompoundTag>, Comparable<Owner>{
	private String ownerName = "";
	@Nullable
	private UUID ownerId;
	private AccessModifier access = AccessModifier.PUBLIC;

	public Owner(){}
	public Owner(@Nullable Player player){
		if(player!=null) setOwner(player);
	}
	public Owner(@Nullable UUID owner, @Nullable String name){
		setOwner(owner, name);
	}
	public Owner(CompoundTag nbt){
		deserializeNBT(nbt);
	}

	public void reset(Owner owner){
		this.ownerName = owner.ownerName;
		this.ownerId = owner.ownerId;
		this.access = owner.access;
	}

	public boolean isOwner(Player player){
		return ownerId==null||ownerId.equals(Player.createPlayerUUID(player.getGameProfile()));
	}
	public boolean isOwnerOrOp(Player player){
		return isOwner(player)||Hearthstones.PROXY.isOp(player);
	}

	public boolean isSameTeam(Player player){
		if(ownerId==null) return false;
		Team t1 = player.level.getScoreboard().getPlayersTeam(ownerName), t2 = player.getTeam();
		return t1!=null&&t1.isAlliedTo(t2);
	}

	public boolean hasOwner(){
		return this.ownerId!=null;
	}
	public String getOwnerName(){
		return this.ownerName;
	}

	@Nullable
	public UUID getOwnerUUID(){
		return this.ownerId;
	}
	public void setOwner(@Nullable Player player){
		setOwner(player==null ? null : Player.createPlayerUUID(player.getGameProfile()), player==null ? "" : player.getGameProfile().getName());
	}
	public void setOwner(@Nullable UUID owner, @Nullable String name){
		if(this.ownerId==null&&owner!=null) access = AccessModifier.PRIVATE;
		this.ownerId = owner;
		this.ownerName = owner==null||name==null ? "" : name;
	}

	public AccessModifier getAccessModifier(){
		return access;
	}
	public void setAccessModifier(AccessModifier access){
		this.access = Objects.requireNonNull(access);
	}
	public boolean hasAccessPermission(Player player){
		return access.hasAccessPermission(player, this);
	}
	public boolean hasModifyPermission(Player player){
		return access.hasModifyPermission(player, this);
	}

	public Accessibility getAccessibility(Player player){
		return hasModifyPermission(player) ? hasOwner()&&isOwnerOrOp(player) ? Accessibility.MODIFIABLE : Accessibility.PARTIALLY_MODIFIABLE : Accessibility.READ_ONLY;
	}

	public void reset(){
		this.ownerName = "";
		this.ownerId = null;
		this.access = AccessModifier.PUBLIC;
	}

	@Override
	public CompoundTag serializeNBT(){
		CompoundTag nbt = new CompoundTag();
		if(this.ownerId!=null){
			nbt.putUUID("owner", this.ownerId);
			nbt.putString("ownerName", this.ownerName);
		}
		nbt.putByte("access", (byte)access.ordinal());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt){
		if(nbt.hasUUID("owner")){
			this.ownerId = nbt.getUUID("owner");
			this.ownerName = nbt.getString("ownerName");
		}else{
			this.ownerId = null;
			this.ownerName = "";
		}
		AccessModifier[] values = AccessModifier.values();
		access = values[Byte.toUnsignedInt(nbt.getByte("access"))%values.length];
	}

	@Override
	public String toString(){
		return this.hasOwner() ? this.ownerName+" ("+this.ownerId+")" : "No Owner";
	}

	@Override
	public boolean equals(Object o){
		if(o==this) return true;
		else if(o instanceof Owner){
			Owner o2 = (Owner)o;
			return Objects.equals(o2.ownerId, ownerId)&&access==o2.access;
		}else return false;
	}

	@Override
	public int compareTo(Owner o){
		// #1 hasOwner, true first
		int i = Boolean.compare(hasOwner(), o.hasOwner());
		if(i!=0) return -i;
		else if(!hasOwner()) return 0;
		// #2 owner name
		i = getOwnerName().compareTo(o.getOwnerName());
		if(i!=0) return i;
		// #3 uuid
		return getOwnerUUID().compareTo(o.getOwnerUUID());
	}
}
