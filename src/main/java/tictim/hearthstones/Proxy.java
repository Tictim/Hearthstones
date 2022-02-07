package tictim.hearthstones;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import tictim.hearthstones.client.TavernItemRenderer;
import tictim.hearthstones.client.TavernTileRenderer;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.tileentity.TavernTile;

public class Proxy{
	public void registerRenderer(){}

	@SuppressWarnings("unused")
	public static final class Client extends Proxy{
		@Override public void registerRenderer(){
			ClientRegistry.bindTileEntitySpecialRenderer(TavernTile.class, new TavernTileRenderer());
			ModItems.TAVERN.setTileEntityItemStackRenderer(new TavernItemRenderer());
		}
	}
}
