package com.game.sprites.items;

import com.badlogic.gdx.maps.MapObject;
import com.game.Platformer;
import com.game.screens.PlayScreen;

public class Box extends Interactive{
    public Box(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Platformer.BOX_BIT);
    }
}
