package info.skyclient;

import info.skyclient.chatCommands.Command;
import info.skyclient.modules.Module;
import info.skyclient.modules.render.Hud;
import info.skyclient.music.MusicManager;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;

public class DiscordRP {
	
	public boolean running = false;
	public String lastLine = "";
	private long created = 0;
	
	public void start() throws Exception {
		
		try {
			
			this.running = true;
			this.created = System.currentTimeMillis();
			
			DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
				
				public void apply(DiscordUser user) {
					
					
					
				}
				
			}).build();
			
			DiscordRPC.discordInitialize("847861611909611551", handlers, true);
			
			new Thread("Discord RPC Callback") {
				
				@Override
				public void run() {
					
					while(running) {
						DiscordRPC.discordRunCallbacks();
					}
					
				}
				
			}.start();
			
		} catch (Exception e) {
			
		}
		
	}
	
	public void shutdown() {
		
		if (SkyClient.discordFailedToStart)
			return;
		
		running = false;
		DiscordRPC.discordShutdown();
		
	}
	
	public void refresh() {
		
		if (SkyClient.discordFailedToStart)
			return;
		
		if (Minecraft.getMinecraft().currentScreen == null && !Minecraft.getMinecraft().isSingleplayer()) {
			lastLine = "Jogando No " + Minecraft.getMinecraft().getCurrentServerData().serverIP;
		}
		else if (Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().isSingleplayer()) {
			lastLine = "Jogando No Single Player";
		}
	
	public void update(String secondline) {
		
		if (SkyClient.discordFailedToStart)
			return;
		
		lastLine = secondline;
		
		int toggled = 0;
		
		for (Module m : SkyClient.modules) {
			
			if (m.isEnabled() && !(m instanceof Hud) && !(m instanceof info.spicyclient.modules.player.DiscordRichPresence) && !(m instanceof info.spicyclient.modules.player.IrcChat)) {
				toggled++;
			}
			
		}
		
		DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondline);
		b.setBigImage("skyclientimage67", "Hacking in minecraft with " + SkyClient.config.clientName + SkyClient.config.clientVersion);
		
		if ((SkyClient.config.skin.isEnabled() && SkyClient.config.skin.mode.is("puro")) || SkyClient.config.furries.isEnabled() || SkyClient.config.floofyFoxes.isEnabled() || (SkyClient.config.hideName.isEnabled() && SkyClient.config.hideName.mode.getMode().toLowerCase().contains("Mine")) {
			//b.setSmallImage("skyclientimage67", "In Game");
			b.setSmallImage("gabe_thumbs_up", "In Game");
		}
		
		b.setDetails(toggled + "/" + (SkyClient.modules.size() - 3) + " Modules enabled");
		b.setStartTimestamps(created);
		
		DiscordRPC.discordUpdatePresence(b.build());
		
		
	}
	
}
