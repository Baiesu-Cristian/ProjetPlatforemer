package com.game.sprites.items;

import com.badlogic.gdx.maps.MapObject;
import com.game.Platformer;
import com.game.screens.PlayScreen;

public class Coin extends Interactive{
    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Platformer.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        // if player's head collides with the coin, it gets collected
        setCategoryFilter(Platformer.DESTROYED_BIT);
        getCell().setTile(null);
    }
}
