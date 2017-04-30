package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by root on 24.04.2017.
 */

public class SplashScreen implements Screen {
    private Texture texture;
    private SpriteBatch batch;
    private MainClass app;


    private long startTime;
    private long currentTime;

    public SplashScreen(MainClass app) {
        this.app = app;

    }

    //this method is like create(), only once called
    @Override
    public void show() {

        batch = new SpriteBatch();
        texture = new Texture("wordWarLogo.jpg");

        startTime = TimeUtils.millis();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentTime = TimeUtils.millis();
        if (currentTime - startTime < app.getSplashScreenDisplayTime()) {
            batch.begin();
            batch.draw(texture, 0, 0, GdxHex.deviceWidth, GdxHex.deviceHeight);
            batch.end();
        }else{
            app.setScreen(new LobbyScreen(app));
            dispose(); //dispose the current instance of the screen
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();

    }
}
