package com.applab.wordwar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class wwInputProcessor implements InputProcessor {

        private Game game;

        public wwInputProcessor(Game game) {
            this.game = game;
        }

        public boolean keyDown (int keycode) {
            return false;
        }

        public boolean keyUp (int keycode) {
            if (game.isInTrial()) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ENTER) {
                    game.giveTranslation();
                } else if (keycode == Input.Keys.BACKSPACE && game.getAnswer().length() > 0) {
                    game.setAnswer(game.getAnswer().substring(0, game.getAnswer().length() - 1));
                }
            } else {
                if (keycode == Input.Keys.BACK) {
                    // TODO: Options or game menu? Exit option?
                }
            }
            return false;
        }

        public boolean keyTyped (char character) {
            if (character == ' ' || 'A' <= character && character <= 'Z' || 'a' <= character && character <= 'z')
                game.setAnswer(game.getAnswer() + character);
            return false;
        }

        public boolean touchDown (int x, int y, int pointer, int button) {
            return false;
        }

        public boolean touchUp (int x, int y, int pointer, int button) {
            return false;
        }

        public boolean touchDragged (int x, int y, int pointer) {
            return false;
        }

        public boolean mouseMoved (int x, int y) {
            return false;
        }

        public boolean scrolled (int amount) {
            return false;
        }
}
