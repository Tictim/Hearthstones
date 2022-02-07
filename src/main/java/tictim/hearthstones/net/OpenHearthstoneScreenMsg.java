package tictim.hearthstones.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;

public class OpenHearthstoneScreenMsg implements IMessage{
	private final PlayerTavernMemory playerMemory;
	private final TavernMemory globalMemory;
	private boolean isHearthingGem;

	public OpenHearthstoneScreenMsg(){
		this.playerMemory = new PlayerTavernMemory();
		this.globalMemory = new TavernMemory();
	}

	public OpenHearthstoneScreenMsg(PlayerTavernMemory playerMemory, TavernMemory globalMemory, boolean isHearthingGem){
		this.playerMemory = playerMemory;
		this.globalMemory = globalMemory;
		this.isHearthingGem = isHearthingGem;
	}

	public OpenHearthstoneScreenMsg(EntityPlayer player, boolean isHearthingGem){
		this(TavernMemories.player(player),
				TavernMemories.global().taverns().values().stream()
						.filter(tavernRecord -> tavernRecord.hasAccessPermission(player))
						.collect(TavernMemory::new, TavernMemory::addOrUpdate, (tavernMemory, tavernMemory2) -> {}),
				isHearthingGem);
	}

	public PlayerTavernMemory getPlayerMemory(){
		return playerMemory;
	}
	public TavernMemory getGlobalMemory(){
		return globalMemory;
	}
	public boolean isHearthingGem(){
		return isHearthingGem;
	}

	@Override public void fromBytes(ByteBuf buf){
		playerMemory.read(buf);
		globalMemory.read(buf);
		isHearthingGem = buf.readBoolean();
	}
	@Override public void toBytes(ByteBuf buf){
		playerMemory.write(buf);
		globalMemory.write(buf);
		buf.writeBoolean(isHearthingGem);
	}
}
