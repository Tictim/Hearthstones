package tictim.hearthstones.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.utils.HearthingContext;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;

public interface Tavern extends INameable{
	World world();
	BlockPos pos();

	TavernType tavernType();

	Owner owner();

	void setName(@Nullable ITextComponent name);

	boolean canBeUpgraded();
	ItemStack createUpgradeItem();

	default boolean canTeleportTo(HearthingContext ctx){
		return owner().hasAccessPermission(ctx.getPlayer());
	}
	default TavernPos tavernPos(){
		return new TavernPos(world(), pos());
	}
}
