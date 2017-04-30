package com.applab.wordwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class EndScreen extends Actor {

    private Game game;
    private ShapeRenderer renderer = new ShapeRenderer();
    private Color outline = Color.BLACK;
    private Color background = new Color(0,0,0,0.8f);
    private BitmapFont gillsans;

    public EndScreen (Game game) {
        this.game = game;

        gillsans = new BitmapFont(Gdx.files.internal("gillsans72.fnt"), false);
        gillsans.getData().setScale(1f);
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

        for (int i = 0; i<3; i++) {
            int id = game.getPlayerPlaces()[i];
            float y_margin = getHeight()*0.3f;

            // Draw highscores of players in gold, silver, bronze
            String name = "Player"+id;
            GlyphLayout nameGlyph = new GlyphLayout(gillsans, name);
            Color playerColor = game.getColor()[(id==0)?1:0][(id==1)?1:0][(id==2)?1:0];

            if (id == game.PLAYER_ID) {
                String winText =
                        (new String[] {"Congratulations! You've won Word War!",
                                "Well done! You took second place.",
                                "Nice job! You took third place."})[i];
                GlyphLayout winGlyph = new GlyphLayout(gillsans, winText);
                gillsans.setColor(Color.WHITE);
                gillsans.draw(batch, winText, getX()+getWidth()*0.05f, getY()+0.90f*getHeight());
            }

            gillsans.setColor(playerColor);
            name = (i+1) + ". " + name + " (" + game.scores[id] +")";
            gillsans.draw(batch, name, getX()+getWidth()*0.05f,
                    getY()+getHeight()-i*(nameGlyph.height+getHeight()*0.1f)-y_margin);

            // Draw medal sprite based on position

        }

    }
}
