package tictim.hearthstones.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.client.utils.TavernSign;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.logic.CompanionHearthstone;
import tictim.hearthstones.logic.HearthstoneItem;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.HearthingContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, value = Dist.CLIENT)
public final class OverlayRenderEventHandler{
	private OverlayRenderEventHandler(){}

	@SubscribeEvent
	public static void renderEvent(RenderGameOverlayEvent.Post e){
		if(e.getType()==RenderGameOverlayEvent.ElementType.CROSSHAIRS&&Minecraft.getInstance().currentScreen==null){
			PlayerEntity p = Minecraft.getInstance().player;
			if(p!=null&&p.isAlive()){
				if(p.getHeldItemMainhand().getItem() instanceof HearthstoneItem) drawHearthstoneUI(e, p, Hand.MAIN_HAND);
				else if(p.getHeldItemOffhand().getItem() instanceof HearthstoneItem) drawHearthstoneUI(e, p, Hand.OFF_HAND);
				drawTavernSign(p);
			}
		}
	}

	private static void drawHearthstoneUI(RenderGameOverlayEvent.Post event, PlayerEntity player, Hand hand){
		Minecraft mc = Minecraft.getInstance();
		MainWindow window = event.getWindow();
		int width = window.getScaledWidth(), height = window.getScaledHeight();
		int left = width/2, top = height/2;
		HearthingContext ctx = new HearthingContext(player, hand);
		TavernRecord t = ctx.getDestinationMemory();
		RenderSystem.disableBlend();
		if(t!=null){
			RenderSystem.pushMatrix();
			RenderSystem.color4f(1, 1, 1, 1);
			RenderSystem.enableRescaleNormal();

			mc.getItemRenderer().renderItemAndEffectIntoGUI(player, t.getTavernType().stackForRender(), left-8, top-55);
			RenderSystem.popMatrix();
			RenderHelper.disableStandardItemLighting();

			drawCentered(I18n.format("info.hearthstones.hearthstone.dest"), left, top-36);
			TavernSign sign = TavernSign.of(t);
			drawCentered(sign.nameAndDistance(), left, top-25);
			String ownerText = sign.owner();
			String positionText = sign.position();
			int ownerWidth = mc.fontRenderer.getStringWidth(ownerText);
			int positionWidth = mc.fontRenderer.getStringWidth(positionText);
			int widthSum = ownerWidth+mc.fontRenderer.getStringWidth(" ")+positionWidth;
			mc.fontRenderer.drawStringWithShadow(ownerText, left-widthSum/2, top-14, 0xF0F0F0);
			mc.fontRenderer.drawString(positionText, left+widthSum/2-positionWidth, top-14, 0xF0F0F0);

			if(player.isHandActive()&&player.getActiveHand()==hand){
				double ratio = MathHelper.clamp((double)player.getItemInUseCount()/player.getActiveItemStack().getUseDuration(), 0, 1);
				drawCentered(I18n.format("info.hearthstones.hearthstone.hearthing"), left, top+22);
				GuiUtils.drawGradientRect(-90, left-32, top+33, left+32, top+43, 0xFF5f5f5f, 0xFF5f5f5f);
				GuiUtils.drawGradientRect(-90, left-32, top+33, left+32-((int)(ratio*64)), top+43, 0xFF02ccfc, 0xFF02ccfc);
			}
		}else drawCentered(ctx.getHearthstone().guideText().getFormattedText(), left, top-33);
		if(ctx.getStack().getItem()==ModItems.COMPANION_HEARTHSTONE.get()){
			Set<Entity> entities = CompanionHearthstone.getWarpTargets(ctx);
			if(!entities.isEmpty()){
				List<String> strs = mc.fontRenderer.listFormattedStringToWidth(
						I18n.format("info.hearthstones.companion_hearthstone.companions")+"\n"+entities.stream().map(e -> e.getDisplayName().getFormattedText()).collect(Collectors.joining("\n")), width/4);
				for(int i = 0; i<strs.size(); i++){
					drawRightAligned(strs.get(i), width-8, top-(strs.size()*mc.fontRenderer.FONT_HEIGHT)/2+i*mc.fontRenderer.FONT_HEIGHT);
				}
			}
		}
		RenderSystem.enableBlend();
	}

	private static void drawTavernSign(PlayerEntity player){
		RayTraceResult ray = Minecraft.getInstance().objectMouseOver;
		if(ray!=null&&ray.getType()==RayTraceResult.Type.BLOCK){
			BlockRayTraceResult blockRay = (BlockRayTraceResult)ray;
			TileEntity te = player.world.getTileEntity(blockRay.getPos());
			if(te instanceof Tavern){
				Tavern tavern = (Tavern)te;
				RenderSystem.pushMatrix();
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.color4f(1, 1, 1, 1);
				RenderSystem.translated(6, 6, 0);
				RenderSystem.pushMatrix();
				TavernRenderHelper.renderTavernUIBase(tavern.tavernType(), false);
				RenderSystem.translatef(2+10, 2, 0);
				TavernRenderHelper.renderAccess(tavern.owner().getAccessModifier());
				RenderSystem.popMatrix();

				PlayerTavernMemory memory = PlayerTavernMemory.get(player);
				if(memory.getHomePos()!=null&&memory.getHomePos().isSameTile(te)){
					Minecraft.getInstance().getTextureManager().bindTexture(HearthstoneScreen.ICONS);
					GuiUtils.drawTexturedModalRect(167*2, 9*2, 7*2, 0, 7*2, 7*2, 0);
				}
				FontRenderer font = Minecraft.getInstance().fontRenderer;
				TavernSign sign = TavernSign.of(tavern);
				font.drawStringWithShadow(sign.name(), 25*2, 8*2+1, 0xFFFFFF);
				font.drawStringWithShadow(sign.owner(), 25*2, 14*2-1, 0xFFFFFF);
				RenderSystem.popMatrix();
			}
		}
	}

	private static void drawCentered(String str, float x, float y){
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		font.drawStringWithShadow(str, x-font.getStringWidth(str)/2, y, 0xF0F0F0);
	}

	private static void drawRightAligned(String str, float x, float y){
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		font.drawStringWithShadow(str, x-font.getStringWidth(str), y, 0xF0F0F0);
	}
}
