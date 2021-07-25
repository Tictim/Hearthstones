package tictim.hearthstones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.util.text.TranslationTextComponent;
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

import java.util.Set;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, value = Dist.CLIENT)
public final class OverlayRenderEventHandler{
	private OverlayRenderEventHandler(){}

	@SubscribeEvent
	public static void renderEvent(RenderGameOverlayEvent.Post event){
		if(event.getType()==RenderGameOverlayEvent.ElementType.CROSSHAIRS&&Minecraft.getInstance().screen==null){
			PlayerEntity p = Minecraft.getInstance().player;
			if(p!=null&&p.isAlive()){
				if(p.getMainHandItem().getItem() instanceof HearthstoneItem) drawHearthstoneUI(event, p, Hand.MAIN_HAND);
				else if(p.getOffhandItem().getItem() instanceof HearthstoneItem) drawHearthstoneUI(event, p, Hand.OFF_HAND);
				drawTavernSign(event.getMatrixStack(), p);
			}
		}
	}

	private static void drawHearthstoneUI(RenderGameOverlayEvent.Post event, PlayerEntity player, Hand hand){
		Minecraft mc = Minecraft.getInstance();
		MainWindow window = event.getWindow();
		int width = window.getGuiScaledWidth(), height = window.getGuiScaledHeight();
		int left = width/2, top = height/2;
		HearthingContext ctx = new HearthingContext(player, hand);
		TavernRecord t = ctx.getDestinationMemory();
		RenderSystem.disableBlend();
		MatrixStack matrixStack = event.getMatrixStack();
		if(t!=null){
			matrixStack.pushPose();
			RenderSystem.color4f(1, 1, 1, 1);
			RenderSystem.enableRescaleNormal();

			mc.getItemRenderer().renderAndDecorateItem(player, t.getTavernType().stackForRender(), left-8, top-55);
			matrixStack.popPose();
			RenderHelper.turnOff();

			drawCentered(matrixStack, I18n.get("info.hearthstones.hearthstone.dest"), left, top-36);
			TavernSign sign = TavernSign.of(t);
			drawCentered(matrixStack, sign.nameAndDistance(), left, top-25);
			String ownerText = sign.owner();
			String positionText = sign.position();
			int ownerWidth = mc.font.width(ownerText);
			int positionWidth = mc.font.width(positionText);
			int widthSum = ownerWidth+mc.font.width(" ")+positionWidth;
			mc.font.drawShadow(matrixStack, ownerText, left-widthSum/2, top-14, 0xF0F0F0);
			mc.font.draw(matrixStack, positionText, left+widthSum/2-positionWidth, top-14, 0xF0F0F0);

			if(player.isUsingItem()&&player.getUsedItemHand()==hand){
				double ratio = MathHelper.clamp((double)player.getUseItemRemainingTicks()/player.getUseItem().getUseDuration(), 0, 1);
				drawCentered(matrixStack, I18n.get("info.hearthstones.hearthstone.hearthing"), left, top+22);
				GuiUtils.drawGradientRect(matrixStack.last().pose(), -90, left-32, top+33, left+32, top+43, 0xFF5f5f5f, 0xFF5f5f5f);
				GuiUtils.drawGradientRect(matrixStack.last().pose(), -90, left-32, top+33, left+32-((int)(ratio*64)), top+43, 0xFF02ccfc, 0xFF02ccfc);
			}
		}else drawCentered(matrixStack, ctx.getHearthstone().guideText().getString(), left, top-33);
		if(ctx.getStack().getItem()==ModItems.COMPANION_HEARTHSTONE.get()){
			Set<Entity> entities = CompanionHearthstone.getWarpTargets(ctx);
			if(!entities.isEmpty()){
				TranslationTextComponent text = new TranslationTextComponent("info.hearthstones.companion_hearthstone.companions");

				for(Entity entity : entities){
					text.append("\n").append(entity.getDisplayName());
				}

				FontRenderer font = Minecraft.getInstance().font;
				String[] strs = text.getString().split("\n");
				for(int i = 0; i<strs.length; i++){
					font.drawShadow(
							matrixStack,
							strs[i],
							width-8-font.width(strs[i]),
							top-(strs.length*mc.font.lineHeight)/2+i*mc.font.lineHeight,
							0xF0F0F0);
				}
			}
		}
		RenderSystem.enableBlend();
	}

	private static void drawTavernSign(MatrixStack matrixStack, PlayerEntity player){
		RayTraceResult ray = Minecraft.getInstance().hitResult;
		if(ray!=null&&ray.getType()==RayTraceResult.Type.BLOCK){
			BlockRayTraceResult blockRay = (BlockRayTraceResult)ray;
			TileEntity te = player.level.getBlockEntity(blockRay.getBlockPos());
			if(te instanceof Tavern){
				Tavern tavern = (Tavern)te;
				matrixStack.pushPose();
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.color4f(1, 1, 1, 1);
				matrixStack.translate(6, 6, 0);
				matrixStack.pushPose();
				TavernRenderHelper.renderTavernUIBase(matrixStack, tavern.tavernType(), false);
				matrixStack.translate(2+10, 2, 0);
				TavernRenderHelper.renderAccess(matrixStack, tavern.owner().getAccessModifier());
				matrixStack.popPose();

				PlayerTavernMemory memory = PlayerTavernMemory.get(player);
				if(memory.getHomePos()!=null&&memory.getHomePos().isSameTile(te)){
					Minecraft.getInstance().getTextureManager().bind(HearthstoneScreen.ICONS);
					GuiUtils.drawTexturedModalRect(matrixStack, 167*2, 9*2, 7*2, 0, 7*2, 7*2, 0);
				}
				FontRenderer font = Minecraft.getInstance().font;
				TavernSign sign = TavernSign.of(tavern);
				font.drawShadow(matrixStack, sign.name(), 25*2, 8*2+1, 0xFFFFFF);
				font.drawShadow(matrixStack, sign.owner(), 25*2, 14*2-1, 0xFFFFFF);
				matrixStack.popPose();
			}
		}
	}

	private static void drawCentered(MatrixStack matrixStack, String str, float x, float y){
		FontRenderer font = Minecraft.getInstance().font;
		font.drawShadow(matrixStack, str, x-font.width(str)/2, y, 0xF0F0F0);
	}
}
