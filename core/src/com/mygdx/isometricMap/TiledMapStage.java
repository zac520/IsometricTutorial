package com.mygdx.isometricMap;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.isometricMap.screens.Play;

/**
 * Created by zac520 on 10/1/14.
 */
public class TiledMapStage extends Stage {

    private TiledMap tiledMap;
    public TiledMapStage(TiledMap tiledMap) {
        this.tiledMap = tiledMap;

        for (MapLayer layer : tiledMap.getLayers()) {
            TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
            createActorsForLayer(tiledLayer);
        }
    }

    private void createActorsForLayer(TiledMapTileLayer tiledLayer) {
        for (int x = 0; x < tiledLayer.getWidth(); x++) {
            for (int y = 0; y < tiledLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                if(cell == null){
                    continue;
                }


                TiledMapActor actor = new TiledMapActor(tiledMap, tiledLayer, cell);
                actor.setBounds(x * tiledLayer.getTileWidth(), y * tiledLayer.getTileHeight(), tiledLayer.getTileWidth(),
                        tiledLayer.getTileHeight());
                actor.setDrawable(new TextureRegionDrawable(cell.getTile().getTextureRegion()));

                //set the space to available or not based on the layer name
                if(tiledLayer.getName().compareTo("Unplaceable")==0){
//                    actor.setUserObject("Can't place here!");
                    actor.setUserObject(false);
                }
                else {
//                    actor.setUserObject("Good to place here though.");
                    actor.setUserObject(true);
                }

                addActor(actor);
                EventListener eventListener = new TiledMapClickListener(actor);
                actor.addListener(eventListener);
            }
        }
    }
}
