package tictim.hearthstones.logic;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Nameable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.utils.HearthingContext;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;

public interface Tavern extends Nameable{
	Level world();
	BlockPos pos();

	TavernType tavernType();

	Owner owner();

	void setName(@Nullable Component name);

	boolean canBeUpgraded();
	ItemStack createUpgradeItem();

	default boolean canTeleportTo(HearthingContext ctx){
		return owner().hasAccessPermission(ctx.getPlayer());
	}
	default TavernPos tavernPos(){
		return new TavernPos(world(), pos());
	}
}
