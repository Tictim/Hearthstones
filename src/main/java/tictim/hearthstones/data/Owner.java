package tictim.hearthstones.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.common.util.INBTSerializable;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.utils.AccessModifier;
import tictim.hearthstones.utils.Accessibility;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public final class Owner implements INBTSerializable<CompoundNBT>, Comparable<Owner>{
	private String ownerName = "";
	@Nullable
	private UUID ownerId;
	private AccessModifier access = AccessModifier.PUBLIC;

	public Owner(){}
	public Owner(@Nullable PlayerEntity player){
		if(player!=null) setOwner(player);
	}
	public Owner(@Nullable UUID owner, @Nullable String name){
		setOwner(owner, name);
	}
	public Owner(CompoundNBT nbt){
		deserializeNBT(nbt);
	}

	public void reset(Owner owner){
		this.ownerName = owner.ownerName;
		this.ownerId = owner.ownerId;
		this.access = owner.access;
	}

	public boolean isOwner(PlayerEntity player){
		return ownerId==null||ownerId.equals(PlayerEntity.getUUID(player.getGameProfile()));
	}
	public boolean isOwnerOrOp(PlayerEntity player){
		return isOwner(player)||Hearthstones.PROXY.isOp(player);
	}

	public boolean isSameTeam(PlayerEntity player){
		if(ownerId==null) return false;
		Team t1 = player.world.getScoreboard().getPlayersTeam(ownerName), t2 = player.getTeam();
		return t1!=null&&t1.isSameTeam(t2);
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
	public void setOwner(@Nullable PlayerEntity player){
		setOwner(player==null ? null : PlayerEntity.getUUID(player.getGameProfile()), player==null ? "" : player.getGameProfile().getName());
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
	public boolean hasAccessPermission(PlayerEntity player){
		return access.hasAccessPermission(player, this);
	}
	public boolean hasModifyPermission(PlayerEntity player){
		return access.hasModifyPermission(player, this);
	}

	public Accessibility getAccessibility(PlayerEntity player){
		return hasModifyPermission(player) ? hasOwner()&&isOwnerOrOp(player) ? Accessibility.MODIFIABLE : Accessibility.PARTIALLY_MODIFIABLE : Accessibility.READ_ONLY;
	}

	public void reset(){
		this.ownerName = "";
		this.ownerId = null;
		this.access = AccessModifier.PUBLIC;
	}

	@Override
	public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(this.ownerId!=null){
			nbt.putUniqueId("owner", this.ownerId);
			nbt.putString("ownerName", this.ownerName);
		}
		nbt.putByte("access", (byte)access.ordinal());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt){
		if(nbt.hasUniqueId("owner")){
			this.ownerId = nbt.getUniqueId("owner");
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
