package com.applab.wordwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by root on 24.04.2017.
 */

public class SplashScreen implements Screen {

    private MainClass app;
    private Table rootTable;
    private Stage stage;


    private long startTime;
    private long currentTime;

    public SplashScreen(MainClass app) {
        this.app = app;

    }

    //this method is like create(), only once called
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgScreens.png"))));

        Texture logoTexture = new Texture("wordWarLogo.jpg");
        TextureRegion appLogoRegion = new TextureRegion(logoTexture);
        Image logoImage = new Image(appLogoRegion);


        float imageWidth = logoImage.getImageWidth();
        float imageHeight = logoImage.getImageHeight();
        float centerImageOnWidth = MainClass.deviceWidth / 2 - imageWidth / 2;
        float centerImageOnHeight = MainClass.deviceHeight / 2 -  imageHeight / 2;
        rootTable.add(logoImage).setActorBounds(centerImageOnWidth,centerImageOnHeight, imageWidth, imageHeight);

        stage.addActor(rootTable);
        startTime = TimeUtils.millis();

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentTime = TimeUtils.millis();

        if (currentTime - startTime < app.getSplashScreenDisplayTime()) {
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        } else{
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



    }
}
