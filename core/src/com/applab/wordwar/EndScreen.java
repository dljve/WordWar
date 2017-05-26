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

public class EndScreen extends Actor {

    private Game game;
    private ShapeRenderer renderer = new ShapeRenderer();
    private Color outline = Color.BLACK;
    private Color background = new Color(0,0,0,0.8f);
    private BitmapFont gillsans;

    public EndScreen (Game game) {
        this.game = game;

        gillsans = new BitmapFont(Gdx.files.internal("gillsans72.fnt"), false);
        gillsans.getData().setScale(0.5f);
        gillsans.setColor(Color.BLACK);
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
        renderer.setColor(background);
        renderer.rect(0, 0, getWidth(), getHeight());
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(outline);
        renderer.rectLine(0, 0, getWidth(), 0, 3);
        renderer.rectLine(0, 0, 0, getHeight(), 3);
        renderer.rectLine(getWidth(), 0, getWidth(), getHeight(), 3);
        renderer.rectLine(0, getHeight(), getWidth(), getHeight(), 3);
        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        ArrayList<Player> players = game.getApp().getClient().getGameModel().getPlayers();
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p2.getScore() - p1.getScore();
            }
        });
        int i = 0;
        for (Player player : players) {
            int id = player.getColor();
            float y_margin = getHeight()*0.3f;

            // Draw high-scores of players in gold, silver, bronze
            String name = player.getName();
            GlyphLayout nameGlyph = new GlyphLayout(gillsans, player.getName());
            Color playerColor = game.getColor()[(id==0)?1:0][(id==1)?1:0][(id==2)?1:0];

            if (id == game.PLAYER_ID) {
                String winText =
                        (new String[] {"Congratulations! You've won Word War!",
                                "Well done! You took second place.",
                                "Nice job! You took third place."})[i];
                gillsans.getData().setScale(0.55f);
                GlyphLayout winGlyph = new GlyphLayout(gillsans, winText);
                gillsans.setColor(Color.WHITE);
                gillsans.draw(batch, winText, getX()+getWidth()/2-winGlyph.width/2, getY()+0.90f*getHeight());
            }

            gillsans.getData().setScale(0.5f);
            gillsans.setColor(playerColor);
            name = "#" + (i+1) + ".  " + name + " (" + game.scores[id] +" tiles)";
            gillsans.draw(batch, name, getX()+getWidth()*0.1f,
                    getY()+getHeight()-i*(nameGlyph.height+getHeight()*0.1f)-y_margin);
        }

    }
}
