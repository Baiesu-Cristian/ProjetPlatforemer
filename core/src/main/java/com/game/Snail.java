package com.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

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
    private boolean setToDestroy;
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
        setToDestroy = false;
        destroyed = false;
        runningRight = false;
    }

    public TextureRegion getFrame(float delta) {
        TextureRegion region = switch (currentState) {
            case STANDING_SHELL, MOVING_SHELL -> shell;
            default -> snailWalk.getKeyFrame(stateTime, true);
        };

        //posibil de scos runningRight
        if ((velocity.x < 0 || !runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        // if player is running right and is facing left
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
            velocity.x = 1;
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
        /*TextureRegion region = snailWalk.getKeyFrame(stateTime, true);
        stateTime += delta;
        // destroy the body
        if (setToDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("snail"), 380, 0, 38, 24));
            stateTime = 0;
        } else if (!destroyed) {
            if ((body.getLinearVelocity().x < 0 || !runningRight) && region.isFlipX()) {
                region.flip(true, false);
                runningRight = false;
            }
            // if player is running right and is facing left
            else if ((body.getLinearVelocity().x > 0 || runningRight) && !region.isFlipX()) {
                region.flip(true, false);
                runningRight = true;
            }
            body.setLinearVelocity(velocity);
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRegion(region);*/
    }

    // la fel ca player, posibil de schimbat
    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / Platformer.PPM);

        // set the snail's filter
        fdef.filter.categoryBits = Platformer.ENEMY_BIT;
        // what the snail can collide with
        fdef.filter.maskBits = Platformer.Ground_BIT | Platformer.BOX_BIT | Platformer.COIN_BIT | Platformer.PLAYER_BIT | Platformer.WALL_BIT | Platformer.ENEMY_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

        //create snail's head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5, 6).scl(1 / Platformer.PPM);
        vertices[1] = new Vector2(5, 6).scl(1 / Platformer.PPM);
        vertices[2] = new Vector2(-3, 3).scl(1 / Platformer.PPM);
        vertices[3] = new Vector2(3, 3).scl(1 / Platformer.PPM);
        head.set(vertices);

        fdef.shape = head;
        fdef.filter.categoryBits = Platformer.ENEMY_HEAD_BIT;
        //small bounce when player hits snail's head
        fdef.restitution = 0.5f;
        body.createFixture(fdef).setUserData(this);
    }

    // the shell disappears after 2 seconds
    /*public void draw(Batch batch) {
        if (!destroyed || stateTime < 2) {
            super.draw(batch);
        }
    }*/

    // if snail gets hit on head, it gets destroyed
    @Override
    public void hitOnHead(Player player) {
        if (currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(player.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
        //setToDestroy = true;
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public void onEnemyHit(Enemy enemy) {
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


