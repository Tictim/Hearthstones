package tictim.hearthstones.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.gui.GuiUtils;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.logic.CompanionHearthstone;
import tictim.hearthstones.logic.HearthstoneItem;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.HearthingContext;
import tictim.hearthstones.utils.TavernTextFormat;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, value = Dist.CLIENT)
public final class OverlayRenderEventHandler{
	private OverlayRenderEventHandler(){}

	@SubscribeEvent
	public static void renderEvent(RenderGameOverlayEvent.Post event){
		if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL||Minecraft.getInstance().screen!=null) return;
		Player p = Minecraft.getInstance().player;
		if(p==null||!p.isAlive()) return;
		if(p.getMainHandItem().getItem() instanceof HearthstoneItem)
			drawHearthstoneUI(event, p, InteractionHand.MAIN_HAND);
		else if(p.getOffhandItem().getItem() instanceof HearthstoneItem)
			drawHearthstoneUI(event, p, InteractionHand.OFF_HAND);
		drawTavernSign(event.getMatrixStack(), p);
	}

	@SuppressWarnings("IntegerDivisionInFloatingPointContext")
	private static void drawHearthstoneUI(RenderGameOverlayEvent.Post event, Player player, InteractionHand hand){
		Minecraft mc = Minecraft.getInstance();
		int width = event.getWindow().getGuiScaledWidth(), height = event.getWindow().getGuiScaledHeight();
		int left = width/2, top = height/2;
		HearthingContext ctx = new HearthingContext(player, hand);
		TavernRecord record = ctx.getDestinationMemory();

		RenderSystem.disableBlend();
		PoseStack pose = event.getMatrixStack();
		if(record!=null){
			pose.pushPose();
			RenderSystem.setShaderColor(1, 1, 1, 1);

			mc.getItemRenderer().renderAndDecorateItem(player, record.getTavernType().stackForRender(), left-8, top-55, 0); // TODO WTF
			pose.popPose();

			drawCentered(pose, new TranslatableComponent("info.hearthstones.hearthstone.dest"), left, top-36);
			drawCentered(pose, TavernTextFormat.nameAndDistance(record, player), left, top-25);
			Component ownerText = TavernTextFormat.owner(record);
			Component positionText = TavernTextFormat.position(record);
			int ownerWidth = mc.font.width(ownerText);
			int positionWidth = mc.font.width(positionText);
			int widthSum = ownerWidth+mc.font.width(" ")+positionWidth;
			mc.font.drawShadow(pose, ownerText, left-widthSum/2, top-14, 0xF0F0F0);
			mc.font.draw(pose, positionText, left+widthSum/2-positionWidth, top-14, 0xF0F0F0);

			if(player.isUsingItem()&&player.getUsedItemHand()==hand){
				double ratio = Mth.clamp((double)player.getUseItemRemainingTicks()/player.getUseItem().getUseDuration(), 0, 1);
				drawCentered(pose, new TranslatableComponent("info.hearthstones.hearthstone.hearthing"), left, top+22);
				GuiUtils.drawGradientRect(pose.last().pose(), -90, left-32, top+33, left+32, top+43, 0xFF5f5f5f, 0xFF5f5f5f);
				GuiUtils.drawGradientRect(pose.last().pose(), -90, left-32, top+33, left+32-((int)(ratio*64)), top+43, 0xFF02ccfc, 0xFF02ccfc);
			}
		}else drawCentered(pose, ctx.getHearthstone().guideText(), left, top-33);
		if(ctx.getStack().getItem()==ModItems.COMPANION_HEARTHSTONE.get()){
			Set<Entity> entities = CompanionHearthstone.getWarpTargets(ctx);
			if(!entities.isEmpty()){
				TranslatableComponent text = new TranslatableComponent("info.hearthstones.companion_hearthstone.companions");

				for(Entity entity : entities){
					text.append("\n").append(entity.getDisplayName());
				}

				Font font = Minecraft.getInstance().font;
				String[] strs = text.getString().split("\n");
				for(int i = 0; i<strs.length; i++){
					font.drawShadow(
							pose,
							strs[i],
							width-8-font.width(strs[i]),
							top-(strs.length*mc.font.lineHeight)/2+i*mc.font.lineHeight,
							0xF0F0F0);
				}
			}
		}
		RenderSystem.enableBlend();
	}

	private static void drawTavernSign(PoseStack pose, Player player){
		HitResult ray = Minecraft.getInstance().hitResult;
		if(ray==null||ray.getType()!=HitResult.Type.BLOCK) return;
		BlockEntity blockEntity = player.level.getBlockEntity(((BlockHitResult)ray).getBlockPos());
		if(!(blockEntity instanceof Tavern tavern)) return;
		pose.pushPose();
		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		pose.translate(6, 6, 0);
		pose.pushPose();
		TavernRenderHelper.renderTavernUIBase(pose, tavern.tavernType(), false);
		pose.translate(2+10, 2, 0);
		TavernRenderHelper.renderAccess(pose, tavern.owner().getAccessModifier());
		pose.popPose();

		PlayerTavernMemory memory = PlayerTavernMemory.get(player);
		if(memory.getHomePos()!=null&&memory.getHomePos().isSameTile(blockEntity)){
			RenderSystem.setShaderTexture(0, HearthstoneScreen.ICONS);
			GuiComponent.blit(pose, 167*2, 9*2, 7*2, 0, 7*2, 7*2, 256, 256);
		}
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, TavernTextFormat.name(tavern), 25*2, 8*2+1, 0xFFFFFF);
		font.drawShadow(pose, TavernTextFormat.owner(tavern), 25*2, 14*2-1, 0xFFFFFF);
		pose.popPose();
		RenderSystem.enableDepthTest();
	}

	private static void drawCentered(PoseStack matrixStack, Component str, float x, float y){
		Font font = Minecraft.getInstance().font;
		//noinspection IntegerDivisionInFloatingPointContext
		font.drawShadow(matrixStack, str, x-font.width(str)/2, y, 0xF0F0F0);
	}
}
