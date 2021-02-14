package tictim.hearthstones.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import tictim.hearthstones.data.Owner;

public enum AccessModifier{
	PUBLIC,
	PROTECTED,
	TEAM,
	PRIVATE;

	public boolean hasAccessPermission(PlayerEntity player, Owner owner){
		switch(this){
			case TEAM:
				return owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PRIVATE:
				return owner.isOwnerOrOp(player);
			default:
				return true; // case PUBLIC: case PROTECTED:
		}
	}

	public boolean hasModifyPermission(PlayerEntity player, Owner owner){
		switch(this){
			case TEAM:
				return owner.isOwnerOrOp(player)||owner.isSameTeam(player);
			case PROTECTED:
			case PRIVATE:
				return owner.isOwnerOrOp(player);
			default:
				return true; // case PUBLIC:
		}
	}

	private TranslationTextComponent localized;

	public ITextComponent toTextComponent(){
		if(localized==null) localized = new TranslationTextComponent("info.hearthstones.access."+this.name().toLowerCase());
		return localized;
	}

	public static AccessModifier fromMeta(int meta){
		AccessModifier[] values = AccessModifier.values();
		return values[meta%values.length];
	}
}
