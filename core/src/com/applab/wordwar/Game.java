package com.applab.wordwar;

import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.Item;
import com.applab.wordwar.model.GameTile;
import com.applab.wordwar.model.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Game implements Screen {
	private SpriteBatch batch;
	protected OrthographicCamera cam;
	protected ArrayList<Rectangle> tiles; 	// Tile positions
	protected ArrayList<Rectangle> frontier; 	// Tile frontier
	protected Map<Rectangle, Item> items; 	// Tile-Item pairs
	protected Map<Rectangle, int[]> captures;	// Tile-Capture pairs
	private Stage HUD;
	private Actor scoreBoard, endScreen, helpLabel;

	// Textures
	private Texture tileW;
	private Texture bg;

	// Fonts
	private BitmapFont gillsans;

	private float DEFAULT_OPACITY = 0.85f;	// Opacity of all non-captured tiles (fog of war)
	private float BOARD_OPACITY = 0.95f;	// Opacity of captured or frontier tiles
	private float TRIAL_OPACITY = 0.3f;		// Opacity of all non-active tiles during trial

	public Color[][][] getColor() {
		return color;
	}

	// Tile colors
	private Color[][][] color = new Color[][][] {
		new Color[][] {
			new Color[] {
				new Color(1.00f, 1.00f, 1.00f, DEFAULT_OPACITY),// Default color
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
				new Color(0.35f, 0.00f, 0.64f, BOARD_OPACITY),	// Player 0 & 1
				new Color(0.47f, 0.30f, 0.07f, BOARD_OPACITY)	// Player 0 & 1 & 2
			}
		}
	};

	// Constants
	protected final int WORLD_WIDTH = 3000;
	protected final int WORLD_HEIGHT = 4500;
	private final int BOARD_SIZE = 5;
	private final int TILE_SIZE = 256;
	private final int TILE_WIDTH = 222;
	private final int TILE_HEIGHT = 192; // 3/4th of tile
	protected float currentZoom = 2f;
	protected final float zoomLimit = 2f;
	protected final float panRate = 0.75f;
	protected int PLAYER_ID = 1;	// 0, 1 or 2 (assigned by server at start of the game)
	private int[] baseTiles = {32, 38 ,53}; // Starting tiles for board size 5 (hardcoded)
	private final float INCORRECT_FEEDBACK_TIME = 4; // Time to show the correct answer after a trial
	private final float CORRECT_FEEDBACK_TIME = 0.6f;
	private boolean showNumbersOnTiles = false; // For developer purposes
	private final long GAME_DURATION = 600; // Game duration in seconds

	// Variables
	private boolean inTrial = false;
	private boolean inFeedback = false;
	protected boolean firstKeyPressed = false;
	private boolean saveStats = false;
	private String answer = "";
	private Vector3 prevPos; // Store the board position when in trial mode
	private float prevZoom; // Store the zoom when in trial mode
	protected int[] scores = {1,1,1};
	private long endTime = 0;
	protected Rectangle activeTile = null; // The tile active during a trial
	private Rectangle disabledTile = null; // The tile after incorrect feedback will be disabled for one trial

	private MainClass app;
	public int[] getScores() {
		return scores;
	}

	public Game(MainClass app) {
		this.app = app;
		// Get player color
		for (Player player : app.getClient().getGameModel().getPlayers()) {
			if(player.getId() == app.getClient().getPlayer().getId()){
				PLAYER_ID = player.getColor();
			}
		}
	}

	public String getTimeLeft() {
		long timeLeft = endTime-System.currentTimeMillis();
		return String.format("%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(timeLeft),
				TimeUnit.MILLISECONDS.toSeconds(timeLeft) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft))
		);
	}

	public boolean isGameEnd() {
		return endTime < System.currentTimeMillis();
	}

	public MainClass getApp() {
		return app;
	}

	public boolean isInTrial() {
		return inTrial;
	}

	public boolean isInFeedback() {
		return inFeedback;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();

		tiles = new ArrayList<Rectangle>();
		frontier = new ArrayList<Rectangle>();
		items = new HashMap<Rectangle, Item>();
		captures = new HashMap<Rectangle, int[]>();

		// Load Fonts
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

		// HUD
		HUD = new Stage(new ScreenViewport());
		scoreBoard = new ScoreBoard(this);
		scoreBoard.setSize(0.35f * w, 0.20f * w);
		scoreBoard.setPosition(w-scoreBoard.getWidth()-0.05f*w, h-scoreBoard.getHeight()-0.05f*h);
		HUD.addActor(scoreBoard);
		endScreen = new EndScreen(this);
		endScreen.setSize(0.8f * w, 0.35f * h);
		endScreen.setPosition(0.1f * w, h/2 - endScreen.getHeight()/2);
		endScreen.setVisible(false);
		HUD.addActor(endScreen);
		helpLabel = new HelpLabel(this);
		helpLabel.setSize(w, 0.1f * h);
		helpLabel.setPosition(0, h - helpLabel.getHeight());
		helpLabel.setVisible(true);
		helpLabel.setVisible(false);
		HUD.addActor(helpLabel);

		// Input Processors
		GestureDetector gestureDetector = new GestureDetector(new wwGestureListener(this));
		com.applab.wordwar.wwInputProcessor inputProcessor = new com.applab.wordwar.wwInputProcessor(this);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(inputProcessor);
		multiplexer.addProcessor(gestureDetector);
		Gdx.input.setInputProcessor(multiplexer);
		Gdx.input.setCatchBackKey(true);

		// Prepare game
		endTime = System.currentTimeMillis() + GAME_DURATION*1000;
		generateTiles();
		getBoardState();
		assignWordsToTiles();
		updateFrontier();
	}

	/**
	 * Get the owner status of tiles and color them
	 * TODO: maybe use same format as server
	 */
	public void getBoardState() {
		GameModel gm = app.getClient().getGameModel();

		ArrayList<GameTile> map = gm.getMap();
		int tileIdx = 0;
		for (int i = 0; i < tiles.size(); i++) {
			int[] capturedState = new int[]{0, 0, 0};
			if (i == baseTiles[0]) {
				capturedState = new int[]{1, 0, 0};
			} else if (i == baseTiles[1]) {
				capturedState = new int[]{0, 1, 0};
			} else if (i == baseTiles[2]) {
				capturedState = new int[]{0, 0, 1};
			} else {
				GameTile tile = map.get(tileIdx);
				capturedState[0] = tile.isOwnedByBlue() ? 1: 0;
				capturedState[1] = tile.isOwnedByRed() ? 1: 0;
				capturedState[2] = tile.isOwnedByYellow() ? 1: 0;
				tileIdx++;
			}

			Rectangle tile = tiles.get(i);
			captures.put(tile, capturedState);
		}
		app.getClient().setStateChanged(false);
	}

	public void giveTranslation() {
		Item item = items.get(activeTile);

		// Give feedback
		if (answer.equals(item.getTranslation())) {
			correctFeedback();
		} else {
			if (!item.isNovel()) {
				incorrectFeedback();
			} else {
				((HelpLabel)helpLabel).setLabel("Incorrect!", new Color(1f,0f,0f,0.5f));
				answer = "";
			}
		}
	}

	private void correctFeedback() {
		Item item = items.get(activeTile);

		if (item.isNovel()) {
			// For a study trial, stay for the test trial
			answer = "";
			item.setNovel(false);
			firstKeyPressed = false;
			startTrial(false); // Start the test trial
		} else {
			// Feedback text
			((HelpLabel)helpLabel).setLabel("Correct!", new Color(0f,1f,0f,0.5f));
			inFeedback = true;
			captureTile(activeTile);

			// End the trial only after the feedback
			Timer.schedule(new Timer.Task(){
				@Override
				public void run() {
					endTrial();
					updateFrontier();
				}
			}, CORRECT_FEEDBACK_TIME);
		}
	}
	private void incorrectFeedback() {
		answer = items.get(activeTile).getTranslation();
		Gdx.input.setOnscreenKeyboardVisible(false);

		// Feedback text
		((HelpLabel)helpLabel).setLabel("Incorrect!", new Color(1f,0f,0f,0.5f));
		inFeedback = true;

		Timer.schedule(new Timer.Task(){
			@Override
			public void run() {
				endTrial();
				disabledTile = activeTile;
				updateFrontier();
			}
		}, INCORRECT_FEEDBACK_TIME);
	}

	private void captureTile(Rectangle tile) {
		int tileID = tiles.indexOf(tile);
		int offset = 0;
		for(int i=0; i<baseTiles.length; i++){
			if(tileID > baseTiles[i]){
				offset++;
			}
		}
		tileID -= offset;
		app.getClient().captureTile(tileID);
	}

	/**
	 * Get the color for a tile
	 * The color is correlated to the activation value of the tile
	 * @param tile
     */
	public Color getTileColor(Rectangle tile) {
		int[] c = captures.get(tile);
		float r, g, b;

		// Get the default color for the captured state
		Color col = color[c[0]][c[1]][c[2]];

		// Set tile opacity
		float opacity;
		if (inTrial && tile != activeTile) {
			opacity = TRIAL_OPACITY;
		} else if (frontier.contains(tile)) {
			opacity = BOARD_OPACITY;
		} else {
			opacity = col.a;
		}

		/*
		// apply activation scale if captured
		if (c[PLAYER_ID] == 1) {
			int i = tiles.indexOf(tile);
			//app.getClient().getGameModel().
			// current in int array for tile ids
			// current = max(0,current+0.5)
			// s = current/(A+0.5)

			float s = 0.5f;
			r = 1-s*(1-col.r);
			g = 1-s*(1-col.g);
			b = 1-s*(1-col.b);
		} else {
			r = col.r;
			g = col.g;
			b = col.b;
		}
		*/

		return new Color(col.r, col.g, col.b, opacity);
	}

	/**
	 * Update the frontier for the player and color the tiles
	 * The frontier contains all the tiles that the player can capture
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

		// Add the non-captured neighbors to the frontier
		frontier.clear();
		for (Rectangle neighbor : neighbors) {
			if (captures.get(neighbor)[PLAYER_ID]==0 && neighbor != disabledTile) {
				// Determine how many captured tiles the neighbor is adjacent to
				int n = 0;
				for (int i = 0; i < neighbors.size(); i++) {
					if (neighbors.get(i) == neighbor) n++;
				}
				if (n>0 || area.size() == 1) { // n>1 if it needs 2 adjacent tiles
					frontier.add(neighbor);
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
		GameModel gm = app.getClient().getGameModel();

		ArrayList<GameTile> map = gm.getMap();

		int wordIndex = 0;
		for (int i = 0; i < tiles.size(); i++) {
			Item item = map.get(wordIndex).getItem();
			if (i==baseTiles[0] || i==baseTiles[1] || i==baseTiles[2]) {
				item = new Item("", "");
			} else {
				wordIndex++;
			}

			// Calculate word positions
			GlyphLayout wordGlyph = new GlyphLayout(gillsans, item.getWord());
			GlyphLayout translationGlyph = new GlyphLayout(gillsans, item.getTranslation());

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

			items.put(tiles.get(i), item);
		}
	}

	public void startTrial(boolean savePosZoom) {
		inTrial = true;
		scoreBoard.setVisible(false);

		// Center on trial tile and zoom in
		if (savePosZoom) {
			prevPos = cam.position;
			prevZoom = cam.zoom;
		}
		cam.position.set(activeTile.x + TILE_SIZE / 2, activeTile.y + 32, 0);
		cam.zoom = 0.4f;

		// Display the keyboard
		Gdx.input.setOnscreenKeyboardVisible(true);

		// Add the item to the slim stampen model
		Item activeItem = items.get(activeTile);
		if (activeItem.isNovel()) {
			app.getClient().sendAddNewItemMessage(activeItem);
		} else {
			// (test trial) Time when the event starts
			app.getClient().sendPracticeEventMessage(items.get(activeTile), System.currentTimeMillis());
		}

		helpLabel.setVisible(true);
		String message = (activeItem.isNovel()) ? "Study Trial" : "Rehearsal";
		((HelpLabel)helpLabel).setLabel(message, new Color(0f,0f,0f,0.5f));
	}

	/**
	 * The user moves out of trial mode:
	 * Clear the answer text, disable the keyboard,
	 * restore the zoom and camera position, restore the tile colors
	 */
	public void endTrial() {
		answer = "";
		Gdx.input.setOnscreenKeyboardVisible(false);
		inTrial = false;
		inFeedback = false;
		cam.zoom = prevZoom;
		cam.position.set(prevPos);
		scoreBoard.setVisible(true);
		helpLabel.setVisible(false);
		((HelpLabel)helpLabel).setLabel("", new Color(0f,0f,0f,0.5f));
		firstKeyPressed = false;
		disabledTile = null;
	}

	public boolean isBaseTile(Rectangle tile) {
		return (tiles.get(baseTiles[0]) == tile ||
				tiles.get(baseTiles[1]) == tile ||
				tiles.get(baseTiles[2]) == tile);
	}

	private long time = 0;
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.13f, 0.23f, 0.26f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(bg, (WORLD_WIDTH-bg.getWidth())/2, (WORLD_HEIGHT-bg.getHeight())/2 );

		// Display the End Screen if there is no time left
		if (isGameEnd()) {
			scoreBoard.setVisible(false);
			endScreen.setVisible(true);
			Gdx.input.setCatchBackKey(false);
			if (!saveStats) {
				// Save player stats
				app.getClient().sendEndGameMessage();
				saveStats = true;
			}
		} else {

			// Update board and score from server
			if (app.getClient().stateChanged()) {
				getBoardState();
				updateFrontier();
				ArrayList<Player> players = app.getClient().getGameModel().getPlayers();
				for (int i = 0; i < players.size(); i++) {
					scores[i] = players.get(i).getScore();
				}
			}

			// Check slim stampen for items below threshold every second
			if (System.currentTimeMillis() - time > 1000 && !inTrial) { // && !inTrial 	else the word might have a missing alpha
				app.getClient().sendRequestTrialMessage(System.currentTimeMillis());
				time = System.currentTimeMillis();
			}

		}

		// Draw the tiles of the board
		for (Rectangle tile : tiles) {
			batch.setColor(getTileColor(tile)); // colors.get(tile)
			batch.draw(tileW, tile.x, tile.y);
		}
		batch.setColor(1, 1, 1, 1);

		// Draw the words on the tiles
		for (Rectangle tile : tiles) {
			Item item = items.get(tile);
			Rectangle wordPos = item.getWordPosition();
			Rectangle transPos = item.getTranslationPosition();
			gillsans.draw(batch, item.getWord(), wordPos.x, wordPos.y);
			if (inTrial && item.isNovel() && items.get(activeTile).equals(item))
				gillsans.draw(batch, item.getTranslation(), transPos.x, transPos.y);
		}

		// Draw the answer text in trial mode
		if (inTrial) {
			GlyphLayout answerGlyph = new GlyphLayout(gillsans, answer);
			float x = activeTile.x + TILE_SIZE/2 - answerGlyph.width/2;
			gillsans.draw(batch, answer, x, activeTile.y + 64 + answerGlyph.height);
		}

		// For debugging: draw the tile indices
		if (showNumbersOnTiles) {
			gillsans.setColor(new Color(0,0,0,1));
			for (int i = 0; i < tiles.size(); i++) {
				gillsans.draw(batch, String.valueOf(i), tiles.get(i).x + 96, tiles.get(i).y + 128);
			}
		}
		batch.end();


		HUD.draw();
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
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}


}
