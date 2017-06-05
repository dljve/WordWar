package com.applab.wordwar;

import com.applab.wordwar.server.TempRivialClient;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.io.IOException;

public class MainClass extends Game  {

	public static long deviceWidth;
	public static long deviceHeight;
	public static long splashScreenDisplayTime;
	public static float HEIGHT_DISTANCE_UNIT;
	private BitmapFont gillsansFont;


	public TempRivialClient getClient() {
		return client;
	}

	private TempRivialClient client;

	@Override
	public void create() {

		gillsansFont = new BitmapFont(Gdx.files.internal("gillsans72.fnt"), false);
		gillsansFont.getData().setScale(0.6f);
		gillsansFont.setColor(Color.BLACK);

		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();
		splashScreenDisplayTime = 1000l;
		HEIGHT_DISTANCE_UNIT = deviceHeight / 18;

		try {
			client = new TempRivialClient("192.168.0.100", 8888);
			Gdx.app.log("MainClass/app", "Client created: " + client);
			(new Thread(client)).start();
		} catch (IOException e) {
			Gdx.app.log("MainClass/app", "Error! : " + e.getMessage());
			e.printStackTrace();
		}
		setScreen(new SplashScreen(this));
	}

	public BitmapFont getGillsansFont() {
		return gillsansFont;
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
