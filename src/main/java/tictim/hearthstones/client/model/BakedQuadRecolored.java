package tictim.hearthstones.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

import static net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE;

public class BakedQuadRecolored extends BakedQuadRetextured{
	private final int color4ub;

	public BakedQuadRecolored(BakedQuad quad, TextureAtlasSprite texture, int color4ub){
		super(quad, texture);
		this.color4ub = color4ub;
	}

	private void remapQuad2(){
		for(VertexFormatElement f : this.format.getElements()){
			if(f.getUsage()==EnumUsage.COLOR&&f.getSize()==4&&f.getType()==UBYTE){
				this.vertexData[this.format.getOffset(f.getIndex())/4] = this.color4ub;
			}
		}
	}
}
