package com.mygdx.isometricMap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by zac520 on 10/7/14.
 */
public class UserInterface extends Stage {
    //Skin skin;
    Play game;
    public Group graphicsGroup;
    public Image opener;
    public boolean scrollStuffShowing = false;
    public Table table;
    public ScrollPane scrollPane;
    public UserInterface(Play myGameScreen){

        game=myGameScreen;

//        skin = new Skin();//new skin for us to set up
//        skin.addRegions(game.atlas);

        graphicsGroup = new Group();

        opener = new Image(new TextureRegion(game.atlas.findRegion("RedTransparent")));
        opener.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!scrollStuffShowing) {
                    addScrollStuff();
                    opener.setX(opener.getX() + scrollPane.getWidth());
                    scrollStuffShowing = true;
                }
                else{
                    table.remove();
                    opener.setX(0);
                    scrollStuffShowing = false;
                }
                return true;
            }
        });

        graphicsGroup.addActor(opener);



        //addScrollStuff();

        //add the graphics group to the stage
        this.addActor(graphicsGroup);

    }

    private void addScrollStuff(){

        String reallyLongString = "This\nIs\nA\nReally\nLong\nString\nThat\nHas\nLots\nOf\nLines\nAnd\nRepeats.\n"
                + "This\nIs\nA\nReally\nLong\nString\nThat\nHas\nLots\nOf\nLines\nAnd\nRepeats.\n"
                + "This\nIs\nA\nReally\nLong\nString\nThat\nHas\nLots\nOf\nLines\nAnd\nRepeats.\n";

        final Skin skin = new Skin(Gdx.files.internal("assets/ui/defaultskin.json"));

        final Label text = new Label(reallyLongString, skin);
        text.setAlignment(Align.center);
        text.setWrap(true);
        final Label text2 = new Label("This is a short string!", skin);
        text2.setAlignment(Align.center);
        text2.setWrap(true);
        final Label text3 = new Label(reallyLongString, skin);
        text3.setAlignment(Align.center);
        text3.setWrap(true);

        final Table scrollTable = new Table();
        scrollTable.add(text);
        scrollTable.row();
        scrollTable.add(text2);
        scrollTable.row();
        scrollTable.add(new Image(game.atlas.findRegion("Chestopen1")));
        scrollTable.row();
        scrollTable.add(text3);
        scrollTable.row();
        scrollTable.add(new Image(game.atlas.findRegion("Chestopen1")));

        scrollPane = new ScrollPane(scrollTable);

        table = new Table();
        table.setFillParent(true);
        table.add(scrollPane).fill().expand();

        //for now, we are manually setting the location oddly like this
        table.setPosition(table.getX() -300, table.getY()-100);

//        graphicsGroup.addActor(table);

        addActor(table);
        table.toBack();
    }
}
