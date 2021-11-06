package tictim.hearthstones.hearthstone;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernPos;

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
		if(memory==null) memory = TavernMemories.player(player);
		return memory;
	}

	@Nullable public Tavern getSelectedTavern(){
		TavernPos selectedPos = getMemory().getSelectedPos();
		if(selectedPos==null) return null;
		Tavern t = getMemory().get(selectedPos);
		return t!=null ? t : TavernMemories.global().get(selectedPos);
	}

	public boolean hasCooldown(){
		return !player.isCreative()&&getMemory().hasCooldown();
	}
	public void hurtItem(int i){
		getStack().hurtAndBreak(i,
				getPlayer(),
				player -> onItemBreak());
	}

	public void onItemBreak(){
		if(getHand()!=null) player.broadcastBreakEvent(getHand());
	}
}
