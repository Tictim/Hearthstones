package tictim.hearthstones.tavern;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface Tavern{
	TavernPos pos();
	BlockPos blockPos();

	TavernType type();
	Owner owner();
	AccessModifier access();

	@Nullable Component name();

	boolean isMissing();

	default boolean hasAccessPermission(Player player){
		return access().hasAccessPermission(player, owner());
	}
	default boolean hasModifyPermission(Player player){
		return access().hasModifyPermission(player, owner());
	}
	default Accessibility getAccessibility(Player player){
		return hasModifyPermission(player) ?
				owner().hasOwner()&&owner().isOwnerOrOp(player) ? Accessibility.MODIFIABLE : Accessibility.PARTIALLY_MODIFIABLE :
				Accessibility.READ_ONLY;
	}

	default TavernRecord toRecord(){
		return new TavernRecord(this);
	}
	default Tavern withMissingSet(boolean missing){
		return isMissing()==missing ? this : new TavernRecord(this, missing);
	}
}
