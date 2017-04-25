package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
	private ArrayList<Rectangle> frontier; 	// Tile frontier
	private Map<Rectangle, Item> items; 	// Tile-Item pairs
	private Map<Rectangle, Color> colors; 	// Tile-Color pairs
	private Map<Rectangle, int[]> captures;	// Tile-Capture pairs
	private Rectangle activeTile = null;

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
				new Color(1.00f, 1.00f, 1.00f, 0.85f),	// Default color
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
	private int[] baseTiles = {32, 38 ,53};
	private int PLAYER_ID = 0;

	private boolean inTrial = false;
	private String answer = "";

	public boolean isInTrial() {
		return inTrial;
	}

	private Vector3 prevPos;
	private float prevZoom;

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}

	@Override
	public void create() {
		batch = new SpriteBatch();

		tiles = new ArrayList<Rectangle>();
		frontier = new ArrayList<Rectangle>();
		items = new HashMap<Rectangle, Item>();
		colors = new HashMap<Rectangle, Color>();
		captures = new HashMap<Rectangle, int[]>();

		// Load Fonts
		font = new BitmapFont();
		gillsans = new BitmapFont(Gdx.files.internal("gillsans72.fnt"), false);
		gillsans.getData().setScale(0.5f);
		gillsans.setColor(Color.BLACK);

		// Load Textures
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

		// Input Processors
		gestureDetector = new GestureDetector(this);
		wwInputProcessor inputProcessor = new wwInputProcessor(this);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(inputProcessor);
		multiplexer.addProcessor(gestureDetector);
		Gdx.input.setInputProcessor(multiplexer);

		Gdx.input.setCatchBackKey(true);

		// Prepare game
		generateTiles();
		getBoardState();
		assignWordsToTiles();
		updateFrontier();
	}

	public void getBoardState() {
		// TODO: Receive captured status for each tile from server, hardcoded for now
		for (int i = 0; i < tiles.size(); i++) {
			int receivedId = i;
			int[] receivedCapture = new int[] {0,0,0};
			if (i == baseTiles[0]) receivedCapture = new int[] {1,0,0};
			if (i == baseTiles[1]) receivedCapture = new int[] {0,1,0};
			if (i == baseTiles[2]) receivedCapture = new int[] {0,0,1};

			Rectangle tile = tiles.get(receivedId);
			captures.put(tile, receivedCapture);
			updateColor(tile);
		}
	}

	public void giveTranslation() {
		// TODO: give translation to server and receive feedback
		// TODO: For study the model does not need to be updated
		// TODO: For test, receive "correct"/"incorrect"

		// Show feedback (player 0 captures tile as an example)
		captures.get(activeTile)[0] = 1;
		updateColor(activeTile);
		items.get(activeTile).setTranslation("");
		//correctFeedback();
		//incorrectFeedback();

		endTrial();
		updateFrontier();
	}

	/**
	 * Update the color for a tile
	 * @param tile
     */
	public void updateColor(Rectangle tile) {
		int[] c = captures.get(tile);
		colors.put(tile, color[c[0]][c[1]][c[2]] );
	}

	/**
	 * Update the frontier for the player
	 * The frontier contains all the tiles that the player can capture
	 *
	 */
	public void updateFrontier() {
		ArrayList<Rectangle> area = new ArrayList<Rectangle>();
		ArrayList<Rectangle> neighbors = new ArrayList<Rectangle>();

		// List the tiles in the player's area
		for (Rectangle tile : tiles) {
			if (captures.get(tile)[PLAYER_ID]==1) {
				area.add(tile);
			}
		}

		// Find the neighboring tiles
		for (Rectangle tile : area) {
			for (Rectangle neighbor : tiles) {
				if (	tile.x + TILE_WIDTH == neighbor.x && tile.y == neighbor.y ||
						tile.x - TILE_WIDTH == neighbor.x && tile.y == neighbor.y ||
						(tile.x + TILE_WIDTH/2 == neighbor.x || tile.x - TILE_WIDTH/2 == neighbor.x) &&
						(tile.y + TILE_HEIGHT == neighbor.y || tile.y - TILE_HEIGHT == neighbor.y)
						) {
					neighbors.add(neighbor);
				}
			}
		}

		// Add the uncaptured neighbors to the frontier
		frontier.clear();
		for (Rectangle neighbor : neighbors) {
			if (captures.get(neighbor)[PLAYER_ID]==0) {
				// Make sure it neighbors at least two tiles or the base tile
				int n = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					if (neighbors.get(i) == neighbor) n++;
				}
				if (n>1 || area.size() == 1) {
					frontier.add(neighbor);
					Color c = colors.get(neighbor);
					colors.put(neighbor, new Color(c.r, c.g, c.b, BOARD_OPACITY));
				}
			}
		}
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
			int y_offset_top = y_offset + y * TILE_HEIGHT;
			int y_offset_bottom = y_offset - y * TILE_HEIGHT;

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

	public void assignWordsToTiles() {
		// TODO: Receive server messages (tileID, word, translation) for each tile

		// Example
		String[][] swen = new String[][] {
				new String[] {"adhama","honor"},new String[] {"jicho","eye"},new String[] {"pombe","beer"},new String[] {"adui","enemy"},new String[] {"jioni","evening"},new String[] {"rafiki","friend"},new String[] {"afisi","office"},new String[] {"jiwe","stone"},new String[] {"rangi","color"},new String[] {"ajabu","wonder"},new String[] {"kamba","rope"},new String[] {"roho","soul"},new String[] {"anga","sky"},new String[] {"kanisa","church"},new String[] {"saduku","box"},new String[] {"askari","police"},new String[] {"kaputula","pants"},new String[] {"samaki","fish"},new String[] {"baba","father"},new String[] {"karamu","party"},new String[] {"sauti","voice"},new String[] {"bahari","sea"},new String[] {"kazi","work"},new String[] {"shukuru","thanks"},new String[] {"barua","letter"},new String[] {"keja","house"},new String[] {"siri","secret"},new String[] {"basi","bus"},new String[] {"kichwa","head"},new String[] {"skati","skirt"},new String[] {"baskeli","bike"},new String[] {"kijana","boy"},new String[] {"tabibu","doctor"},new String[] {"bibi","grandmother"},new String[] {"kioo","mirror"},new String[] {"tofaa","apple"},new String[] {"bustani","garden"},new String[] {"kisu","knife"},new String[] {"tumaini","hope"},new String[] {"chakula","food"},new String[] {"kitanda","bed"},new String[] {"tumbili","monkey"},new String[] {"chama","society"},new String[] {"kiti","chair"},new String[] {"tunda","fruit"},new String[] {"chanjo","scissors"},new String[] {"kofia","hat"},new String[] {"ufu","death"},new String[] {"chapati","bread"},new String[] {"kuku","chicken"},new String[] {"ujuzi","knowledge"},new String[] {"chunga","pot"},new String[] {"lango","door"},new String[] {"wakati","alarm"},new String[] {"degaga","glasses"},new String[] {"likizo","holidays"},new String[] {"wimbo","song"},new String[] {"dhoruba","storm"},new String[] {"limau","lemon"},new String[] {"wingu","cloud"},new String[] {"duara","wheel"}
		};

		for (int i = 0; i < tiles.size(); i++) {
			Item item = new Item(swen[i][0], swen[i][1]);
			if (i==baseTiles[0]) item = new Item ("","");
			if (i==baseTiles[1]) item = new Item ("","");
			if (i==baseTiles[2]) item = new Item ("","");

			// Calculate word positions
			GlyphLayout wordGlyph = new GlyphLayout(gillsans, swen[i][0]);
			GlyphLayout translationGlyph = new GlyphLayout(gillsans, swen[i][1]);

			Rectangle wordPosition = new Rectangle(
					tiles.get(i).x + TILE_SIZE/2 - wordGlyph.width/2,
					tiles.get(i).y + TILE_HEIGHT+15,
					wordGlyph.width, wordGlyph.height
			);

			Rectangle translationPosition = new Rectangle(
					tiles.get(i).x + TILE_SIZE/2 - translationGlyph.width/2,
					tiles.get(i).y + TILE_HEIGHT+15 - 32,
					translationGlyph.width, translationGlyph.height
			);

			item.setWordPosition(wordPosition);
			item.setTranslationPosition(translationPosition);

			items.put(tiles.get(i),item);
		}
	}

	public void startTrial() {
		inTrial = true;

		// Center on trial tile and zoom in
		prevPos = cam.position;
		prevZoom = cam.zoom;
		cam.position.set(activeTile.x + TILE_SIZE / 2, activeTile.y, 0);
		cam.zoom = 0.4f;

		// Grey out other tiles
		for (Rectangle tile2 : tiles) {
			if (activeTile != tile2) {
				Color c = colors.get(tile2);
				colors.put(tile2, new Color(c.r, c.g, c.b, 0.3f));
			}
		}
		colors.get(activeTile).a = BOARD_OPACITY;

		// Display the keyboard
		Gdx.input.setOnscreenKeyboardVisible(true);
	}

	/**
	 * The user moves out of trial mode:
	 * Clear the answer text, disable the keyboard,
	 * restore the zoom and camera position, restore the tile colors
	 */
	public void endTrial() {
		answer = "";
		Gdx.input.setOnscreenKeyboardVisible(false);
		for (Rectangle tile : tiles)
			updateColor(tile);
		inTrial = false;
		cam.zoom = prevZoom;
		cam.position.set(prevPos);
	}

	public boolean isBaseTile(Rectangle tile) {
		return (tiles.get(baseTiles[0]) == tile ||
				tiles.get(baseTiles[1]) == tile ||
				tiles.get(baseTiles[2]) == tile);
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
		if (inTrial) {
			GlyphLayout answerGlyph = new GlyphLayout(gillsans, answer);
			float x = activeTile.x + TILE_SIZE/2 - answerGlyph.width/2;
			gillsans.draw(batch, answer, x, activeTile.y + 64 + answerGlyph.height);
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
		Vector3 worldCoords = cam.unproject(new Vector3(x,y,0));

		for (Rectangle tile : tiles) {
			if ( tile.contains(worldCoords.x,worldCoords.y) ) {
				Gdx.app.log("test",tile.x + " " + tile.y);
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
		Vector3 worldCoords = cam.unproject(new Vector3(x,y,0));

		for (Rectangle tile : tiles) {
			if ( tile.contains(worldCoords.x,worldCoords.y) &&
					frontier.contains(tile) && !isBaseTile(tile) ) {
				activeTile = tile;
				startTrial();
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
		if (inTrial)
			return false;

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
		currentZoom = cam.zoom;
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (inTrial)
			return false;

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
