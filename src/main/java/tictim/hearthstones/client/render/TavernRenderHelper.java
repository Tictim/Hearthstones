package tictim.hearthstones.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import tictim.hearthstones.client.screen.TavernScreen;
import tictim.hearthstones.utils.AccessModifier;
import tictim.hearthstones.utils.TavernType;

public final class TavernRenderHelper{
	private TavernRenderHelper(){}

	public static void renderAccess(PoseStack pose, AccessModifier access){
		RenderSystem.setShaderTexture(0, switch(access){
			case PUBLIC -> TavernScreen.ACCESS_PUBLIC;
			case PROTECTED -> TavernScreen.ACCESS_PROTECTED;
			case TEAM -> TavernScreen.ACCESS_TEAM;
			case PRIVATE -> TavernScreen.ACCESS_PRIVATE;
		});
		GuiComponent.blit(pose, 0, 0, 0, 0, 32, 32, 32, 32);
	}

	public static void renderTavernUIBase(PoseStack pose, TavernType type, boolean selected){
		RenderSystem.setShaderTexture(0, type.tavernUITexture);
		GuiComponent.blit(pose, 0, 0, 0, selected ? 20*2 : 0, 179*2, 20*2, 512, 512);
	}
}
