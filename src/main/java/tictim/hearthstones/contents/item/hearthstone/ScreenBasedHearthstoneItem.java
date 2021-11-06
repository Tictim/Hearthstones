package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import tictim.hearthstones.hearthstone.Hearthstone;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenHearthstoneScreenMsg;

public class ScreenBasedHearthstoneItem extends HearthstoneItem{
	public ScreenBasedHearthstoneItem(Properties properties, Hearthstone hearthstone){
		super(properties, hearthstone);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
		if(!player.isSecondaryUseActive()) return super.use(level, player, hand);
		if(!level.isClientSide&&player instanceof ServerPlayer sp)
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new OpenHearthstoneScreenMsg(player, isHearthingGem()));
		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	protected boolean isHearthingGem(){
		return false;
	}
}
