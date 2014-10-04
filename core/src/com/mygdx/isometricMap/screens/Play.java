package com.mygdx.isometricMap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.isometricMap.TiledMapStage;
import com.mygdx.isometricMap.WorldAddition;

/**
 * Created by zac520 on 9/30/14.
 */
public class Play implements Screen {

    public static final int BLOCK_SIZE = 32;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private OrthographicCamera camera;

    private TiledMapStage stage;

    public TextureAtlas atlas;

    private Image mapImage;

    private int WINDOW_WIDTH = 240;
    private int WINDOW_HEIGHT = 180;

    private float originalZoomLevelX = 1;
    private float originalZoomLevelY = 1;

    private float currentZoomLevelX = 1;
    private float currentZoomLevelY = 1;

    public boolean flinging = false;
    public float velX;
    public float velY;

    public float x_left_limit;
    public float x_right_limit;
    public float y_bottom_limit;
    public float y_top_limit;

    public boolean initialZoomTouchdown = true;
    private float currentCameraZoom = 1;

    private boolean movingBlockBeyondBorders = false;
    private WorldAddition additionSelected;

    public boolean blockAvailability [][];

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //slow down the camera if we are flinging
        if (flinging) {
          flingCamera();
        }



        if(additionSelected!=null){
            //move the block and scroll screen at the same time
            if(movingBlockBeyondBorders){
                controlItemMovementBeyondBorders();
            }

            //check and see if the box is placeable at that location
            try {
                Vector2 myVector = new Vector2(
                        (int)additionSelected.getGroup().getX(),
                        (int)additionSelected.getGroup().getY());

                //if we are holding "true" in the actor we hit, then it is placeable
               if((Boolean)stage.hit(myVector.x,myVector.y, false).getUserObject()==true){
                   //System.out.println("totally placeable");
                   additionSelected.getGroup().getChildren().get(0).setColor(Color.GREEN);

               }
               else{
                   //System.out.println("not at all placeable");
//                   myVector = new Vector2( myVector.x, -myVector.y);
//
//                   System.out.println("x: " + (int)additionSelected.getGroup().stageToLocalCoordinates(myVector).x +
//                           " y: " + (int)additionSelected.getGroup().stageToLocalCoordinates(myVector).y);
//                   additionSelected.getGroup().hit(
//                           (int)additionSelected.getGroup().stageToLocalCoordinates(myVector).x,
//                           (int)additionSelected.getGroup().stageToLocalCoordinates(myVector).y ,
//                           false).setColor(Color.RED);
                   System.out.println(additionSelected.getGroup().getChildren().get(0).getX());
                   System.out.println(additionSelected.getGroup().getChildren().get(0).getY());


               }

            }
            catch (Exception e){

            }

        }

