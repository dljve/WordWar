package com.applab.wordwar;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class wwGestureListener implements GestureDetector.GestureListener{

    private Game game;

    public wwGestureListener(Game game) {
        this.game = game;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Vector3 worldCoords = game.cam.unproject(new Vector3(x,y,0));

        for (Rectangle tile : game.tiles) {
            if ( tile.contains(worldCoords.x,worldCoords.y) ) {
                break;
            }
        }
        return false;
    }


    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }


    @Override
    public boolean longPress(float x, float y) {
        Vector3 worldCoords = game.cam.unproject(new Vector3(x,y,0));

        for (Rectangle tile : game.tiles) {
            if ( tile.contains(worldCoords.x,worldCoords.y) &&
                    game.frontier.contains(tile) && !game.isBaseTile(tile) ) {
                if (game.trialType != "test") game.trialType = "study"; // TODO: receive this from server and remove this line
                game.activeTile = tile;
                game.startTrial();
                break;
            }
        }

        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (game.isInTrial())
            return false;

        game.cam.translate(-deltaX * game.currentZoom * game.panRate, deltaY * game.currentZoom * game.panRate);

        float effectiveViewportWidth = game.cam.viewportWidth * game.cam.zoom;
        float effectiveViewportHeight = game.cam.viewportHeight * game.cam.zoom;

        game.cam.position.x = MathUtils.clamp(game.cam.position.x, effectiveViewportWidth / 2f, game.WORLD_WIDTH - effectiveViewportWidth / 2f);
        game.cam.position.y = MathUtils.clamp(game.cam.position.y, effectiveViewportHeight / 2f, game.WORLD_HEIGHT - effectiveViewportHeight / 2f);
        game.cam.update();
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        game.currentZoom = game.cam.zoom;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (game.isInTrial())
            return false;

        float newZoom = (initialDistance / distance) * game.currentZoom;
        if (newZoom <= game.zoomLimit) {
            game.cam.zoom = newZoom;
            game.cam.zoom = MathUtils.clamp(game.cam.zoom, 0.1f, game.WORLD_WIDTH/game.cam.viewportWidth);
            game.cam.update();
        }
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
