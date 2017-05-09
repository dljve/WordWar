package com.applab.wordwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import java.util.ArrayList;

import sun.applet.Main;


/**
 * Created by root on 01.05.2017.
 */

public class NewGameScreen implements Screen {
    private MainClass app;
    private Table rootTable;
    private Stage stage;
    private Skin skin;

    private TextButton createGameButton;

    private Label.LabelStyle labelStyle;
    private BitmapFont font;

    private SelectBox<String> numberOfPlayersBox;
    private Array<String> possibleNumberOfPlayersList;


    private Array<String> wordListArray;
    private SelectBox<String> wordlistSelectBox;

    public NewGameScreen(MainClass app) {
        this.app = app;
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        font = new BitmapFont(Gdx.files.internal("gillsans72.fnt"), false);
        font.getData().setScale(0.6f);
        font.setColor(Color.BLACK);

        wordListArray = new Array<String>();
        possibleNumberOfPlayersList = new Array<String>();

        possibleNumberOfPlayersList.add("");
        possibleNumberOfPlayersList.add("2");
        possibleNumberOfPlayersList.add("3");
        possibleNumberOfPlayersList.add("4");

        wordListArray.add("");
        wordListArray.add("Romanian - English");
        wordListArray.add("Dutch - German");
        wordListArray.add("German - Italian");


        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        initializeRootTable();

        stage = new Stage();
        stage.addActor(rootTable);

        Gdx.input.setInputProcessor(stage);
    }

    public void initializeRootTable() {
        rootTable = new Table();

        rootTable.setFillParent(true);

        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgScreens.png"))));

        // rootTable.debug();

        //adding the top label of this screen
        Texture lobbyLabelTexture = new Texture("newGameLabelLobby.png");
        TextureRegion lobbyLabelRegion = new TextureRegion(lobbyLabelTexture);
        Image lobbyLabelImage = new Image(lobbyLabelRegion);


        lobbyLabelImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(new LobbyScreen(app));
                dispose();
            }
        });

        rootTable.add(lobbyLabelImage).width(app.deviceWidth).height(2 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom(MainClass.HEIGHT_DISTANCE_UNIT).align(Align.top);
        rootTable.row();

        //adding the info about how to create the game
        Texture infoTexture = new Texture("infoNewGame.png");
        TextureRegion infoTextureRegion = new TextureRegion(infoTexture);
        Image infoImage = new Image(infoTextureRegion);
        rootTable.add(infoImage).width(2 * app.deviceWidth / 3).height(3 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom(MainClass.HEIGHT_DISTANCE_UNIT);
        rootTable.row();

        createWordlistTable();
        //createNumberOfPlayersTable();
        createGameButton();

    }


    private void createWordlistTable() {
        Table smallTable = new Table();
        smallTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgMiniTabel.png"))));

        //table.debug();

        Label label = new Label("Wordlist", labelStyle);
        label.setAlignment(Align.center);

        smallTable.add(label).expandX().fill().padBottom(MainClass.HEIGHT_DISTANCE_UNIT / 5);
        smallTable.row();

        wordlistSelectBox = new SelectBox<String>(skin);
        wordlistSelectBox.getStyle().font.getData().setScale(1.5f);
        wordlistSelectBox.setItems(wordListArray);
        wordlistSelectBox.getStyle().listStyle.selection.setLeftWidth(150);


        wordlistSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("There was selected the following wordlist:", wordlistSelectBox.getSelected());
            }
        });


        smallTable.add(wordlistSelectBox).expandX().fill().padBottom(MainClass.HEIGHT_DISTANCE_UNIT / 5);
        smallTable.row();

        rootTable.add(smallTable).height(3 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom(5 * MainClass.HEIGHT_DISTANCE_UNIT );
        rootTable.row();
    }


    private void createNumberOfPlayersTable() {

        Table smallTable = new Table();
        smallTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgMiniTabel.png"))));

        //table.debug();

        Label label = new Label("How many players?", labelStyle);
        label.setAlignment(Align.center);

        smallTable.add(label).expandX().fill().padBottom(MainClass.HEIGHT_DISTANCE_UNIT / 5);
        smallTable.row();

        numberOfPlayersBox = new SelectBox<String>(skin);
        numberOfPlayersBox.getStyle().font.getData().setScale(1.5f);
        numberOfPlayersBox.setItems(possibleNumberOfPlayersList);
        numberOfPlayersBox.getStyle().listStyle.selection.setLeftWidth(290);


        numberOfPlayersBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("Number of players:", numberOfPlayersBox.getSelected());
            }
        });


        smallTable.add(numberOfPlayersBox).expandX().fill().padBottom(MainClass.HEIGHT_DISTANCE_UNIT / 5);
        smallTable.row();

        rootTable.add(smallTable).height(3 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom(MainClass.HEIGHT_DISTANCE_UNIT);
        rootTable.row();
    }

    private void createGameButton() {
        createGameButton = new TextButton("Create game", skin, "default");

        createGameButton.getLabel().setFontScale(2f);


        createGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("new game", "Loading...");
                //com.badlogic.gdx.scenes.scene2d.ui.Dialog
                /*
                Dialog myDialog = new Dialog("Waiting for players...", skin, "dialog") {
                    protected void result (Object object) {
                        System.out.println("Chosen: " + object);
                    }
                }.text("The new game was created, \n please wait for the players to join :)").button("Cancel", true).key(Input.Keys.ENTER, true)
                        .key(Input.Keys.ESCAPE, false).show(stage).getStyle().background.setMinHeight(2 * MainClass.HEIGHT_DISTANCE_UNIT);
       */

                final Dialog myDialog = new Dialog("Waiting for the other players...", skin);
                myDialog.setScale(1.3f);


                myDialog.text("The new game was created, \n please wait for the players to join :)");

                TextButton cancelButton = new TextButton("Cancel", skin);
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        myDialog.cancel();
                    }
                });

                myDialog.button(cancelButton);

                myDialog.getStyle().background.setMinWidth(2 / 3 * app.deviceWidth);
                myDialog.getStyle().background.setMinHeight(2 * MainClass.HEIGHT_DISTANCE_UNIT);
                myDialog.show(stage);
            }

        });


        rootTable.add(createGameButton).align(Align.bottom).width(2 * app.deviceWidth / 3).height(2 * MainClass.HEIGHT_DISTANCE_UNIT).padBottom(MainClass.HEIGHT_DISTANCE_UNIT);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act(Gdx.graphics.getDeltaTime());
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
