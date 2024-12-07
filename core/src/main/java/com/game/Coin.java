package com.game;

import com.badlogic.gdx.math.Rectangle;

public class Coin extends Interactive{
    public Coin(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
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
