package tictim.hearthstones.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Locale;

public class HearthstoneCmd{
	public static GameProfile parseProfile(MinecraftServer server, String username) throws CommandException{
		if(ArrayUtils.contains(server.getPlayerProfileCache().getUsernames(), username.toLowerCase(Locale.ROOT))){
			GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
			if(profile!=null&&profile.getId()!=null) return profile;
		}
		throw new CommandException("command.tavern_memory.no_profile", username);
	}
}
