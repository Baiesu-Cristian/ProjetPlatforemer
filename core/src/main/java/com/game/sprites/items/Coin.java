package com.game.sprites.items;

import com.badlogic.gdx.maps.MapObject;
import com.game.Platformer;
import com.game.screens.PlayScreen;

public class Coin extends Interactive{
    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Platformer.COIN_BIT);

        // permets de passer à travers les monnaies
        fixture.setSensor(true);
    }
}
