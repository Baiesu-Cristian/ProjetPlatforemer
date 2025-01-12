package com.game.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.game.Platformer;
import com.game.screens.PlayScreen;
import com.game.sprites.Player;

public class Mushroom extends Enemy{
    private float stateTime;
    private Animation<TextureRegion> mushroomWalk;
    private Animation<TextureRegion> mushroomDead;
    private boolean setToDestroy;
    private boolean destroyed;
    private boolean runningRight;

    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<>();

        // animation pour marcher
        for (int i = 0; i < 16; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mushroom"), i * 32, 0, 32, 32));
        }
        mushroomWalk = new Animation<>(0.1f, frames);
        frames.clear();

        // animation se faire toucher
        for (int i = 16; i < 21; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mushroom"), i * 32, 0, 32, 32));
        }
        mushroomDead = new Animation<>(0.1f, frames);
        frames.clear();

        stateTime = 0;
        setBounds(getX(), getY(), 15 / Platformer.PPM, 15 / Platformer.PPM);
        setToDestroy = false;
        destroyed = false;
        runningRight = false;
    }

    public void update(float delta) {
        TextureRegion region = mushroomWalk.getKeyFrame(stateTime, true);
        stateTime += delta;
        // détruire l'objet
        if (setToDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
            setRegion(mushroomDead.getKeyFrame(stateTime));
            stateTime = 0;
        } else if (!destroyed) {
            // si le champignon marche à gauche et regarde à droite
            if ((body.getLinearVelocity().x < 0 || !runningRight) && region.isFlipX()) {
                region.flip(true, false);
                runningRight = false;
            }
            // si le champignon marche à droite et regarde à gauche
            else if ((body.getLinearVelocity().x > 0 || runningRight) && !region.isFlipX()) {
                region.flip(true, false);
                runningRight = true;
            }
            body.setLinearVelocity(velocity);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRegion(region);
        }
    }

    // le champignon disparait après 2 secondes
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 2) {
            super.draw(batch);
        }
    }

    // si le champignon est frappé à la tête, il est détruit
    @Override
    public void hitOnHead(Player player) {
        setToDestroy = true;
    }

    public void onEnemyHit(Enemy enemy) {
        // s'il est touché par une coquille en mouvement, il est détruit
        if (enemy instanceof Snail && ((Snail) enemy).currentState == Snail.State.MOVING_SHELL) {
            setToDestroy = true;
        } else {
            reverseVelocity(true, false);
        }
    }
}
