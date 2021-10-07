package tictim.hearthstones.hearthstone;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tictim.hearthstones.capability.PlayerTavernMemory;
import tictim.hearthstones.capability.TavernMemory;

import javax.annotation.Nullable;

public final class WarpContext{
	private final ItemStack stack;
	private final Player player;
	@Nullable private final InteractionHand hand;

	@Nullable private PlayerTavernMemory memory;

	public WarpContext(ItemStack stack, Player player){
		this(stack, player, null);
	}
	public WarpContext(ItemStack stack, Player player, @Nullable InteractionHand hand){
		this.stack = stack;
		this.player = player;
		this.hand = hand;
	}

	public ItemStack getStack(){
		return stack;
	}
	public Player getPlayer(){
		return player;
	}
	@Nullable public InteractionHand getHand(){
		return hand;
	}
	public PlayerTavernMemory getMemory(){
		if(memory==null) memory = TavernMemory.expectFromPlayer(player);
		return memory;
	}

	public boolean hasCooldown(){
		return !player.isCreative()&&getMemory().hasCooldown();
	}
}
