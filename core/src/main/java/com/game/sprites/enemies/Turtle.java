package com.game.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.game.Platformer;
import com.game.screens.PlayScreen;
import com.game.sprites.Player;

public class Turtle extends Enemy{
    public enum State {NO_SPIKES, SPIKES, DEAD}

    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> turtleNoSpikes;
    private Animation<TextureRegion> turtleSpikes;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y){
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<>();

        // animation non pointé
        for (int i = 0; i < 14; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), i * 44, 0, 44, 26));
        }
        turtleNoSpikes = new Animation<>(0.1f, frames);
        frames.clear();

        // animation pointé
        for (int i = 14; i < 28; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), i * 44, 0, 44, 26));
        }
        turtleSpikes = new Animation<>(0.1f, frames);
        frames.clear();

        currentState = previousState = State.NO_SPIKES;
        stateTime = 0;
        setBounds(getX(), getY(), 18 / Platformer.PPM, 15 / Platformer.PPM);
        destroyed = false;
    }

    @Override
    public void hitOnHead(Player player) {
        // si l'hérisson est frappé à la tête, il meurt
        if (currentState == State.NO_SPIKES) {
            killed();
        }
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        // si l'hérisson touche un autre ennemie, il meurt
        killed();
    }

    // quand l'hérisson meurt
    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = Platformer.Nothing_BIT;

        for (Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        body.applyLinearImpulse(new Vector2(1f, 4f), body.getWorldCenter(), true);
    }

    @Override
    public void update(float delta) {
        if (currentState == State.SPIKES) {
            setRegion(turtleSpikes.getKeyFrame(stateTime, true));
        } else {
            setRegion(turtleNoSpikes.getKeyFrame(stateTime, true));
        }
        stateTime = currentState == previousState ? stateTime + delta : 0;
        previousState = currentState;

        // changement des états chaque 3 secondes
        if (currentState == State.NO_SPIKES && stateTime > 3) {
            currentState = State.SPIKES;
            stateTime = 0;
        } else if (currentState == State.SPIKES && stateTime > 3) {
            currentState = State.NO_SPIKES;
            stateTime = 0;
        }
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - 8 / Platformer.PPM);

        if (currentState == State.DEAD) {
            if (stateTime > 3 && !destroyed) {
                world.destroyBody(body);
                destroyed = true;
            }
        }
    }
}

