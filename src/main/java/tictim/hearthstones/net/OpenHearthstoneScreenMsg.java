package tictim.hearthstones.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;

public record OpenHearthstoneScreenMsg(
		PlayerTavernMemory playerMemory,
		TavernMemory globalMemory,
		boolean isHearthingGem
){
	public static OpenHearthstoneScreenMsg read(FriendlyByteBuf buf){
		PlayerTavernMemory playerMemory = new PlayerTavernMemory();
		playerMemory.read(buf);
		TavernMemory globalMemory = new TavernMemory();
		globalMemory.read(buf);
		return new OpenHearthstoneScreenMsg(playerMemory, globalMemory, buf.readBoolean());
	}

	public OpenHearthstoneScreenMsg(Player player, boolean isHearthingGem){
		this(TavernMemories.player(player),
				TavernMemories.global().taverns().values().stream()
						.filter(tavernRecord -> tavernRecord.hasAccessPermission(player))
						.collect(TavernMemory::new, TavernMemory::addOrUpdate, (tavernMemory, tavernMemory2) -> {}),
				isHearthingGem);
	}

	public void write(FriendlyByteBuf buf){
		playerMemory.write(buf);
		globalMemory.write(buf);
		buf.writeBoolean(isHearthingGem);
	}
}
