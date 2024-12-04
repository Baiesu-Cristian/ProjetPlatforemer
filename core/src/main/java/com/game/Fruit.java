package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Fruit extends Interactive{
    public Fruit(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        fixture.setUserData(this);
        setCategoryFilter(Platformer.FRUIT_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("fruit", "collision");
    }
}
