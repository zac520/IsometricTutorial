package com.mygdx.isometricMap;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * http://stackoverflow.com/questions/24080272/libgdx-how-to-make-tiled-map-tiles-clickable
 */
public class TiledMapClickListener extends ClickListener {

    private TiledMapActor actor;

    public TiledMapClickListener(TiledMapActor actor) {
        this.actor = actor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        //System.out.println(actor.cell + " has been clicked.");
        System.out.println(actor.tiledLayer.getName());
    }
}
