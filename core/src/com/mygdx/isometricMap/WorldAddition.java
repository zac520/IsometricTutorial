package com.mygdx.isometricMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.isometricMap.screens.Play;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * The purpose of this class is to form a group of actors that will become part of the stage
 */

public class WorldAddition extends Image {

    public boolean selected = false;
    public Group graphicsGroup;//used to link in the arrow later

    Image [][] transparentBoxImages;
    Play game;
    WorldAddition worldAddition;
    public WorldAddition(Play myPlay, TextureRegion textureRegion, int width, int height, int x, int y){
        game = myPlay;

        this.setDrawable(new TextureRegionDrawable(textureRegion));
        this.setSize(width, height);

        //this position will be at 0,0, and the group will take it from there
        this.setPosition(0, 0);

        graphicsGroup = new Group();
        //group will start at 0,0 i believe, unless we set it to coincide with the actor we are using
        graphicsGroup.setPosition(x,y);

        //now add the actor
        graphicsGroup.addActor(this);

        worldAddition = this;
        this.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                game.selectEnemy(worldAddition);
                return true;
            }
        });
    }

    public Group getGroup(){
        return graphicsGroup;
    }
    public void toggleSelected(){
//TODO need to make this so that there are as many boxes as the image covers. Going to bed now
        //toggle the selected enemy
        if(!selected) {
            this.graphicsGroup.toFront();
//            System.out.println("selecting");
            transparentBoxImages = new Image[(int)this.getWidth()/32][(int) this.getHeight()/32];
            //add the box to the proper position for each row this world addition covers
            for(int x = 0; x< this.getWidth() / game.BLOCK_SIZE; x++){
                for(int y = 0; y<this.getHeight()/game.BLOCK_SIZE;y++){
                    Image nextImage = new Image(new TextureRegion(game.atlas.findRegion("GreenTransparent")));
                    nextImage.setSize(game.BLOCK_SIZE, game.BLOCK_SIZE);
                    nextImage.setPosition(this.getX() + (x * game.BLOCK_SIZE), this.getY() + (y * game.BLOCK_SIZE));

                    graphicsGroup.addActorBefore(this, nextImage);
                    transparentBoxImages[x][y]= nextImage;

                }
            }

            //pick the item up
            this.setY(this.getY() + 10);
            selected = true;
        }
        else{

            //put the item back down
            this.setY(this.getY() - 10);

            for(int x = 0; x< this.getWidth() / game.BLOCK_SIZE; x++){
                for(int y = 0; y<this.getHeight()/game.BLOCK_SIZE;y++){
                    graphicsGroup.removeActor(transparentBoxImages[x][y]);

                }
            }


            selected = false;

            //if we can't place it there, then we will just run everything again
            if(!game.placeable){
                Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    game.selectEnemy(worldAddition);
                }
            }, 0.1f);
            }


        }
    }
}
