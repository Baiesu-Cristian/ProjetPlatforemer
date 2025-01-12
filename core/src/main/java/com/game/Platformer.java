package com.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.screens.PlayScreen;

public class Platformer extends Game {
    // taille d'écran virtuelle et échelle box2d
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 200;
    public static final float PPM = 100;

    // valeurs par défaut pour les filtres
    public static final short Nothing_BIT = 0;
    public static final short Ground_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short BOX_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short ENEMY_BIT = 32;
    public static final short WALL_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
