package com.game;

import com.badlogic.gdx.maps.MapObject;

public class Box extends Interactive{
    public Box(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Platformer.BOX_BIT);
    }

    @Override
    public void onHeadHit() {
        // if player's head collides with the box, it gets destroyed
        setCategoryFilter(Platformer.DESTROYED_BIT);
        getCell().setTile(null);
    }
}
