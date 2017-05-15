package com.applab.wordwar;

import com.applab.wordwar.model.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HelpLabel extends Actor {

    private Game game;
    private ShapeRenderer renderer = new ShapeRenderer();
    private BitmapFont gillsans;

    public HelpLabel(Game game) {
        this.game = game;

        gillsans = new BitmapFont(Gdx.files.internal("gillsans72.fnt"), false);
        gillsans.getData().setScale(1f);
        gillsans.setColor(Color.WHITE);
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.translate(getX(), getY(), 0);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(game.helpColor);
        renderer.rect(0, 0, getWidth(), getHeight());
        renderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

        GlyphLayout textGlyph = new GlyphLayout(gillsans, game.helpText);
        gillsans.draw(batch, game.helpText,
                getX()+(getWidth()-textGlyph.width)/2,
                getY()+(getHeight()+textGlyph.height)/2);


    }
}
