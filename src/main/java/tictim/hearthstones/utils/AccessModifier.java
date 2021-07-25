package tictim.hearthstones.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import tictim.hearthstones.data.Owner;

public enum AccessModifier{
	PUBLIC,
	PROTECTED,
	TEAM,
	PRIVATE;

	public boolean hasAccessPermission(Player player, Owner owner){
		return switch(this){
			case TEAM -> owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PRIVATE -> owner.isOwnerOrOp(player);
			default -> true; // case PUBLIC: case PROTECTED:
		};
	}

	public boolean hasModifyPermission(Player player, Owner owner){
		return switch(this){
			case TEAM -> owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PROTECTED, PRIVATE -> owner.isOwnerOrOp(player);
			default -> true; // case PUBLIC:
		};
	}

	private TranslatableComponent localized;

	public Component toTextComponent(){
		if(localized==null) localized = new TranslatableComponent("info.hearthstones.access."+this.name().toLowerCase());
		return localized;
	}

	public static AccessModifier fromMeta(int meta){
		AccessModifier[] values = AccessModifier.values();
		return values[meta%values.length];
	}
}
