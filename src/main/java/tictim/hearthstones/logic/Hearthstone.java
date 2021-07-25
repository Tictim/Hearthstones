package tictim.hearthstones.logic;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.ITextComponent;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.utils.HearthingContext;

import javax.annotation.Nullable;

public interface Hearthstone{
	int getMaxDamage();

	default void onWarp(HearthingContext ctx){
		ctx.warpEntity(ctx.getPlayer());
	}
	default void applyDamage(HearthingContext ctx){
		ctx.getStack().hurtAndBreak(1, ctx.getPlayer(), player -> player.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
	}

	@Nullable TavernRecord getDestination(HearthingContext ctx);
	int getCooldown(HearthingContext ctx);

	ITextComponent invalidDestinationError();
	ITextComponent noSelectionError();
	ITextComponent guideText();
}
