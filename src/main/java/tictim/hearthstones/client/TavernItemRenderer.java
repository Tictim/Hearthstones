package tictim.hearthstones.client;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import tictim.hearthstones.contents.item.TavernItem;
import tictim.hearthstones.contents.tileentity.TavernTile;

public class TavernItemRenderer extends TileEntityItemStackRenderer{
	@Override public void renderByItem(ItemStack stack, float partialTicks){
		TavernRenderer.renderItem(TavernItem.type(stack),
				TavernTile.readItemSkin(stack),
				stack,
				TileEntityRendererDispatcher.instance.renderEngine);
	}
}
