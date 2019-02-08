package com.example.macstudent.test1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable{

    int screenHeight;
    int screenWidth;

    boolean gameIsRunning;

    Thread gameThread;

    SurfaceHolder holder;
    Canvas canvas;
    Paint paintBrush;

    private int gpXPosition;
    private int gpYPosition;

    private  int snakeLength;

    private int snakeXPositions;
    private int snakeYPositions;

    private int snakeDirection;

    private int score = 0;
    private  int racketXPosition;
    private int racketYPosition;
private int gpDirection;
   private int racketDirection;
    private int blockSize;

    private  final int NUM_BLOCKS_WIDE = 40;
    private  int numBlocksHigh;


    int maxX;
    int maxY;
    int minX;
    int minY;

    public  GameEngine(Context context, int h, int w, Point p){
        super(context);

        this.screenHeight = p.y;
        this.screenWidth = p.x;


        holder = this.getHolder();
        paintBrush = new Paint();


        this.blockSize = this.screenWidth/this.NUM_BLOCKS_WIDE;
        this.numBlocksHigh = this.screenHeight/ this.blockSize;



        this.minX = 7;
        this.minY = 1;
        this.maxX = (NUM_BLOCKS_WIDE -7);
        this.maxY =  (numBlocksHigh -57);




        this.newGame();

    }
    @Override
    public void run() {
        while (gameIsRunning == true)  {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }

    private void newGame(){
        spawnSnake();

        // Add the first apple
        spawnGP();
    spawnRacket();
        // Reset the score
        score = 0;
    }

    private void spawnSnake(){

        this.snakeLength =1;
        this.snakeXPositions = NUM_BLOCKS_WIDE /2;
        this.snakeYPositions = numBlocksHigh-20;

        //this.snakeDirection = 0;
    }

    private void spawnGP(){


        gpXPosition = NUM_BLOCKS_WIDE/2;
        gpYPosition = numBlocksHigh-60;


    }
    private void spawnRacket(){


        this.racketXPosition = NUM_BLOCKS_WIDE /2;
        this.racketYPosition = numBlocksHigh-15;
        this.racketDirection = 0;


    }

    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    public void redrawSprites(){
        if (this.holder.getSurface().isValid()){
            this.canvas = this.holder.lockCanvas();

            canvas.drawColor(Color.argb(255,0,0,0));

            paintBrush.setColor(Color.argb(255, 255, 0, 0));

            // Draw apple
            canvas.drawRect(gpXPosition * (blockSize) ,
                    (gpYPosition * blockSize),
                    (gpXPosition * blockSize) + (blockSize),
                    (gpYPosition * blockSize) + blockSize,
                    paintBrush);

            // Draw apple

            paintBrush.setColor(Color.argb(255, 2, 100, 255));
            canvas.drawRect(racketXPosition * blockSize,
                    (racketYPosition * blockSize),
                    (racketXPosition * blockSize) + (4*blockSize),
                    (racketYPosition * blockSize) + blockSize,
                    paintBrush);


            paintBrush.setColor(Color.argb(255,255,255,255));

            for(int i = 0; i< snakeLength; i++) {
                canvas.drawRect(snakeXPositions * blockSize, snakeYPositions * blockSize, (snakeXPositions * blockSize) + blockSize, (snakeYPositions * blockSize) + blockSize, paintBrush);


            }



            paintBrush.setTextSize(50);
            paintBrush.setColor(Color.argb(255, 0, 255, 0));
            this.canvas.drawText("Score: " + score, 50, 300, paintBrush);


            // top
            this.canvas.drawLine(this.minX*blockSize, this.minY*blockSize, this.maxX*blockSize, this.minY*blockSize, paintBrush );
            // bottom
            //this.canvas.drawLine(this.minX*blockSize, this.maxY*blockSize, this.maxX*blockSize, this.maxY*blockSize, paintBrush );
            // left wall
            this.canvas.drawLine(this.minX*blockSize,this.minY*blockSize,this.minX*blockSize,this.maxY*blockSize,paintBrush);

            // right wall
            this.canvas.drawLine(this.maxX*blockSize,this.minY*blockSize, this.maxX*blockSize,this.maxY*blockSize,paintBrush);
            this.holder.unlockCanvasAndPost(canvas);
        }


    }

    public void updatePositions() {

        if(snakeXPositions <= minX || snakeXPositions >= maxX || snakeYPositions <= minY )
        {
            paintBrush.setTextSize(100);
            paintBrush.setColor(Color.argb(255, 0, 255, 0));

            if(score>=5) {
                this.canvas.drawText("YOU WIN!!! ", 20, 20, paintBrush);
            }
            else if(score <5)
            {
                this.canvas.drawText("YOU LOOse!!! ", 20, 20, paintBrush);
            }
            score =0;

        }

        if(racketXPosition == snakeXPositions && racketYPosition == snakeYPositions){
            snakeDirection = 1;
            racketDirection =0;
        }
         if ( (snakeXPositions == gpXPosition)
                && (snakeYPositions == gpYPosition)) {
            snakeDirection = 2;
            score++;

        }
        else if((snakeXPositions == racketXPosition)
                && (snakeYPositions == racketYPosition)){
             snakeDirection = 0;
        }
        if (snakeDirection == 0) {
            //up
            snakeYPositions = snakeYPositions - 1;
        }
        else if (snakeDirection == 1) {
            // right
            snakeXPositions = snakeXPositions + 1;
        }
        else if (snakeDirection == 2) {
            // down
            snakeYPositions = snakeYPositions + 1;
        }
        else if (snakeDirection == 3) {
            // left
            snakeXPositions = snakeXPositions - 1;
        }

        if (gpDirection == 1) {
            //up
            gpXPosition = gpXPosition + 1;
        }

        else if (gpDirection == 3) {
            // right
            gpXPosition = gpXPosition - 1;
        }



    }

    public void setFPS(){
        try{
            gameThread.sleep(50);
        }
        catch (Exception e){

        }
    }

    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_UP){
            if(this.gpDirection == 1){
                this.gpDirection = 3;
            }
            else this.gpDirection =1;

        }
        return true;
    }
}
