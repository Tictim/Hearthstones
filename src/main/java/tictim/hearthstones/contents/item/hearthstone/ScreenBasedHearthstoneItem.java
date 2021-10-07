package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tictim.hearthstones.capability.TavernMemory;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.hearthstone.Hearthstone;

public class ScreenBasedHearthstoneItem extends HearthstoneItem{
	public ScreenBasedHearthstoneItem(Properties properties, Hearthstone hearthstone){
		super(properties, hearthstone);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
		if(!player.isShiftKeyDown()) return super.use(level, player, hand);
		if(level.isClientSide) openHearthstoneScreen(player);
		else if(player instanceof ServerPlayer) TavernMemory.expectFromPlayer(player).sync();
		return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
	}

	protected void openHearthstoneScreen(Player player){
		if(player==Minecraft.getInstance().player)
			Client.openHearthstoneScreen(false);
	}

	protected static final class Client{
		private Client(){}

		public static void openHearthstoneScreen(boolean hearthingGem){
			Minecraft.getInstance().setScreen(new HearthstoneScreen(hearthingGem));
		}
	}
}
