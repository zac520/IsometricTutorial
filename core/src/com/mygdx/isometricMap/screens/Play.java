package com.mygdx.isometricMap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricStaggeredTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by zac520 on 9/30/14.
 */
public class Play implements Screen {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private OrthographicCamera camera;

    private Stage stage;

    private TextureAtlas atlas;

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
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //slow down the camera if we are flinging
        if (flinging) {
          flingCamera();
        }

        //render the map
        renderer.setView(camera);
        renderer.render();

        //render the stage
        stage.act();
        stage.draw();

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
        camera.position.set(200,100,0);

        //load the map
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("assets/Tilemap/Test2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        //load the stage
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        Gdx.input.setInputProcessor(new GestureDetector(new MyGestureListener()));


        //get the atlas set up
        atlas = new TextureAtlas(Gdx.files.internal("assets/Atlas/FirstLevel.txt"));

        //pull a random image from the atlas for now and add to stage
        Image enemy = new Image(new TextureRegion(atlas.findRegion("Enemy1_Left1")));
        stage.addActor(enemy);

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
        Vector3 newPosition;
        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            initialZoomTouchdown = true;

            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {

//            TiledMapTileLayer.Cell cell = layer.getCell(col, row);


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


            //get the new position
            newPosition = new Vector3(
                    camera.position.x - (deltaX* currentZoomLevelX),
                    camera.position.y + (deltaY* currentZoomLevelY),
                    0

            );

            camera.position.set(newPosition);
            pushCameraBackIntoLimits();
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            //System.out.println("stopping panning");

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
}
