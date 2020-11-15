package edu.lewisu.cs.mikalaspencer;

// Mikala Spencer
// 2020-11-14
// This program is a sound board of 8 sounds, labels, and images.

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

abstract class CameraEffect 
{
	protected OrthographicCamera cam;
	protected int duration, progress;
	protected float imgX, imgY;
	protected ShapeRenderer renderer;
	protected SpriteBatch batch;

	// Constructor
	public CameraEffect(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer)
	{
		this.cam = cam;
		this.duration = duration;
		this.batch = batch;
		this.renderer = renderer;
		progress = duration;
	}

	public boolean isActive() 
	{
		// Returns if the camera is active or not
		return (progress < duration);
	}

	public void updateCamera() 
	{
		// Update the camera
		cam.update();

		if (renderer != null) 
		{
			// Update renderer
			renderer.setProjectionMatrix(cam.combined);
		}

		if (batch != null) 
		{
			// Update batch
			batch.setProjectionMatrix(cam.combined);
		}
	}

	public void start() 
	{
		progress = 0;
	}
}

// Camera Effect of moving the camera
class CameraMove extends CameraEffect 
{
	private int intensity;
	private int speed;

	public CameraMove(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer) 
	{
		super(cam, duration, batch, renderer);
	}

	public int getIntensity() 
	{
		return intensity;
	}

	public void setIntensity(int intensity) 
	{
		if (intensity < 0) 
		{
			this.intensity = 0;
		} 
		else 
		{
			this.intensity = intensity;
		}
	}

	public int getSpeed() 
	{
		return speed;
	}

	public void setSpeed(int speed) 
	{
		if (speed < 0) 
		{
			speed = 0;
		} 
		else 
		{
			if (speed > duration) 
			{
				speed = duration / 2;
			} 
			else 
			{
				this.speed = speed;
			}
		}
	}

	@Override
	public boolean isActive() 
	{
		return super.isActive() && speed > 0;
	}

	public CameraMove(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer, int intensity, int speed) 
	{
		super(cam, duration, batch, renderer);
		setIntensity(intensity);
		setSpeed(speed);
	}

	public void play() 
	{
		if (isActive()) 
		{
			if (progress % speed == 0) 
			{
				cam.rotate(180f);
			}

			progress++;

			if (!isActive()) 
			{
				cam.translate(0, 0);
			}

			updateCamera();
		}
	}

	public void start() 
	{
		super.start();
		updateCamera();
	}
}

class SoundLabel 
{
	private Label label;
	private Sound sound;

	// Get function for label to draw
	public Label getLabel() 
	{
		return label;
	}

	// This sets up a SoundLabel that is ready to be clicked & play sound.
	public SoundLabel(String pathToSound, String textToShow, LabelStyle style, int xpos, int ypos) {
		sound = Gdx.audio.newSound(Gdx.files.internal(pathToSound));
		label = new Label(textToShow, style);
		label.setPosition(xpos, ypos);
	}

	public void playSound(float vol) 
	{
		sound.play(vol);
	}
	
	// This determines if the label was clicked.
	public boolean wasClicked(int x, int y) 
	{
		if (x >= label.getX() && x <= label.getX() + label.getWidth() && y >= label.getY() && y <= label.getY() + label.getHeight()) 
		{
			return true;
		} 
		else 
		{
			return false;
		}
	}
}

public class SoundBoard extends ApplicationAdapter 
{
	SpriteBatch batch, batch2;
	TextureRegion background, img;
	Texture tsumiki, mioda, nanami, komaeda, iruma, robot, evilBear, goodBear, musicNote;
	Texture tsumiki2, mioda2, nanami2, komaeda2, iruma2, robot2, evilBear2, goodBear2, junko, volDown, volUp;
	OrthographicCamera cam;
	int WIDTH, HEIGHT;
	float vol;
	LabelStyle labelStyle;
	SoundLabel mikan, ibuki, chiaki, nagito, miu, kiibo, monokuma, usami;
	CameraMove effect;
	int imgX, imgY, imgOX, imgOY, imgWidth, imgHeight, imgAngle;