        //render the stage
        stage.act();
        stage.draw();

    }

    private void controlItemMovementBeyondBorders(){
        //move the block
        additionSelected.getGroup().setPosition(
                additionSelected.getGroup().getX() +  ((additionSelected.getGroup().getX() - camera.position.x) /50),
                additionSelected.getGroup().getY()+((additionSelected.getGroup().getY()  - camera.position.y)/50));

        camera.position.set(
                camera.position.x + ((additionSelected.getGroup().getX() - camera.position.x) /50),
                camera.position.y +((additionSelected.getGroup().getY()  - camera.position.y)/50) ,
                0
        );



        pushCameraBackIntoLimits();
        pushItemBackIntoLimits();

    }
    private void flingCamera(){
        velX *= 0.95f;
        velY *= 0.95f;

        //update camera to new spot
        camera.position.set(
                camera.position.x -velX * Gdx.graphics.getDeltaTime(),
                camera.position.y + velY * Gdx.graphics.getDeltaTime(),
                0);
        //push back into limits
        pushCameraBackIntoLimits();

        //slow down,and stop flinging if too slow
        if (Math.abs(velX) < 0.25f) velX = 0;//if velocities are below a threshold, then set to zero
        if (Math.abs(velY) < 0.25f) velY = 0;
        if ((velX == 0) && (velY == 0)) {//if both velocities are zero, stop running this flinging
            flinging = false;
        }

    }

    @Override
    public void resize(int width, int height) {
//        camera.viewportWidth = width;
//        camera.viewportHeight = height;
//        camera.update();

        //get the zoom level to use for scrolling no matter how zoomed in a user is
        originalZoomLevelX = camera.viewportWidth/width;
        originalZoomLevelY = camera.viewportHeight/height;
        currentZoomLevelX = originalZoomLevelX;
        currentZoomLevelY = originalZoomLevelY;
    }

    @Override
    public void show() {



        //set the camera up
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        camera.position.set(200,200,0);

        //load the map
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("assets/Tilemap/Test2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        //load the stage
        //stage = new Stage();
        stage = new TiledMapStage(map);
        stage.getViewport().setCamera(camera);
        //need a multiplexor so that the user can touch the level, or the user interface
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GestureDetector(new MyGestureListener()));
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);


        //get the atlas set up
        atlas = new TextureAtlas(Gdx.files.internal("assets/Atlas/FirstLevel.txt"));

        //pull a random image from the atlas for now and add to stage
        WorldAddition enemy = new WorldAddition(this, new TextureRegion(atlas.findRegion("crate1")),BLOCK_SIZE*2,BLOCK_SIZE,6*BLOCK_SIZE,6*BLOCK_SIZE);
        stage.addActor(enemy.getGroup());

        //set the limits of the camera
        setStageLimits();



    }

    private void setStageLimits(){

        MapProperties prop = map.getProperties();
        float mapWidth = prop.get("width", Integer.class);//gives the width in blocks
        mapWidth = mapWidth * prop.get("tilewidth", Integer.class); //multiply by width of blocks
        float mapHeight = prop.get("height", Integer.class);//gives the height in blocks
        mapHeight = mapHeight * prop.get("tileheight", Integer.class); //multiply by height of blocks

         x_left_limit = (camera.viewportWidth *camera.zoom)/2;
         x_right_limit = mapWidth - (camera.viewportWidth*camera.zoom) / 2;
         y_bottom_limit = (camera.viewportHeight*camera.zoom) / 2;
         y_top_limit = (mapHeight - (camera.viewportHeight*camera.zoom) /2);
    }
    private boolean isInStageLimits(Vector3 newPosition){

        //if out of bounds in x or y, return false. else it's good.
        if(newPosition.x < x_left_limit ||newPosition.x > x_right_limit){
            return false;
        }
        if (newPosition.y < y_bottom_limit || newPosition.y > y_top_limit){
            return false;
        }
        else{
            return true;
        }



    }

    @Override
    public void hide() {
        dispose();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }


    class MyGestureListener implements GestureDetector.GestureListener {
       //gesture listener x and y values are local to the screen. ie, in the middle of the world, still
        //the x value will be 0 on the left, and the screen width value on the right.
        Vector3 newPosition;

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            initialZoomTouchdown = true;
            flinging = false;
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {

//            TiledMapTileLayer.Cell cell = layer.getCell(col, row);
            //System.out.println("x: " + x);


            return false;
        }

        @Override
        public boolean longPress(float x, float y) {

            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            //System.out.println("flinging");
            flinging = true;
            velX = currentZoomLevelX * velocityX;
            velY = currentZoomLevelY * velocityY;
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {


            //if we aren't moving something, then pan the camera
            if(additionSelected==null) {
                //get the new position
                newPosition = new Vector3(
                        camera.position.x - (deltaX * currentZoomLevelX),
                        camera.position.y + (deltaY * currentZoomLevelY),
                        0

                );

                camera.position.set(newPosition);
                pushCameraBackIntoLimits();
            }

            //if we are panning something, then move the something
            else{

                if(panItemBeyondBorder()){

                    //with no motion, this block does not run. By setting this, we are going to
                    //use the render loop to continue our moving while we are still
                    movingBlockBeyondBorders = true;

                    //also moving here, for when the user wants to stop moving by moving the item back
                    additionSelected.getGroup().setPosition(
                            additionSelected.getGroup().getX() + (deltaX * currentZoomLevelX),
                            additionSelected.getGroup().getY() - (deltaY * currentZoomLevelY));
                }
                else {
                    //no map panning needed, just move the item around
                    movingBlockBeyondBorders = false;
                    additionSelected.getGroup().setPosition(
                            additionSelected.getGroup().getX() + (deltaX * currentZoomLevelX),
                            additionSelected.getGroup().getY() - (deltaY * currentZoomLevelY));
                }
                pushItemBackIntoLimits();
            }

            return false;
        }
        private boolean panItemBeyondBorder(){
            if(additionSelected.getGroup().getX() + additionSelected.getWidth() >10+ camera.position.x + (camera.viewportWidth *currentCameraZoom)/2){
                return true;
            }
            if(additionSelected.getGroup().getX()  < -10 + camera.position.x - (camera.viewportWidth *currentCameraZoom)/2){
                return true;
            }
            if(additionSelected.getGroup().getY()  < -10 + camera.position.y - (camera.viewportHeight *currentCameraZoom)/2){
                return true;
            }
            if(additionSelected.getGroup().getY() + additionSelected.getHeight()  > camera.position.y + (camera.viewportHeight *currentCameraZoom)/2){
                return true;
            }
            else{
                return false;
            }
        }
        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            System.out.println("stopping panning");
            movingBlockBeyondBorders = false;
            return false;
        }

        @Override
        public boolean zoom (float originalDistance, float currentDistance){

            if(initialZoomTouchdown){
                currentCameraZoom = camera.zoom;
                initialZoomTouchdown = false;
            }

            float ratio = originalDistance / currentDistance;
            if((currentCameraZoom * ratio <2)&&(currentCameraZoom*ratio>0.2)) {
                camera.zoom = currentCameraZoom * ratio;
                currentZoomLevelX = originalZoomLevelX * camera.zoom;
                currentZoomLevelY = originalZoomLevelY * camera.zoom;

                //reset the window limits based on the new zoom
                setStageLimits();

                //if we have zoomed beyond the stage limits, move back in
                pushCameraBackIntoLimits();

            }

            return false;
        }

        @Override
        public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){
            System.out.println("pinching");

            return false;
        }
    }

    private void pushCameraBackIntoLimits(){
        if(camera.position.x < x_left_limit){
            camera.position.x = x_left_limit;
        }
        else if(camera.position.x > x_right_limit){
            camera.position.x = x_right_limit;
        }
        if(camera.position.y < y_bottom_limit){
            camera.position.y = y_bottom_limit;
        }
        else if(camera.position.y > y_top_limit){
            camera.position.y = y_top_limit;
        }
    }
    private void pushItemBackIntoLimits(){



        if(additionSelected.getGroup().getX() < x_left_limit - (camera.viewportWidth *camera.zoom)/2){
            additionSelected.getGroup().setX(x_left_limit - (camera.viewportWidth *camera.zoom)/2);
        }
        else if(additionSelected.getGroup().getX() + additionSelected.getWidth() > x_right_limit +(camera.viewportWidth*camera.zoom) / 2){
            additionSelected.getGroup().setX(x_right_limit +((camera.viewportWidth*camera.zoom) / 2) - additionSelected.getWidth());
        }
        if(additionSelected.getGroup().getY() < y_bottom_limit-(camera.viewportHeight*camera.zoom) / 2){
            additionSelected.getGroup().setY(y_bottom_limit-(camera.viewportHeight*camera.zoom) / 2);
        }
        else if(additionSelected.getGroup().getY() + additionSelected.getHeight() > y_top_limit+(camera.viewportHeight*camera.zoom) /2){
            additionSelected.getGroup().setY(y_top_limit+((camera.viewportHeight*camera.zoom) /2)- additionSelected.getHeight()) ;
        }
    }
    public void selectEnemy(WorldAddition myAddition){

        if(additionSelected !=null) {
            if (additionSelected == myAddition) {//if the body is already selected, then we unselect.
                additionSelected.toggleSelected();
                additionSelected = null;
                return;
            } else {
                //let the old body know we are unselecting it
                additionSelected.toggleSelected();

                //set it to the new enemy
                additionSelected = myAddition;

                //toggle the new enemy
                additionSelected.toggleSelected();
            }

        }
        else{
            additionSelected = myAddition;
            additionSelected.toggleSelected();
        }
    }
}
