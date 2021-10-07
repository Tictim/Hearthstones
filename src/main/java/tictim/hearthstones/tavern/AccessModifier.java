package tictim.hearthstones.tavern;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

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

	private TranslatableComponent text;

	public Component text(){
		if(text==null) text = new TranslatableComponent("info.hearthstones.access."+this.name().toLowerCase());
		return text;
	}

	public static AccessModifier of(int index){
		AccessModifier[] values = AccessModifier.values();
		return values[index%values.length];
	}
}