	public void setupLabelStyle() 
	{
		labelStyle = new LabelStyle();
		labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/labelFont.fnt"));
	}

	// Render the sound label on the screen.	
	public void drawSoundLabel() 
	{
		mikan.getLabel().draw(batch, 1);
		ibuki.getLabel().draw(batch, 1);
		chiaki.getLabel().draw(batch, 1);
		nagito.getLabel().draw(batch, 1);
		miu.getLabel().draw(batch, 1);
		kiibo.getLabel().draw(batch, 1);
		monokuma.getLabel().draw(batch, 1);
		usami.getLabel().draw(batch, 1);
	}

	@Override
	public void create() 
	{
		batch = new SpriteBatch();
		batch2 = new SpriteBatch();

		background = new TextureRegion(new Texture("trialBackground.png"));

		musicNote = new Texture("musicNote.png");

		// Volume controls
		volDown = new Texture("volDown.png");
		volUp = new Texture("volUp.png");
		vol = 0.5f;

		// Images for sound labels
		tsumiki = new Texture("mikan.png");
		mioda = new Texture("ibuki.png");
		nanami = new Texture("chiaki.png");
		komaeda = new Texture("nagito.png");
		iruma = new Texture("miu.png");
		robot = new Texture("kiibo.png");
		evilBear = new Texture("monokuma.png");
		goodBear = new Texture("usami.png");
		junko = new Texture("junko.png");

		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();

		img = new TextureRegion(musicNote);
		imgAngle = 0;
		imgWidth = musicNote.getWidth();
		imgHeight = musicNote.getHeight();
		imgX = 0;
		imgY = 0;
		imgOX = imgWidth/2;
		imgOY = imgHeight/2;

		// Camera
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH/2,HEIGHT/2);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch2.setProjectionMatrix(cam.combined);

		effect = new CameraMove(cam, 100, batch, null, 2, 10);
		effect = new CameraMove(cam, 100, batch2, null, 2, 10);

		setupLabelStyle();

		// Text Labels for sounds
		mikan = new SoundLabel("audio/mikanName.mp3", "Mikan", labelStyle, 50, 250);
		ibuki = new SoundLabel("audio/ibuki.mp3", "Ibuki", labelStyle, 50, 150);
		chiaki = new SoundLabel("audio/chiaki.mp3", "Chiaki", labelStyle, 50, 50);
		nagito = new SoundLabel("audio/nagito.mp3", "Nagito", labelStyle, 400, 50);
		miu = new SoundLabel("audio/miu.mp3", "Miu", labelStyle, 400, 150);
		kiibo = new SoundLabel("audio/kiibo.mp3", "Kiibo", labelStyle, 400, 250);
		monokuma = new SoundLabel("audio/monokuma.mp3", "Monokuma", labelStyle, 400, 350);
		usami = new SoundLabel("audio/usami.mp3", "Monomi", labelStyle, 50, 350);

