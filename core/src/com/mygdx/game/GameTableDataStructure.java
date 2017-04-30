package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Created by root on 29.04.2017.
 */
public class GameTableDataStructure {
        Table parentTable;
       private Table leftTable;
       private Table rightTable ;
       private int id;


    public GameTableDataStructure(Table rootTable, int id) {
        super();
        parentTable = new Table();
        this.id = id;


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

        TextButton joinButton = new TextButton("Join", skin, "default");

        Label languagesLabel = new Label("English - Romanian", skin, "default");
        //languagesLabel.setFontScale(1.3f);
        Label noPlayersLabel = new Label("1/4 Players", skin, "default");


        //leftTable.debug();
        leftTable.pad(30);
        leftTable.add(languagesLabel).expand().fill().center();
        leftTable.row();
        leftTable.add(noPlayersLabel).expand().fill().center();

        rightTable.add(joinButton).expand().fill();
        rightTable.pad(50);
    }
}
