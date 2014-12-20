package com.musicroom;
import org.glassfish.jersey.server.ResourceConfig;

public class MusicRoomApplication extends ResourceConfig {
	public MusicRoomApplication() {
        packages("com.musicroom.resources");
        packages("com.musicroom.resources.decode");
  }
}
