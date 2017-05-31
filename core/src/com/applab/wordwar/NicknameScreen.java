package com.applab.wordwar;

import com.applab.wordwar.LobbyScreen;
import com.applab.wordwar.MainClass;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import sun.applet.Main;

/**
 * Created by root on 08.05.2017.
 */

public class NicknameScreen implements Screen {
    private Table rootTable;
    private Stage stage;
    private MainClass app;
    private Skin skin;
    private TextField nicknameTextField;
    private Button imageButton;
    private Dialog myDialog;

    public NicknameScreen(MainClass app){
        this.app = app;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        rootTable = new Table();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        initializeRootTable();

        initializeSmallTable();
        
        stage.addActor(rootTable);
        Gdx.input.setInputProcessor(stage);
    }

    private void initializeRootTable() {
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgScreens.png"))));

        //adding the top label of this screen
        Texture labelTexture = new Texture("wordWarNickname.png");
        TextureRegion labelTextureRegion = new TextureRegion(labelTexture);
        Image labelImage = new Image(labelTextureRegion);
        rootTable.add(labelImage).width(MainClass.deviceWidth).height(3 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom( MainClass.HEIGHT_DISTANCE_UNIT);
        rootTable.row();

        //adding the info label

        Texture infoLabelTexture = new Texture("infoNicknameScreen.png");
        TextureRegion infoLabelRegion = new TextureRegion(infoLabelTexture);
        Image infoImage = new Image(infoLabelRegion);
        rootTable.add(infoImage).width(2 * MainClass.deviceWidth / 3).height(3 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom( MainClass.HEIGHT_DISTANCE_UNIT);
        rootTable.row();
    }


    private void initializeSmallTable() {
        Table smallTable = new Table();
        //smallTable.debug();


        smallTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgMiniTabel.png"))));

        Texture nextButtonTexture = new Texture("nextImg.jpg");
        TextureRegion nextButtonRegion = new TextureRegion(nextButtonTexture);
        Image nextImage = new Image(nextButtonRegion);

        initializeDialog();

        imageButton = new Button(nextImage,skin);
        imageButton.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y){
                String playerName = "";
                playerName = nicknameTextField.getText();
                if (playerName.length() == 0){
                    myDialog.show(stage);

                }else{
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    app.getClient().changeName(playerName);
                    app.setScreen(new LobbyScreen(app));
                    dispose();
                }

            }
        });

        TextField.TextFieldStyle textFieldStyle = skin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font.getData().setScale(2f);
        nicknameTextField = new TextField("", textFieldStyle);
       // nicknameTextField = new TextField("", skin);


        rootTable.add(smallTable).width(0.8f * MainClass.deviceWidth).height(3 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom(7 * MainClass.HEIGHT_DISTANCE_UNIT);
        rootTable.row();

        smallTable.add(nicknameTextField).expandX().fillX().pad(20);

        smallTable.add(imageButton).pad(20);
    }

    private void initializeDialog() {
        myDialog = new Dialog("No name picked :( !", skin);
        myDialog.setScale(1.3f);


        myDialog.text("Please type a name \n and then hit next :) ");

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                myDialog.cancel();
            }
        });

        myDialog.button(cancelButton);

        myDialog.getStyle().background.setMinWidth(2 / 3 * MainClass.deviceWidth);
        myDialog.getStyle().background.setMinHeight(2 * MainClass.HEIGHT_DISTANCE_UNIT);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act(delta);
        stage.draw();
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
