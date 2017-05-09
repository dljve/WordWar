package com.applab.wordwar;

import com.applab.wordwar.server.NicknameScreen;
import com.applab.wordwar.server.TempRivialClient;
import com.applab.wordwar.server.handlers.RivialHandler;
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
		splashScreenDisplayTime = 10000l;
		HEIGHT_DISTANCE_UNIT = deviceHeight / 18;

		try {
			// 172.20.10.2
			client = new TempRivialClient("172.20.10.2", 8888);
			(new Thread(client)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setScreen(new NewGameScreen(this));
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
