package tictim.hearthstones.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.gui.TavernButton;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.item.TavernWaypointItem;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;
import tictim.hearthstones.hearthstone.CompanionHearthstone;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernRecord;
import tictim.hearthstones.tavern.TavernTextFormat;

import javax.annotation.Nullable;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, value = Side.CLIENT)
public class HearthstoneOverlay{
	/**
	 * Yeah I know this looks bad, stfu
	 */
	@Nullable public static TavernPos homePos;

	@SubscribeEvent
	public static void renderEvent(RenderGameOverlayEvent.Post event){
		if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL||Minecraft.getMinecraft().currentScreen!=null) return;
		EntityPlayer p = Minecraft.getMinecraft().player;
		if(p==null||!p.isEntityAlive()) return;

		drawTavernSign(p);

		int width = event.getResolution().getScaledWidth(), height = event.getResolution().getScaledHeight();

		// held item overlay
		if(p.isHandActive()&&p.getActiveHand()==EnumHand.OFF_HAND){
			if(drawOverlayForItem(p, width, height, EnumHand.OFF_HAND)) return;
			if(drawOverlayForItem(p, width, height, EnumHand.MAIN_HAND)) return;
		}else{
			if(drawOverlayForItem(p, width, height, EnumHand.MAIN_HAND)) return;
			if(drawOverlayForItem(p, width, height, EnumHand.OFF_HAND)) return;
		}
		// block overlay
		drawBinderLecternOverlay(p, width, height);
	}

	private static void drawTavernSign(EntityPlayer player){
		Tavern tavern = getTileOverMouse(player.world, Tavern.class);
		if(tavern==null) return;
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(6, 6, 0);
		GlStateManager.pushMatrix();
		Rendering.renderTavernUIBase(tavern.type(), false);
		GlStateManager.translate(2+10, 2, 0);
		Rendering.renderTavernAccess(tavern.access());
		GlStateManager.popMatrix();

		TavernPos homePos = HearthstoneOverlay.homePos;
		if(homePos!=null&&homePos.equals(tavern.pos())){
			Minecraft.getMinecraft().getTextureManager().bindTexture(TavernButton.ICONS);
			Gui.drawModalRectWithCustomSizedTexture(167*2, 9*2, 7*2, 0, 7*2, 7*2, 256, 256);
		}
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		font.drawStringWithShadow(TavernTextFormat.name(tavern), 25*2, 8*2+1, 0xFFFFFF);
		font.drawStringWithShadow(TavernTextFormat.owner(tavern), 25*2, 14*2-1, 0xFFFFFF);
		GlStateManager.popMatrix();
		GlStateManager.enableDepth();
	}

	private static boolean drawOverlayForItem(EntityPlayer player, int width, int height, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);
		if(stack.isEmpty()) return false;
		if(stack.getItem() instanceof HearthstoneItem){
			HearthstoneItem item = (HearthstoneItem)stack.getItem();
			drawHearthstoneOverlay(player, width, height, stack, item, hand);
			return true;
		}else if(stack.getItem()==ModItems.WAYPOINT_BINDER||stack.getItem()==ModItems.INFINITE_WAYPOINT_BINDER){
			BinderLecternTile binderLectern = getTileOverMouse(player.world, BinderLecternTile.class);
			if(binderLectern!=null&&!binderLectern.hasBinderSync()) return false;

			TavernBinderData data = TavernBinderItem.data(stack);
			if(data!=null) drawBinderOverlay(player, width, height, data.getWaypoints(), data.getEmptyWaypoints(), data.isInfiniteWaypoints());
			return true;
		}else if(stack.getItem()==ModItems.WAYPOINT){
			drawWaypointOverlay(player, width, height, stack);
			return true;
		}
		return false;
	}

	@Nullable private static TavernRecord displayTavernCache;
	@Nullable private static ItemStack displayTavernItem;

	private static ItemStack displayTavern(Tavern tavern){
		if(displayTavernCache==null||
				displayTavernCache.type()!=tavern.type()||
				displayTavernCache.skin()!=tavern.skin()){
			displayTavernCache = tavern.toRecord();
			displayTavernItem = TavernBlock.createTavernStack(tavern);
		}
		return displayTavernItem;
	}

	@SuppressWarnings("IntegerDivisionInFloatingPointContext")
	private static void drawHearthstoneOverlay(EntityPlayer player, int width, int height, ItemStack stack, HearthstoneItem hearthstoneItem, EnumHand hand){
		Minecraft mc = Minecraft.getMinecraft();
		int left = width/2, top = height/2;

		HearthstoneItem.Data data = HearthstoneItem.data(stack);
		if(data!=null) data.read();

		if(data==null||data.destination==null){
			drawHelpString(left, top-33,
					stack.getItem()==ModItems.SHABBY_HEARTHSTONE ?
							"info.hearthstones.hearthing_planks.help" :
							"info.hearthstones.hearthstone.help");
		}else{
			drawTavernOverlay(player, left, top-55, data.destination,
					"info.hearthstones.hearthstone.dest");
			if(hearthstoneItem.getHearthstone() instanceof CompanionHearthstone){
				CompanionHearthstone ch = (CompanionHearthstone)hearthstoneItem.getHearthstone();
				Set<Entity> entities = ch.getWarpTargets(player);
				if(!entities.isEmpty()){
					StringBuilder text = new StringBuilder(I18n.format("info.hearthstones.companion_hearthstone.companions"));
					for(Entity entity : entities) text.append("\n").append(entity.getDisplayName().getFormattedText());

					String[] strs = text.toString().split("\n");
					for(int i = 0; i<strs.length; i++)
						mc.fontRenderer.drawStringWithShadow(strs[i],
								width-8-mc.fontRenderer.getStringWidth(strs[i]),
								top-(strs.length*mc.fontRenderer.FONT_HEIGHT)/2+i*mc.fontRenderer.FONT_HEIGHT,
								0xF0F0F0);
				}
			}
		}

		if(player.isHandActive()&&player.getActiveHand()==hand){
			double ratio = MathHelper.clamp((double)player.getItemInUseCount()/player.getActiveItemStack().getMaxItemUseDuration(), 0, 1);
			drawCentered(I18n.format("info.hearthstones.hearthstone.hearthing"), left, top+22);
			GuiUtils.drawGradientRect(-90, left-32, top+33, left+32, top+43, 0xFF5f5f5f, 0xFF5f5f5f);
			GuiUtils.drawGradientRect(-90, left-32, top+33, left+32-(int)(ratio*64), top+43, 0xFF02ccfc, 0xFF02ccfc);
		}
	}

	@Nullable private static ItemStack waypointForRender;

	private static void drawBinderOverlay(EntityPlayer player, int width, int height, int waypoints, int emptyWaypoints, boolean infiniteWaypoints){
		Minecraft mc = Minecraft.getMinecraft();
		int left = width/2, top = height/2;

		int midpoint = top-33-18;

		String waypointText = infiniteWaypoints ?
				I18n.format("info.hearthstones.binder.overlay.waypoints.infinite",
						formatWaypointCount(waypoints)) :
				I18n.format("info.hearthstones.binder.overlay.waypoints",
						formatWaypointCount(waypoints), formatWaypointCount(emptyWaypoints));

		int length = 16+4+mc.fontRenderer.getStringWidth(waypointText);

		if(waypointForRender==null) waypointForRender = new ItemStack(ModItems.WAYPOINT);
		renderItem(player, waypointForRender, left-length/2, midpoint-8);

		//noinspection IntegerDivisionInFloatingPointContext
		mc.fontRenderer.drawStringWithShadow(waypointText, left-length/2+20, midpoint-mc.fontRenderer.FONT_HEIGHT/2, 0xFFFFFFFF);

		drawHelpString(left, top-33,
				"info.hearthstones.binder.overlay.help.0",
				"info.hearthstones.binder.overlay.help.1");
	}

	private static String formatWaypointCount(int waypoints){
		if(waypoints<=0) return ChatFormatting.RED.toString()+waypoints+ChatFormatting.RESET;
		else if(waypoints>=1000) return "1,000+";
		else return waypoints+"";
	}

	private static void drawWaypointOverlay(EntityPlayer player, int width, int height, ItemStack stack){
		TavernRecord tavern = TavernWaypointItem.getTavern(stack);
		if(tavern!=null)
			drawTavernOverlay(player, width/2, height/2-55, tavern,
					"info.hearthstones.waypoint.overlay.saved");

		drawHelpString(width/2, height/2+15, getTileOverMouse(player.world, BinderLecternTile.class)!=null ?
				"info.hearthstones.waypoint.overlay.help.lectern" :
				tavern!=null ?
						"info.hearthstones.waypoint.overlay.help.read" :
						"info.hearthstones.waypoint.overlay.help.bind");
	}

	/**
	 * height=51 if {@code contextStringKey} is present, height=40 if didn't
	 */
	public static void drawTavernOverlay(@Nullable EntityPlayer player, int xCenter, int yStart, Tavern tavern, @Nullable String contextStringKey){
		Minecraft mc = Minecraft.getMinecraft();
		renderItem(player, displayTavern(tavern), xCenter-8, yStart);

		if(contextStringKey!=null){
			drawCentered(I18n.format(contextStringKey), xCenter, yStart+19);
			yStart += 11;
		}
		drawCentered(player==null ? TavernTextFormat.name(tavern) : TavernTextFormat.nameAndDistance(tavern, player), xCenter, yStart+19);
		String ownerText = TavernTextFormat.owner(tavern);
		String positionText = TavernTextFormat.position(tavern);
		int positionWidth = mc.fontRenderer.getStringWidth(positionText);
		int widthSum = mc.fontRenderer.getStringWidth(ownerText)+mc.fontRenderer.getStringWidth(" ")+positionWidth;
		//noinspection IntegerDivisionInFloatingPointContext
		mc.fontRenderer.drawStringWithShadow(ownerText, xCenter-widthSum/2, yStart+30, 0xF0F0F0);
		mc.fontRenderer.drawString(positionText, xCenter+widthSum/2-positionWidth, yStart+30, 0xF0F0F0);
	}

	private static void drawBinderLecternOverlay(EntityPlayer player, int width, int height){
		BinderLecternTile binderLectern = getTileOverMouse(player.world, BinderLecternTile.class);
		if(binderLectern==null) return;

		if(binderLectern.hasBinderSync()){
			drawBinderOverlay(player, width, height,
					binderLectern.getWaypointsSync(),
					binderLectern.getEmptyWaypointsSync(),
					binderLectern.isInfiniteWaypointsSync());
		}else{
			drawHelpString(width/2, height/2+15,
					"info.hearthstones.binder.overlay.help.empty");
		}
	}

	private static void drawHelpString(int xCenter, int yStart, String... keys){
		for(String key : keys){
			drawCentered(I18n.format(key), xCenter, yStart);
			yStart += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
		}
	}

	private static void drawCentered(String str, float x, float y){
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		//noinspection IntegerDivisionInFloatingPointContext
		font.drawStringWithShadow(str, x-font.getStringWidth(str)/2, y, 0xF0F0F0);
	}

	@Nullable private static <TE> TE getTileOverMouse(World world, Class<TE> type){
		RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
		if(ray==null||ray.typeOfHit!=RayTraceResult.Type.BLOCK) return null;
		TileEntity te = world.getTileEntity(ray.getBlockPos());
		return type.isInstance(te) ? type.cast(te) : null;
	}

	private static void renderItem(EntityPlayer player, ItemStack stack, int x, int y){
		RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(player, stack, x, y);
		RenderHelper.disableStandardItemLighting();
	}
}
