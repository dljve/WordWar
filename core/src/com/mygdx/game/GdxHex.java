package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
	private ArrayList<Rectangle> tiles; 	// Tile positions
	private Map<Rectangle, Item> items; 	// Tile-Item pairs
	private Map<Rectangle, Color> colors; 	// Tile-Color pairs
	private Map<Rectangle, int[]> captures;	// Tile-Capture pairs

	// Textures
	private Texture tileW;
	private Texture bg;

	// Fonts
	private BitmapFont font;
	private BitmapFont gillsans;

	private float BOARD_OPACITY = 0.95f;

	// Tile colors
	private Color[][][] color = new Color[][][] {
		new Color[][] {
			new Color[] {
				new Color(1.00f, 1.00f, 1.00f, BOARD_OPACITY),	// Default color
				new Color(1.00f, 0.85f, 0.00f, BOARD_OPACITY)	// Player 2
			},
			new Color[] {
				new Color(0.85f, 0.00f, 0.00f, BOARD_OPACITY),	// Player 1
				new Color(1.00f, 0.69f, 0.04f, BOARD_OPACITY)	// Player 1 & 2
			}
		},
		new Color[][] {
			new Color[] {
				new Color(0.12f, 0.56f, 1.00f, BOARD_OPACITY), 	// Player 0
				new Color(0.12f, 0.62f, 0.12f, BOARD_OPACITY)	// Player 0 & 2
			},
			new Color[] {
				new Color(0.29f, 0.00f, 0.51f, BOARD_OPACITY),	// Player 0 & 1
				new Color(0.47f, 0.30f, 0.07f, BOARD_OPACITY)	// Player 0 & 1 & 3
			}
		}
	};


	// Constants
	private final int WORLD_WIDTH = 4000;
	private final int WORLD_HEIGHT = 3000;
	private final int BOARD_SIZE = 5;
	private final int TILE_SIZE = 256;
	private final int TILE_WIDTH = 222;
	private final int TILE_HEIGHT = 192; // 3/4th of tile
	private float currentZoom = 2f;
	private float zoomLimit = 2f;
	private float panRate = 1f;
	private boolean showNumbersOnTiles = false;

	@Override
	public void create() {
		batch = new SpriteBatch();

		tiles = new ArrayList<Rectangle>();
		items = new HashMap<Rectangle, Item>();
		colors = new HashMap<Rectangle, Color>();
		captures = new HashMap<Rectangle, int[]>();

		// Fonts
		font = new BitmapFont();
		gillsans = new BitmapFont(Gdx.files.internal("gillsans72.fnt"), false);
		gillsans.getData().setScale(0.5f);
		gillsans.setColor(Color.BLACK);

		// Textures
		tileW = new Texture("tileW.png");
		bg = new Texture("bg.jpg");

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Set camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, w, h);
		cam.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
		cam.zoom = currentZoom;
		cam.update();

		gestureDetector = new GestureDetector(this);
		Gdx.input.setInputProcessor(gestureDetector);

		generateTiles();
		getBoardState();
		assignWordsToTiles();
	}

	public void getBoardState() {
		// TODO: Receive captured status for each tile from server, hardcoded for now
		for (int i = 0; i < tiles.size(); i++) {
			int receivedId = i;
			int[] receivedCapture = new int[] {0,0,0};
			if (i == 25) receivedCapture = new int[] {1,0,0};
			if (i == 31) receivedCapture = new int[] {0,1,0};
			if (i == 58) receivedCapture = new int[] {0,0,1};

			Rectangle tile = tiles.get(receivedId);
			captures.put(tile, receivedCapture);
			updateColor(tile);
		}
	}

	/**
	 * Update the color for a tile
	 * @param tile
     */
	public void updateColor(Rectangle tile) {
		int[] c = captures.get(tile);
		colors.put(tile, color[c[0]][c[1]][c[2]] );
	}

	public void updateFrontier() {
		// For each of my tiles (iterate over capture, assume you are player 0)
		//   Loop through adjacency matrix
		//   if adjacent, color
	}

	public int getTileCount() {
		return 3 * BOARD_SIZE * (BOARD_SIZE-1) + 1;
	}

	public void generateTiles() {
		float w = WORLD_WIDTH;
		float h = WORLD_HEIGHT;
		int y_offset = (int)h/2 - TILE_SIZE/2; // Vertical center of screen

		for (int y = 0; y < BOARD_SIZE; y++) {
			int n_tiles = 2 * BOARD_SIZE - 1 - y; // Numbers of tiles in current row
			int row_width = TILE_WIDTH * n_tiles; // Width of the row
			int x_offset = (int)w/2 - row_width/2; // Horizontal center of screen
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
			batch.setColor(colors.get(tile));
			batch.draw(tileW, tile.x, tile.y);
		}
		batch.setColor(1, 1, 1, 1);

		for (Rectangle tile : tiles) {
			Item item = items.get(tile);
			Rectangle wordPos = item.getWordPosition();
			Rectangle transPos = item.getTranslationPosition();
			gillsans.draw(batch, item.getWord(), wordPos.x, wordPos.y);
			if (item.isNovel())
				gillsans.draw(batch, item.getTranslation(), transPos.x, transPos.y);
		}

		if (showNumbersOnTiles) {
			gillsans.setColor(new Color(0,0,0,1));
			for (int i = 0; i < tiles.size(); i++) {
				gillsans.draw(batch, String.valueOf(i), tiles.get(i).x + 96, tiles.get(i).y + 128);
			}
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
				//updateColor(tile);
				Gdx.app.log("info",tile.x + " " + tile.y);
			}
		}
		//Gdx.input.setOnscreenKeyboardVisible(true);
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

	public void assignWordsToTiles() {
		// TODO: Receive server messages (tileID, word, translation) for each tile

		// Example
		String[][] swen = new String[][] {
			new String[] {"adhama","honor"},new String[] {"jicho","eye"},new String[] {"pombe","beer"},new String[] {"adui","enemy"},new String[] {"jioni","evening"},new String[] {"rafiki","friend"},new String[] {"afisi","office"},new String[] {"jiwe","stone"},new String[] {"rangi","color"},new String[] {"ajabu","wonder"},new String[] {"kamba","rope"},new String[] {"roho","soul"},new String[] {"anga","sky"},new String[] {"kanisa","church"},new String[] {"saduku","box"},new String[] {"askari","police"},new String[] {"kaputula","pants"},new String[] {"samaki","fish"},new String[] {"baba","father"},new String[] {"karamu","party"},new String[] {"sauti","voice"},new String[] {"bahari","sea"},new String[] {"kazi","work"},new String[] {"shukuru","thanks"},new String[] {"barua","letter"},new String[] {"keja","house"},new String[] {"siri","secret"},new String[] {"basi","bus"},new String[] {"kichwa","head"},new String[] {"skati","skirt"},new String[] {"baskeli","bike"},new String[] {"kijana","boy"},new String[] {"tabibu","doctor"},new String[] {"bibi","grandmother"},new String[] {"kioo","mirror"},new String[] {"tofaa","apple"},new String[] {"bustani","garden"},new String[] {"kisu","knife"},new String[] {"tumaini","hope"},new String[] {"chakula","food"},new String[] {"kitanda","bed"},new String[] {"tumbili","monkey"},new String[] {"chama","society"},new String[] {"kiti","chair"},new String[] {"tunda","fruit"},new String[] {"chanjo","scissors"},new String[] {"kofia","hat"},new String[] {"ufu","death"},new String[] {"chapati","bread"},new String[] {"kuku","chicken"},new String[] {"ujuzi","knowledge"},new String[] {"chunga","pot"},new String[] {"lango","door"},new String[] {"wakati","alarm"},new String[] {"degaga","glasses"},new String[] {"likizo","holidays"},new String[] {"wimbo","song"},new String[] {"dhoruba","storm"},new String[] {"limau","lemon"},new String[] {"wingu","cloud"},new String[] {"duara","wheel"}
		};

		for (int i = 0; i < tiles.size(); i++) {
			Item item = new Item(swen[i][0], swen[i][1]);
			if (i==25) item = new Item ("","");
			if (i==31) item = new Item ("","");
			if (i==58) item = new Item ("","");

			// Calculate word positions
			GlyphLayout wordGlyph = new GlyphLayout(gillsans, swen[i][0]);
			GlyphLayout translationGlyph = new GlyphLayout(gillsans, swen[i][1]);

			Rectangle wordPosition = new Rectangle(
					tiles.get(i).x + TILE_SIZE/2 - wordGlyph.width/2,
					tiles.get(i).y + TILE_HEIGHT,
					wordGlyph.width, wordGlyph.height
			);

			Rectangle translationPosition = new Rectangle(
					tiles.get(i).x + TILE_SIZE/2 - translationGlyph.width/2,
					tiles.get(i).y + TILE_HEIGHT - 32,
					translationGlyph.width, translationGlyph.height
			);

			item.setWordPosition(wordPosition);
			item.setTranslationPosition(translationPosition);

			items.put(tiles.get(i),item);
		}
	}
}
