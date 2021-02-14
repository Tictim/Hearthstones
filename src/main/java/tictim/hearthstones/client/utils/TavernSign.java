package tictim.hearthstones.client.utils;

import com.google.common.math.DoubleMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.logic.Tavern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.RoundingMode;

import static net.minecraft.util.text.TextFormatting.*;
import static tictim.hearthstones.utils.HearthingContext.FAR_AWAY;
import static tictim.hearthstones.utils.HearthingContext.NEARBY;

public abstract class TavernSign{
	public static TavernSign of(TavernRecord record){
		return new TavernRecordSign(record);
	}

	public static TavernSign of(Tavern tavern){
		return new TavernTileSign(tavern);
	}

	private TavernSign(){}

	public String name(){
		StringBuilder stb = new StringBuilder();
		if(isMissing()) stb.append(RED);
		ITextComponent name = getTavernName();
		if(name==null) stb.append(I18n.format("info.hearthstones.tavern.noName"));
		else stb.append(BOLD).append(name.getString());
		return stb.toString();
	}
	public String owner(){
		return formatOwner(getOwner());
	}
	public String distance(){
		TavernPos pos = getTavernPos();
		PlayerEntity p = Minecraft.getInstance().player;
		if(!pos.isSameDimension(p.world)) return I18n.format("info.hearthstones.tavern.another_dim");
		double dist = Math.sqrt(p.getDistanceSq(pos.pos.getX()+0.5, pos.pos.getY()+0.5, pos.pos.getZ()+0.5));
		if(dist>=FAR_AWAY) return I18n.format("info.hearthstones.tavern.far_away");
		else if(dist<=NEARBY) return I18n.format("info.hearthstones.tavern.nearby");
		else return I18n.format("info.hearthstones.tavern.n_meter_away", DoubleMath.roundToInt(dist, RoundingMode.HALF_UP));
	}
	public String position(){
		return DARK_GRAY.toString()+getTavernPos();
	}
	public String nameAndDistance(){
		return name()+RESET+" - "+distance();
	}

	@Nullable
	protected abstract ITextComponent getTavernName();
	protected abstract Owner getOwner();
	protected abstract TavernPos getTavernPos();
	protected abstract boolean isMissing();

	public static String formatOwner(Owner o){
		return o.hasOwner() ? GREEN+I18n.format("info.hearthstones.tavern.ownedBy", o.getOwnerName()) : RED+I18n.format("info.hearthstones.tavern.noOwner");
	}

	private static final class TavernTileSign extends TavernSign{
		private final Tavern tavern;
		public TavernTileSign(Tavern tavern){this.tavern = tavern;}

		@Nullable
		@Override
		protected ITextComponent getTavernName(){
			return tavern.hasCustomName() ? tavern.getName() : null;
		}
		@Override
		protected Owner getOwner(){
			return tavern.owner();
		}
		@Override
		protected TavernPos getTavernPos(){
			return tavern.tavernPos();
		}
		@Override
		protected boolean isMissing(){
			return false;
		}
	}

	private static final class TavernRecordSign extends TavernSign{
		private final TavernRecord record;

		public TavernRecordSign(@Nonnull TavernRecord record){
			this.record = record;
		}

		@Nullable
		@Override
		protected ITextComponent getTavernName(){
			return record.getName();
		}
		@Override
		protected Owner getOwner(){
			return record.getOwner();
		}
		@Override
		protected TavernPos getTavernPos(){
			return record.getTavernPos();
		}
		@Override
		protected boolean isMissing(){
			return record.isMissing();
		}
	}
}
