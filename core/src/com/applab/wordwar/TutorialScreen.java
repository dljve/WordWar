package com.applab.wordwar;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TutorialScreen implements Screen {
    private MainClass app;
    private Table rootTable;
    private Stage stage;
    private int currentTexture;
    private Texture[] tutorialTextures;
    private SpriteBatch spriteBatch;

    private Texture leftArrowTexture;
    private Texture rightArrowTexture;

    private float x_position_LeftImg;
    private float y_position_LeftImg;

    private float x_position_RightImg;
    private float y_position_RightImg;

    private float textureWidth;
    private float textureHeight;

    private Image leftArrowImg;
    private Image rightArrowImg;

    private Texture patchTexture;
    private Actor leftImgActor;

    public TutorialScreen(MainClass app) {
        this.app = app;
    }


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        rootTable = new Table();
        stage.addActor(rootTable);
        spriteBatch = new SpriteBatch();
        patchTexture = new Texture("imgPatch.png");

        initializeTutorialImages();

        initializeRootTable();

        initializeArrowsImages();

        Gdx.input.setInputProcessor(stage);

    }

    private void initializeArrowsImages() {
        leftArrowTexture = new Texture("leftArrow.png");
        leftArrowImg = new Image(leftArrowTexture);


        rightArrowTexture = new Texture("rightArrow.png");
        rightArrowImg = new Image(rightArrowTexture);

        textureWidth = 0.1f * app.deviceWidth;
        textureHeight = 2 * MainClass.HEIGHT_DISTANCE_UNIT;

        x_position_LeftImg = 0;
        y_position_LeftImg = app.deviceHeight / 2 - textureHeight / 2;

        x_position_RightImg = app.deviceWidth - textureWidth;
        y_position_RightImg = app.deviceHeight / 2 - textureHeight/2;

        leftArrowImg.setPosition(x_position_LeftImg,y_position_LeftImg);
        leftArrowImg.setWidth(textureWidth);
        leftArrowImg.setHeight(textureHeight);
        leftArrowImg.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    if (currentTexture == 0){
                        currentTexture = 2;
                    }else{
                         currentTexture--;
                    }
                Gdx.app.log("left arrow", (currentTexture + 1) + " -> " + currentTexture);
            }
        });

        rightArrowImg.setPosition(x_position_RightImg,y_position_RightImg);
        rightArrowImg.setWidth(textureWidth);
        rightArrowImg.setHeight(textureHeight);
        rightArrowImg.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentTexture == 2){
                    currentTexture = 0;
                }else{
                    currentTexture++;
                }

                Gdx.app.log("right arrow", (currentTexture - 1) + " -> " + currentTexture);
            }

        });

        stage.addActor(rightArrowImg);
        stage.addActor(leftArrowImg);

    }

    private void initializeTutorialImages() {;

        currentTexture = 0;

        tutorialTextures = new Texture[]{new Texture("help1.png"), new Texture("help2.png"), new Texture("help3.png")};

    }

    private void initializeRootTable() {
        rootTable.setFillParent(true);
        //rootTable.debug();

        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgScreens.png"))));


        Texture labelTutorialTexture = new Texture("wordWarTutorial.png");
        TextureRegion labelTutReg = new TextureRegion(labelTutorialTexture);
        Image labelImgTut = new Image(labelTutReg);

        rootTable.add(labelImgTut).width(app.deviceWidth).height(2 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom(16 * MainClass.HEIGHT_DISTANCE_UNIT);
        rootTable.row();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        drawTutorialImg(currentTexture);

    }

    private void drawTutorialImg(int currentTexture) {
        float imageWidth = app.deviceWidth * 0.8f;
        float imageHeight = 14 * MainClass.HEIGHT_DISTANCE_UNIT;
        float x = MainClass.HEIGHT_DISTANCE_UNIT;
        float y =   MainClass.HEIGHT_DISTANCE_UNIT;

        switch (currentTexture){
            case 0:
                    spriteBatch.begin();
                    spriteBatch.draw(tutorialTextures[0], x, y, imageWidth, imageHeight );

                    //cover the left arrow img so that the user can't click on it anymore
                    spriteBatch.draw(patchTexture, x_position_LeftImg, y_position_LeftImg, textureWidth, textureHeight);

                    spriteBatch.end();
                break;

            case 1:
                    spriteBatch.begin();
                    spriteBatch.draw(tutorialTextures[1], x, y, imageWidth, imageHeight );
                    spriteBatch.end();
                break;

            case 2:
                    spriteBatch.begin();
                    spriteBatch.draw(tutorialTextures[2], x, y, imageWidth, imageHeight );
                    spriteBatch.end();
                break;
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
        spriteBatch.dispose();
    }
}
