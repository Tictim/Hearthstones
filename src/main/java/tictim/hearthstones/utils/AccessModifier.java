package tictim.hearthstones.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import tictim.hearthstones.data.Owner;

public enum AccessModifier{
	PUBLIC,
	PROTECTED,
	TEAM,
	PRIVATE;

	public boolean hasAccessPermission(PlayerEntity player, Owner owner){
		switch(this){
			case TEAM:
				return owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PRIVATE:
				return owner.isOwnerOrOp(player);
			default:
				return true; // case PUBLIC: case PROTECTED:
		}
	}

	public boolean hasModifyPermission(PlayerEntity player, Owner owner){
		switch(this){
			case TEAM:
				return owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PROTECTED:
			case PRIVATE:
				return owner.isOwnerOrOp(player);
			default:
				return true; // case PUBLIC:
		}
	}

	private String localizeKey;

	public String localize(){
		if(localizeKey==null) localizeKey = "info.hearthstones.access."+this.name().toLowerCase();
		return I18n.format(localizeKey); // TODO check whether this code explodes or not
	}

	public static AccessModifier fromMeta(int meta){
		AccessModifier[] values = AccessModifier.values();
		return values[meta%values.length];
	}
}
