package info.spicyclient.music;

import java.io.File;
import java.util.Random;

import javax.swing.SwingUtilities;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.files.FileManager;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.Notification;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.SceneBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.StageBuilder;
import javafx.stage.WindowEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class MusicManager {
	
	public static MusicManager musicManager;
	
	public static MusicManager getMusicManager() {
		
		if (musicManager == null) {
			musicManager = new MusicManager();
			
			SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                new JFXPanel(); // this will prepare JavaFX toolkit and environment
	                Platform.runLater(new Runnable() {
	                    @Override
	                    public void run() {
	                        StageBuilder.create()
	                                .scene(SceneBuilder.create()
	                                        .width(320)
	                                        .height(240)
	                                        .root(LabelBuilder.create()
	                                                .font(new javafx.scene.text.Font("Arial", 54d))
	                                                .text("Music Player Window")
	                                                .build())
	                                        .build())
	                                .onCloseRequest(new EventHandler<WindowEvent>() {
	                                    @Override
	                                    public void handle(WindowEvent windowEvent) {
	                                        System.exit(0);
	                                    }
	                                })
	                                .build();
	                    }
	                });
	            }
	        });
			
		}
		
		return musicManager;
		
	}
	
	public MediaPlayer mediaPlayer;
	public Notification musicNotification;
	public boolean playingMusic = false, shuffle = false;
	
	public void playMp3(String filepath) {
		
		if (musicNotification != null) {
			
			musicNotification.timeOnScreen = 0;
			stopPlaying();
			
		}
		
		musicNotification = new Notification("Playing - " + ((filepath.split("/")[filepath.split("/").length - 1]).contains(".mp3") ? (filepath.split("/")[filepath.split("/").length - 1]) : (filepath.split("/")[filepath.split("/").length - 1]) + ".mp3").replaceAll("%20", " ").replaceAll("%5B", "[").replaceAll("%5D", "]"), "", true, (long) mediaPlayer.getTotalDuration().toMillis() + 1500L, Type.INFO, Color.values()[new Random().nextInt(Color.values().length)], NotificationManager.getNotificationManager().defaultTargetX, NotificationManager.getNotificationManager().defaultTargetY, NotificationManager.getNotificationManager().defaultStartingX, NotificationManager.getNotificationManager().defaultStartingY, NotificationManager.getNotificationManager().defaultSpeed);
		musicNotification.setDefaultY = true;
		NotificationManager.getNotificationManager().createNotification(musicNotification);
		
		new Thread("Music Player - " + filepath) {
			
			public void run() {
				
				try {
					
					if (filepath.contains(".mp3")) {
						
						Media hit = new Media(filepath.replaceAll("\\\\", "/"));
						mediaPlayer = new MediaPlayer(hit);
						mediaPlayer.play();
						
					}else {
						
						Media hit = new Media((filepath + ".mp3").replaceAll("\\\\", "/"));
						mediaPlayer = new MediaPlayer(hit);
						mediaPlayer.play();
						
					}
					
					playingMusic = true;
					
				} catch (MediaException | IllegalStateException | IllegalArgumentException e) {
					
					musicNotification.timeOnScreen = 0;
					e.printStackTrace();
					Command.sendPrivateChatMessage("Failed to play song (Wrong file name?)");
					
				}
				
			};
			
		}.start();
		
	}
	
	public void stopPlaying() {
		
		if (mediaPlayer != null || musicNotification != null) {
			mediaPlayer.stop();
			musicNotification.timeOnScreen = 0;
		}
		
	}
	
	public void changeNotificationColor(EventUpdate e) {
		
		if (shuffle && musicNotification.left) {
			
			File[] files = FileManager.music.listFiles();
			
			if (files == null) {
				
				Command.sendPrivateChatMessage("You have 0 mp3 files");
				return;
				
			}
			
			MusicManager.getMusicManager().playMp3(files[new Random().nextInt(files.length)].toURI().toString().replaceAll(" ", "%20"));
			
		}
		
		if (playingMusic && (Minecraft.getMinecraft().thePlayer.ticksExisted % 20 == 0 || Minecraft.getMinecraft().thePlayer.ticksExisted % 20 == 10)) {
			
			musicNotification.color = Color.values()[new Random().nextInt(Color.values().length)];
			
		}
		
	}
	
}
