package com.applab.wordwar;

import com.applab.wordwar.model.GameModel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;


/**
 * Created by root on 29.04.2017.
 */
public class GameTableDataStructure {
    Table parentTable;
    private Table leftTable;
    private Table rightTable;
    private int id;
    private MainClass app;
    TextButton joinButton;
    private LobbyScreen lobbyScreen;




    public GameTableDataStructure(Table rootTable, GameModel game, BitmapFont font, MainClass app1, LobbyScreen lobbyScreen1) {
        this.lobbyScreen = lobbyScreen1;
        parentTable = new Table();
        this.id = game.getId();
        this.app = app1;

        // to get the design from presentation, I need three tables
        leftTable = new Table();
        rightTable = new Table();

        parentTable.add(leftTable);
        parentTable.add(rightTable);
        // rootTable.add(parentTable).width(5 * MainClass.deviceWidth / 7).height(2 * MainClass.deviceHeight / 12).align(Align.center);

        //setting the blue background for each table
        leftTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgMiniTabel.png"))));
        rightTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("bgMiniTabel.png"))));

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
       // TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle()


        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white");
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default-font");
        textButtonStyle.font.getData().setScale(2);

        //Hi . I'm here

        skin.add("default", textButtonStyle);
        joinButton = new TextButton("Join", skin, "default");

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.getClient().joinGame(id, System.currentTimeMillis());

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        while(app.getClient().getGameModel() == null);
                        // TODO Go to waiting room instead
                        app.setScreen(new Game(app));
                        lobbyScreen.dispose();
                    }
                });
            }
        });
        // TODO Get the language from the game ==> GameModel game is a variable
        Label languagesLabel = new Label("English - Swahili", labelStyle);
        //languagesLabel.setFontScale(1.3f);
        Label noPlayersLabel = new Label(game.getPlayers().size() + "/3 Players", labelStyle);


        //leftTable.debug();
        languagesLabel.setAlignment(Align.center);
        noPlayersLabel.setAlignment(Align.center);
        leftTable.pad(30);
        leftTable.add(languagesLabel).expand().fill().center();
        leftTable.row();
        leftTable.add(noPlayersLabel).expand().fill().center();

        //rightTable.debug();
        rightTable.add(joinButton).expandX().expandY().fill();//.width(rightTable.getWidth() - (rightTable.getWidth()/10)).height(rightTable.getHeight()- (rightTable.getHeight()/10));
        rightTable.pad(50);
    }



}
