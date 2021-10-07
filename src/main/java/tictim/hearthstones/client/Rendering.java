package tictim.hearthstones.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import tictim.hearthstones.client.screen.TavernScreen;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.TavernType;

import java.util.Random;

public final class Rendering{ // TODO rename
	private Rendering(){}

	public static void renderTavernUIBase(PoseStack pose, TavernType type, boolean selected){
		RenderSystem.setShaderTexture(0, type.tavernUITexture);
		GuiComponent.blit(pose, 0, 0, 0, selected ? 20*2 : 0, 179*2, 20*2, 512, 512);
	}

	public static void renderTavernAccess(PoseStack pose, AccessModifier access){
		RenderSystem.setShaderTexture(0, switch(access){
			case PUBLIC -> TavernScreen.ACCESS_PUBLIC;
			case PROTECTED -> TavernScreen.ACCESS_PROTECTED;
			case TEAM -> TavernScreen.ACCESS_TEAM;
			case PRIVATE -> TavernScreen.ACCESS_PRIVATE;
		});
		GuiComponent.blit(pose, 0, 0, 0, 0, 32, 32, 32, 32);
	}


	private static final Random RNG = new Random();

	public static void renderHearthstoneParticles(LivingEntity entity, int amount){
		ParticleEngine particleManager = Minecraft.getInstance().particleEngine;

		for(int i = 0; i<amount; i++){
			double sy = rand(1.5, 0.75);
			Particle p = particleManager.createParticle(ParticleTypes.WITCH,
					rand(entity.getX(), 0.375),
					rand(entity.getY()+1, 0.75),
					rand(entity.getZ(), 0.375),
					0,
					sy,
					0);
			if(p!=null){
				// #02CCFC
				float r = (0x02)/255f;
				float g = (0xcc)/255f;
				float b = (0xfc)/255f;
				float chroma = 0.75F+RNG.nextFloat()*0.25F;
				p.setColor(r*chroma, g*chroma, b*chroma);
				p.setPower((float)sy);
			}
		}
	}

	private static double rand(double center, double spread){
		double rand = RNG.nextGaussian()*2-1;
		return center+spread*rand;
	}
}
