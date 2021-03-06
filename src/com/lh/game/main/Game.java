package com.lh.game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import com.lh.framework.util.InputHandler;
import com.lh.game.state.LoadState;
import com.lh.game.state.State;

@SuppressWarnings("serial")
public class Game extends JPanel implements Runnable{

	private int gameWidth;
	private int gameHeight;
	private Image gameImage;
	
	private Thread gameThread;
	private volatile boolean running;
	private volatile State currentState;
	
	private InputHandler inputHandler;
	
	public Game(int gameWidth, int gameHeight) {
		
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		setPreferredSize(new Dimension(gameWidth, gameHeight));
		setBackground(Color.BLACK);
		setFocusable(true);
		requestFocus();
		
	}
	
	public void setCurrentState(State newState){
		System.gc();
		newState.init();
		currentState = newState;
		inputHandler.setCurrentState(newState);
	}

	@Override
	public void addNotify() {
		// TODO Auto-generated method stub
		super.addNotify();
		initInput();
		setCurrentState(new LoadState());
		initGame();
	}
	
	public void initGame(){
		running = true;
		gameThread = new Thread(this, "Game Thread");
		gameThread.start();
	}
	
	private void initInput(){
		inputHandler = new InputHandler();
		addKeyListener(inputHandler);
		addMouseListener(inputHandler);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		long updateDurationMillis = 0;
		long sleepDurationMillis =0;
		while(running){
			long beforeUpdateRender = System.nanoTime();
			long deltaMillis = updateDurationMillis + sleepDurationMillis;
			
			updateAndRender(deltaMillis);
			
			updateDurationMillis = (System.nanoTime() - beforeUpdateRender) /1000000L ;
			sleepDurationMillis = Math.max(2, 17 - updateDurationMillis);
			try{
				Thread.sleep(sleepDurationMillis);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
			
		}
		
		System.exit(0);
	}

	private void updateAndRender(long deltaMillis) {
		currentState.update(deltaMillis / 1000f);
		prepareGameImage();
		currentState.render(gameImage.getGraphics());
		rendGameImage(getGraphics());
	}
	
	private void prepareGameImage(){
		if (gameImage == null){
			gameImage = createImage(gameWidth, gameHeight);
		}
	}
	
	public void exit(){
		running = false ;
	}
	
	private void rendGameImage(Graphics g){
		if (gameImage != null){
			g.drawImage(gameImage, 0, 0, null);
		}
		g.dispose();
	}
	
	
	
	
	
}