		// Starting camera effect
		effect.start();
		effect.play();
	}

	/**
	 * This function allows the musicNote image to move where the user clicks.
	 */
	public void move()
	{
		if (Gdx.input.isTouched())
		{
			String btnName;
			if (Gdx.input.isButtonPressed(Buttons.LEFT))
			{
				btnName = "left";
			}
			else
			{
				btnName = "right";
			}

			if (btnName.equals("right"))
			{
				imgOX = Gdx.input.getX();
				imgOY = HEIGHT - Gdx.input.getY();
			}
			else
			{
				// Moves the image center to where the mouse clicked in window
				imgX = Gdx.input.getX() - imgWidth/2;
				imgY = HEIGHT - Gdx.input.getY() - imgHeight/2;
			}
		}
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}

	/**
	 * This function displays the filtered version of the images when the user
	 * clicks and/or holds the left mouse key.
	 * 
	 * It displays a pink version of the original image to show that sound has been selected.
	 */
	public boolean touchDown(int imgX, int imgY, Texture image, SoundLabel label2)
	{
		batch2.setColor(Color.PINK);
		batch2.draw(image, imgX, imgY);
		batch2.setColor(Color.WHITE);
		return true;
	}

	public void input()
	{
		// When the spacebar is hit, junko image will appear
		if (Gdx.input.isKeyPressed(Keys.SPACE))
		{
			batch2.draw(junko, 100, 100);
		}
	}

	@Override
	public void render () 
	{
		Gdx.gl.glClearColor(98/255f, 102/255f, 101/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Moves the music note image to where the user clicks
		move();

		// Rotate the camera
		effect.play();

		batch.begin();
		batch2.begin();

		// If user clicks left mouse button
		if (Gdx.input.isButtonJustPressed(Buttons.LEFT))
		{
			// If either of the volume images has been clicked
			if (Gdx.input.getX() >= 500 && Gdx.input.getX() <= 599 && HEIGHT-Gdx.input.getY() >= 0 && HEIGHT-Gdx.input.getY() <= 50)
			{
				// Lower the volume
				vol = vol - 0.1f;
			}
			else if (Gdx.input.getX() >= 600 && Gdx.input.getX() <= 700 && HEIGHT-Gdx.input.getY() >= 0 && HEIGHT-Gdx.input.getY() <= 50)
			{
				// Raise the volume
				vol = vol + 0.1f;
			}

			// If label was clicked
			else if (mikan.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				mikan.playSound(vol);
			}
			else if (ibuki.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				ibuki.playSound(vol);
			}
			else if (chiaki.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				chiaki.playSound(vol);
			}
			else if (nagito.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				nagito.playSound(vol);
			}
			else if (miu.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				miu.playSound(vol);
			}
			else if (kiibo.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				kiibo.playSound(vol);
			}
			else if (monokuma.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				monokuma.playSound(vol);
			}
			else if (usami.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				usami.playSound(vol);
			}
		}

		// If user clicks and/or holds left mouse button
		if (Gdx.input.isButtonPressed(Buttons.LEFT))
		{
			// If label was clicked
			if (mikan.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(150, 250, tsumiki, mikan);
			}
			else if (ibuki.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(150, 150, mioda, ibuki);
			}
			else if (chiaki.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(150, 50, nanami, chiaki);
			}
			else if (nagito.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(320, 50, komaeda, nagito);
			}
			else if (miu.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(320, 150, iruma, miu);
			}
			else if (kiibo.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(320, 240, robot, kiibo);
			}
			else if (monokuma.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(330, 350, evilBear, monokuma);
			}
			else if (usami.wasClicked(Gdx.input.getX(),HEIGHT-Gdx.input.getY()))
			{
				touchDown(175, 350, goodBear, usami);
			}
		}

		input();

		batch.draw(background, 0, 0);

		// Update the music note's location
		batch.draw(img, imgX, imgY, imgOX, imgOY, imgWidth, imgHeight, 1f, 1f, imgAngle);

		// Display the original images next to their labels
		batch.draw(tsumiki, 150, 250);
		batch.draw(mioda, 150, 150);
		batch.draw(nanami, 150, 50);
		batch.draw(komaeda, 320, 50);
		batch.draw(iruma, 320, 150);
		batch.draw(robot, 320, 240);
		batch.draw(evilBear, 330, 350);
		batch.draw(goodBear, 175, 350);

		// Display the volume controls
		batch.draw(volDown, 550, -10);
		batch.draw(volUp, 600, 0);

		// Display the labels
		drawSoundLabel();
		batch2.end();
		batch.end();
	}

	@Override
	public void dispose () 
	{
		batch.dispose();
		batch2.dispose();
	}
}