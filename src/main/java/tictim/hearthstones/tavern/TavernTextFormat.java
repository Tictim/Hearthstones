package tictim.hearthstones.tavern;

import com.google.common.math.DoubleMath;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.math.RoundingMode;

import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;

public final class TavernTextFormat{
	private TavernTextFormat(){}

	public static final double FAR_AWAY = 5000;
	public static final double NEARBY = 5;

	public static Component name(Tavern tavern){
		return formatName(tavern.name(), false);
	}

	public static Component owner(Tavern tavern){
		return formatOwner(tavern.owner());
	}

	public static Component distance(Tavern tavern, Entity entity){
		return formatDistance(tavern.pos(), entity);
	}

	public static Component position(Tavern tavern){
		return formatPosition(tavern.pos());
	}

	public static Component nameAndDistance(Tavern tavern, Entity entity){
		return Component.literal("")
				.append(name(tavern))
				.append(" - ")
				.append(distance(tavern, entity));
	}

	public static Component formatName(@Nullable String tavernName, boolean isMissing){
		MutableComponent component = tavernName==null ?
				Component.translatable("info.hearthstones.tavern.no_name") :
				Component.literal(tavernName).withStyle(ChatFormatting.BOLD);
		return isMissing ? component.withStyle(ChatFormatting.RED) : component;
	}

	public static Component formatOwner(Owner owner){
		return owner.hasOwner() ?
				Component.translatable("info.hearthstones.tavern.owned_by", owner.getName()).withStyle(GREEN) :
				Component.translatable("info.hearthstones.tavern.no_owner").withStyle(RED);
	}

	public static Component formatDistance(TavernPos pos, Entity entity){
		if(!pos.isSameDimension(entity.level))
			return Component.translatable("info.hearthstones.tavern.another_dim");
		double dist = Math.sqrt(entity.distanceToSqr(pos.pos().getX()+0.5, pos.pos().getY()+0.5, pos.pos().getZ()+0.5));
		if(dist>=FAR_AWAY) return Component.translatable("info.hearthstones.tavern.far_away");
		else if(dist<=NEARBY) return Component.translatable("info.hearthstones.tavern.nearby");
		else return Component.translatable("info.hearthstones.tavern.n_meter_away", DoubleMath.roundToInt(dist, RoundingMode.HALF_UP));
	}

	public static Component formatPosition(TavernPos pos){
		return Component.literal(pos.toString()).withStyle(ChatFormatting.DARK_GRAY);
	}
}
