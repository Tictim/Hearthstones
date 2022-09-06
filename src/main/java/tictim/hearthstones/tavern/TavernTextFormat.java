package tictim.hearthstones.tavern;

import com.google.common.math.DoubleMath;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.math.RoundingMode;

import static net.minecraft.util.text.TextFormatting.*;

public class TavernTextFormat{
	private TavernTextFormat(){}

	public static final double FAR_AWAY = 5000;
	public static final double NEARBY = 5;

	public static String name(Tavern tavern){
		return formatName(tavern.name(), false);
	}

	public static String owner(Tavern tavern){
		return formatOwner(tavern.owner());
	}

	public static String distance(Tavern tavern, Entity entity){
		return formatDistance(tavern.pos(), entity);
	}

	public static String position(Tavern tavern){
		return formatPosition(tavern.pos());
	}

	public static String nameAndDistance(Tavern tavern, Entity entity){
		return name(tavern)+" - "+distance(tavern, entity);
	}

	public static String formatName(@Nullable String tavernName, boolean isMissing){
		String component = tavernName==null ?
				I18n.format("info.hearthstones.tavern.no_name") :
				BOLD+tavernName;
		return (isMissing ? RED+component : component)+RESET;
	}

	public static String formatOwner(Owner owner){
		return (owner.hasOwner() ?
				GREEN+I18n.format("info.hearthstones.tavern.owned_by", owner.getName()) :
				RED+I18n.format("info.hearthstones.tavern.no_owner"))+RESET;
	}

	public static String formatDistance(TavernPos pos, Entity entity){
		if(!pos.isSameDimension(entity.world))
			return I18n.format("info.hearthstones.tavern.another_dim");
		double dist = Math.sqrt(entity.getDistanceSq(pos.pos().getX()+0.5, pos.pos().getY()+0.5, pos.pos().getZ()+0.5));
		if(dist>=FAR_AWAY) return I18n.format("info.hearthstones.tavern.far_away");
		else if(dist<=NEARBY) return I18n.format("info.hearthstones.tavern.nearby");
		else return I18n.format("info.hearthstones.tavern.n_meter_away", DoubleMath.roundToInt(dist, RoundingMode.HALF_UP));
	}

	public static String formatPosition(TavernPos pos){
		return DARK_GRAY+pos.toString()+RESET;
	}
}
