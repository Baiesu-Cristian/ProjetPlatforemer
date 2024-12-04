package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Box extends Interactive{
    public Box(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        fixture.setUserData(this);
        setCategoryFilter(Platformer.BOX_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("box", "collision");
        // if player's head collides with the box, it gets destroyed
        setCategoryFilter(Platformer.DESTROYED_BIT);
        getCell().setTile(null);
    }
}
