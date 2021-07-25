package tictim.hearthstones.logic;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.Component;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.utils.HearthingContext;

import javax.annotation.Nullable;

public interface Hearthstone{
	int getMaxDamage();

	default void onWarp(HearthingContext ctx){
		ctx.warpEntity(ctx.getPlayer());
	}
	default void applyDamage(HearthingContext ctx){
		ctx.getStack().hurtAndBreak(1, ctx.getPlayer(), player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
	}

	@Nullable TavernRecord getDestination(HearthingContext ctx);
	int getCooldown(HearthingContext ctx);

	Component invalidDestinationError();
	Component noSelectionError();
	Component guideText();
}
