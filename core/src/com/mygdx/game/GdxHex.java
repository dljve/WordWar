package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GdxHex extends ApplicationAdapter implements GestureDetector.GestureListener {
	private SpriteBatch batch;
	private OrthographicCamera cam;
	private GestureDetector gestureDetector;
	private Texture tileW;
	private Texture tileR;
	private Texture tileY;
	private Texture tileB;
	private Texture bg;
	private Sprite sprite;

	private final int BOARD_SIZE = 5;
	private final int TILE_SIZE = 256;
	private final int TILE_WIDTH = 222;
	private final int TILE_HEIGHT = 192; // 3/4th of tile
	private ArrayList<Rectangle> tiles;
	private Map<Rectangle, Texture> textures;

	private float currentZoom = 2f;
	private float zoomLimit = 2f;
	private float panRate = 1f;

	private final int WORLD_WIDTH = 4000;
	private final int WORLD_HEIGHT = 3000;


	@Override
	public void create() {
		batch = new SpriteBatch();

		tiles = new ArrayList<Rectangle>();
		textures = new HashMap<Rectangle, Texture>();

		// Textures
		tileW = new Texture("tileW.png");
		tileR = new Texture("tileR.png");
		tileY = new Texture("tileY.png");
		tileB = new Texture("tileB.png");

		bg = new Texture("bg.jpg");
		sprite = new Sprite(bg);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Set camera
		cam = new OrthographicCamera(); //new OrthographicCamera(30,30*r);
		cam.setToOrtho(false, w, h);
		cam.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
		cam.zoom = currentZoom;
		cam.update();

		generateTiles();

		// Set colors (this only works when BOARD_SIZE = 5
		textures.put(tiles.get(25),tileB);
		textures.put(tiles.get(31),tileY);
		textures.put(tiles.get(58),tileR);

		gestureDetector = new GestureDetector(this);
		Gdx.input.setInputProcessor(gestureDetector);
	}

	public int getTileCount() {
		return 3 * BOARD_SIZE * (BOARD_SIZE-1) + 1;
	}

	public void generateTiles() {
		float w = WORLD_WIDTH; //Gdx.graphics.getWidth();
		float h = WORLD_HEIGHT; //Gdx.graphics.getHeight();
		int y_offset = (int)h/2 - TILE_SIZE/2;

		for (int y = 0; y < BOARD_SIZE; y++) {
			int n_tiles = 2 * BOARD_SIZE - 1 - y;
			int row_width = TILE_WIDTH * n_tiles;
			int x_offset = (int)w/2 - row_width/2; // Center of screen
			int y_offset_top = y_offset - y * TILE_HEIGHT;
			int y_offset_bottom = y_offset + y * TILE_HEIGHT;

			// Top row
			for (int x = 0; x < n_tiles; x++) {
				Rectangle tile = new Rectangle();
				tile.x = x_offset + x * TILE_WIDTH;
				tile.y = y_offset_top;
				tile.width = TILE_SIZE;
				tile.height = TILE_SIZE;
				tiles.add(tile);
				textures.put(tile,tileW);
			}

			// Bottom Row
			if (y == 0) continue;
			for (int x = 0; x < n_tiles; x++) {
				Rectangle tile = new Rectangle();
				tile.x = x_offset + x * TILE_WIDTH;
				tile.y = y_offset_bottom;
				tile.width = TILE_SIZE;
				tile.height = TILE_SIZE;
				tiles.add(tile);
				textures.put(tile, tileW);
			}
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(bg, 0, 0);
		for (Rectangle tile : tiles) {
			batch.draw(textures.get(tile), tile.x, tile.y);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		tileW.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {

		/*
		Vector3 worldCoords = cam.unproject(new Vector3(x,y,0));

		for (Rectangle tile : tiles) {
			if ( tile.contains(worldCoords.x,worldCoords.y) ) {
				textures.put(tile,tileB);
				Gdx.app.log("info",tile.x + " " + tile.y);
			}
		}
		*/
		return false;
	}


	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		Vector3 worldCoords = cam.unproject(new Vector3(x,y,0));

		for (Rectangle tile : tiles) {
			if ( tile.contains(worldCoords.x,worldCoords.y) ) {
				textures.put(tile,tileB);
				Gdx.app.log("info",tile.x + " " + tile.y);
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
		Gdx.app.log("INFO", "pan");
		Gdx.app.log("INFO", cam.position.x + " " + cam.position.y + " ");
		cam.translate(-deltaX * currentZoom * panRate, deltaY * currentZoom * panRate);

		float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
		float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

		cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, WORLD_WIDTH - effectiveViewportWidth / 2f);
		cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, WORLD_HEIGHT - effectiveViewportHeight / 2f);
		cam.update();
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		Gdx.app.log("INFO", "panStop");
		currentZoom = cam.zoom;
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		Gdx.app.log("INFO", String.valueOf(cam.zoom) );
		float newZoom = (initialDistance / distance) * currentZoom;
		if (newZoom <= zoomLimit) {
			cam.zoom = newZoom;
			cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, WORLD_WIDTH/cam.viewportWidth);
			cam.update();
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
