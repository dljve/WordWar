package com.applab.wordwar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class MainClass extends Game  {

	public static long deviceWidth;
	public static long deviceHeight;
	public static long splashScreenDisplayTime;
	public static float HEIGHT_DISTANCE_UNIT;


	@Override
	public void create() {

		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();
		splashScreenDisplayTime = 2000l;
		HEIGHT_DISTANCE_UNIT = deviceHeight / 18;

		setScreen(new NewGameScreen(this));


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
