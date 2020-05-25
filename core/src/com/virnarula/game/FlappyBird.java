package com.virnarula.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Arrays;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	Texture topTube, bottomTube;
	Texture gameOver;
	Circle birdCircle;
	Rectangle[] topRect;
	Rectangle[] bottomRect;
	BitmapFont font;
//	ShapeRenderer renderer;
	int flapState = 0;
	int score;
	int scoringTube;

	double birdY = 0;
	double velocity = 0;
	double gravity = 2;
	int gap = 400;
	double maxTubeOffset;
	Random randomGen;
	double tubeVelocity = 4;
	int numberOfTubes = 4;
	double[] tubeX = new double[numberOfTubes];
	double[] tubeOffset = new double[numberOfTubes];
	double distanceBetweenTubes;

	int gameState = 0;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameOver = new Texture("gameover.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;
		randomGen = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() / 4 * 3;
//		renderer = new ShapeRenderer();
		birdCircle = new Circle();
		topRect = new Rectangle[numberOfTubes];
		Arrays.fill(topRect, new Rectangle());
		bottomRect = new Rectangle[numberOfTubes];
		Arrays.fill(bottomRect, new Rectangle());
		score = 0;
		scoringTube = 0;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		startGame();

	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;

        for(int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomGen.nextDouble() - 0.5) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth() * 3 / 2- topTube.getWidth() / 2 + i * distanceBetweenTubes;
        }
    }

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		for (int i = 0; i < numberOfTubes; i++) {
			batch.draw(topTube, (int) tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + (int) tubeOffset[i]);
			batch.draw(bottomTube, (int) tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + (int) tubeOffset[i]);
		}

		if(gameState == 1) {
			// If touched
			if(Gdx.input.justTouched()) {
				velocity = -30;

			}
			// if point is scored
			if (tubeX[scoringTube] + topTube.getWidth() < Gdx.graphics.getWidth() / 2) {
				score++;
				Gdx.app.log("Score", String.valueOf(score));
				if(scoringTube < numberOfTubes - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}
			// Update tube values and draw them
			for (int i = 0; i < numberOfTubes; i++) {

				if(tubeX[i] < 0 - topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGen.nextDouble() - 0.5) * (Gdx.graphics.getHeight() - gap - 200);
				}

				tubeX[i] = tubeX[i] - tubeVelocity;
			}
			// Update bird position and velocity as long as it is not touching the ground
			if(birdY > 0) {
				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}
			// Check for collisions
			for(int i = 0; i < numberOfTubes; i++) {
				topRect[i].set((float) tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + (int) tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomRect[i].set((float) tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 + (int) tubeOffset[i] - bottomTube.getHeight(), topTube.getWidth(), topTube.getHeight());

				if (Intersector.overlaps(birdCircle, topRect[i]) || Intersector.overlaps(birdCircle, bottomRect[i])) {
					Gdx.app.log("Collision", "indeed");
					gameState = 2;
				}
			}

			birdCircle.set(Gdx.graphics.getWidth() / 2, (float) (birdY + birds[flapState].getHeight() / 2), birds[flapState].getWidth() / 2);
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
            if (Gdx.input.justTouched()) {
                gameState = 0;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
            }
		}

		flapState = flapState == 0 && gameState != 2 ? 1 : 0;

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, (int)birdY);
		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 20 * 19);
		batch.end();

//		renderer.begin(ShapeRenderer.ShapeType.Filled);
//		renderer.setColor(Color.RED);
//		renderer.rect(topRect[i].x, topRect[i].y, topRect[i].width, topRect[i].height);
//		renderer.rect(bottomRect[i].x, bottomRect[i].y, bottomRect[i].width, bottomRect[i].height);

//		renderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//		renderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
