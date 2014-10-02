package com.mygdx.isometricMap;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by zac520 on 10/1/14.
 */
public class TiledMapActor extends Actor {

    private TiledMap tiledMap;

    public TiledMapTileLayer tiledLayer;

    public TiledMapTileLayer.Cell cell;


    public TiledMapActor(TiledMap tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        this.cell = cell;
    }

}
