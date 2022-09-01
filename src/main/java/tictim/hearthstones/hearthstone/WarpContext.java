package tictim.hearthstones.hearthstone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;

public final class WarpContext{
	private final ItemStack stack;
	private final EntityPlayer player;

	@Nullable private PlayerTavernMemory memory;

	public WarpContext(ItemStack stack, EntityPlayer player){
		this.stack = stack;
		this.player = player;
	}

	public ItemStack getStack(){
		return stack;
	}
	public EntityPlayer getPlayer(){
		return player;
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
		getStack().damageItem(i, getPlayer());
	}
}