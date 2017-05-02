package com.applab.wordwar;

import com.applab.wordwar.server.TempRivialClient;
import com.applab.wordwar.server.handlers.RivialHandler;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import java.io.IOException;

public class MainClass extends Game  {

	public static long deviceWidth;
	public static long deviceHeight;
	public static long splashScreenDisplayTime;
	public static float HEIGHT_DISTANCE_UNIT;


	public TempRivialClient getClient() {
		return client;
	}

	private TempRivialClient client;

	@Override
	public void create() {

		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();
		splashScreenDisplayTime = 2000l;
		HEIGHT_DISTANCE_UNIT = deviceHeight / 18;

		try {
			// 172.20.10.2
			client = new TempRivialClient("192.168.0.101", 8888);
			(new Thread(client)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setScreen(new LobbyScreen(this));
	}

	public long getSplashScreenDisplayTime() { return splashScreenDisplayTime;}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {

	}

	@Override
	public void resize(int width, int height) {
	}


}
