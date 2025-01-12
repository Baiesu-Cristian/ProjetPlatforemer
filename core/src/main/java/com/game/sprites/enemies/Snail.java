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
    private TextureRegion shell;
    private boolean destroyed;
    private boolean runningRight;

    public Snail(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<>();
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

        // si l'escargot marche à gauche et regarde à droite
        if ((velocity.x < 0 || !runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        // si l'escargot marche à droite et regarde à gauche
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

    // si l'escargot est frappé à la tête, il se transforme en coquille
    @Override
    public void hitOnHead(Player player) {
        if (currentState == State.WALKING) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
            if (runningRight) {
                player.body.setLinearVelocity(-0.5f, 4f);
            } else {
                player.body.setLinearVelocity(0.5f, 4f);
            }
        }
        else if (currentState == State.STANDING_SHELL) {
            kick(player.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public void onEnemyHit(Enemy enemy) {
        // quand un escargot touche un autre escargot
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

    // quand l'escargot meurt
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


