package tictim.hearthstones.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import tictim.hearthstones.client.gui.TavernScreen;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.TavernType;

import java.util.Random;

public class Rendering{
	private Rendering(){}

	public static void renderTavernUIBase(TavernType type, boolean selected){
		Minecraft.getMinecraft().getTextureManager().bindTexture(type.guiTexture);
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, selected ? 20*2 : 0, 179*2, 20*2, 512, 512);
	}

	public static void renderTavernAccess( AccessModifier access){
		ResourceLocation tex;
		switch(access){
			case PUBLIC:
				tex = TavernScreen.ACCESS_PUBLIC;
				break;
			case PROTECTED:
				tex = TavernScreen.ACCESS_PROTECTED;
				break;
			case TEAM:
				tex = TavernScreen.ACCESS_TEAM;
				break;
			case PRIVATE:
				tex = TavernScreen.ACCESS_PRIVATE;
				break;
			default:
				throw new IllegalArgumentException();
		}
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		Gui.drawModalRectWithCustomSizedTexture( 0, 0, 0, 0, 32, 32, 32, 32);
	}


	private static final Random RNG = new Random();

	public static void renderHearthstoneParticles(EntityLivingBase entity, int amount){
		ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;

		for(int i = 0; i<amount; i++){
			double sy = rand(1.5, 0.75);
			Particle p = effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(),
					rand(entity.posX, 0.375),
					rand(entity.posY+1, 0.75),
					rand(entity.posZ, 0.375),
					0,
					sy,
					0);
			if(p!=null){
				// #02CCFC
				float r = (0x02)/255f;
				float g = (0xcc)/255f;
				float b = (0xfc)/255f;
				float chroma = 0.75F+RNG.nextFloat()*0.25F;
				p.setRBGColorF(r*chroma, g*chroma, b*chroma);
				p.multiplyVelocity((float)sy);
			}
		}
	}

	private static double rand(double center, double spread){
		double rand = RNG.nextGaussian()*2-1;
		return center+spread*rand;
	}
}
