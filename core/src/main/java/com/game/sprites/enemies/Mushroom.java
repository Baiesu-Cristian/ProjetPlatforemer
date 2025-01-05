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
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private boolean runningRight;

    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();

        // walking animation
        for (int i = 0; i < 16; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("mushroom"), i * 32, 0, 32, 32));
        }
        mushroomWalk = new Animation<>(0.1f, frames);
        frames.clear();

        // getting hit animation
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
        // destroy the body
        if (setToDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
            setRegion(mushroomDead.getKeyFrame(stateTime));
            stateTime = 0;
        } else if (!destroyed) {
            // if mushroom is moving left and facing right
            if ((body.getLinearVelocity().x < 0 || !runningRight) && region.isFlipX()) {
                region.flip(true, false);
                runningRight = false;
            }
            // if mushroom is moving right and is facing left
            else if ((body.getLinearVelocity().x > 0 || runningRight) && !region.isFlipX()) {
                region.flip(true, false);
                runningRight = true;
            }
            body.setLinearVelocity(velocity);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRegion(region);
        }
    }

    // the mushroom disappears after 2 seconds
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 2) {
            super.draw(batch);
        }
    }

    // if snail gets hit on head, it gets destroyed
    @Override
    public void hitOnHead(Player player) {
        setToDestroy = true;
    }

    public void onEnemyHit(Enemy enemy) {
        // if it gets hit by a moving shell, it gets destroyed
        if (enemy instanceof Snail && ((Snail) enemy).currentState == Snail.State.MOVING_SHELL) {
            setToDestroy = true;
        } else {
            reverseVelocity(true, false);
        }
    }
}
