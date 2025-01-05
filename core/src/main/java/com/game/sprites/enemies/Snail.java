package com.game.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.game.Platformer;
import com.game.screens.PlayScreen;
import com.game.sprites.Player;

public class Snail extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;

    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}

    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> snailWalk;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private boolean destroyed;
    private boolean runningRight;

    public Snail(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 10; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("snail"), i * 38, 0, 38, 24));
        }
        snailWalk = new Animation<>(0.1f, frames);
        shell = new TextureRegion(screen.getAtlas().findRegion("snail"), 380, 0, 38, 24);
        currentState = previousState = State.WALKING;
        stateTime = 0;
        setBounds(getX(), getY(), 19 / Platformer.PPM, 12 / Platformer.PPM);
        destroyed = false;
        runningRight = false;
    }

    public TextureRegion getFrame(float delta) {
        TextureRegion region = switch (currentState) {
            case STANDING_SHELL, MOVING_SHELL -> shell;
            default -> snailWalk.getKeyFrame(stateTime, true);
        };

        //posibil de scos runningRight
        // if snail is moving left and is facing right
        if ((velocity.x < 0 || !runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        // if snail is moving right and is facing left
        else if ((velocity.x > 0 || runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTime = currentState == previousState ? stateTime + delta : 0;
        previousState = currentState;
        return region;
    }

    public void update(float delta) {
        setRegion(getFrame(delta));
        if (currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 0.15f;
        }
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - 8 / Platformer.PPM);

        if (currentState == State.DEAD) {
            if (stateTime > 5 && !destroyed) {
                world.destroyBody(body);
                destroyed = true;
            }
        } else {
            body.setLinearVelocity(velocity);
        }
    }

    // if snail gets hit on head, it transforms to shell
    @Override
    public void hitOnHead(Player player) {
        // Only allow state change if we're walking
        if (currentState == State.WALKING) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
            // Add small upward impulse to player with direction consideration
            float bounceForce = 4f;
            // If snail is moving right, add a small leftward force to the player
            if (runningRight) {
                player.body.setLinearVelocity(-0.5f, bounceForce);
            } else {
                player.body.setLinearVelocity(0.5f, bounceForce);
            }
        }
        // Only allow kick if we're explicitly in STANDING_SHELL state
        else if (currentState == State.STANDING_SHELL) {
            kick(player.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public void onEnemyHit(Enemy enemy) {
        // when snail hits another snail
        if (enemy instanceof Snail) {
            if (((Snail) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                killed();
            } else if (currentState == State.MOVING_SHELL && ((Snail) enemy).currentState == State.WALKING) {
                return;
            } else {
                reverseVelocity(true, false);
            }
        } else if (currentState != State.MOVING_SHELL) {
            reverseVelocity(true, false);
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    //what happens when a snail dies
    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = Platformer.Nothing_BIT;

        for (Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        body.applyLinearImpulse(new Vector2(1f, 4f), body.getWorldCenter(), true);
    }
}


