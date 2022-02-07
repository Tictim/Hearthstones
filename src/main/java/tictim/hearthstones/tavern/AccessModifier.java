package tictim.hearthstones.tavern;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum AccessModifier{
	PUBLIC,
	PROTECTED,
	TEAM,
	PRIVATE;

	public boolean hasAccessPermission(EntityPlayer player, Owner owner){
		switch(this){
			case TEAM:
				return owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PRIVATE:
				return owner.isOwnerOrOp(player);
			case PUBLIC:
			case PROTECTED:
				return true;
			default:
				throw new IllegalArgumentException();
		}
	}

	public boolean hasModifyPermission(EntityPlayer player, Owner owner){
		switch(this){
			case TEAM:
				return owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PROTECTED:
			case PRIVATE:
				return owner.isOwnerOrOp(player);
			case PUBLIC:
				return true;
			default:
				throw new IllegalArgumentException();
		}
	}

	private TextComponentTranslation text;

	public ITextComponent text(){
		if(text==null) text = new TextComponentTranslation("info.hearthstones.access."+this.name().toLowerCase());
		return text;
	}

	public static AccessModifier of(int index){
		AccessModifier[] values = AccessModifier.values();
		return values[index%values.length];
	}
}