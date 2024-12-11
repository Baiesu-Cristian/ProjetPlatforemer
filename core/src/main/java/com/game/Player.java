package com.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite {
    public enum State {STANDING, RUNNING, JUMPING, FALLING, DEAD}
    public State currentState;
    public State previousState;
    public World world;
    public Body body;
    private TextureRegion playerStand;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerJump;
    private Animation<TextureRegion> playerFall;
    private Animation<TextureRegion> playerDead;
    private float stateTimer;
    private boolean runningRight;
    private boolean playerIsDead;

    public Player(PlayScreen screen) {
        super(screen.getAtlas().findRegion("ninja"));
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = false;

        Array<TextureRegion> frames = new Array<>();
        // running animation
        for (int i = 0; i < 12; i++) {
            frames.add(new TextureRegion(getTexture(), i*32, 0, 32, 32));
        }
        playerRun = new Animation<>(0.1f, frames);
        frames.clear();

        // jumping animation
        frames.add(new TextureRegion(getTexture(), 41 * 32, 0, 32, 32));
        playerJump = new Animation<>(0.1f, frames);
        frames.clear();

        // falling animation
        frames.add(new TextureRegion(getTexture(), 42 * 32, 0, 32, 32));
        playerFall = new Animation<>(0.1f, frames);
        frames.clear();

        // standing frame
        playerStand = new TextureRegion(getTexture(), 0, 0, 32, 32);

        // getting hit animation
        for (int i = 23; i < 30; i++) {
            frames.add(new TextureRegion(getTexture(), i*32, 0, 32, 32));
        }
        playerDead = new Animation<>(0.1f, frames);
        frames.clear();

        definePlayer();
        setBounds(0, 0, 16 / Platformer.PPM, 16 / Platformer.PPM);
        setRegion(playerStand);
    }

    public void update(float delta) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();
        TextureRegion region = switch (currentState) {
            case JUMPING -> playerJump.getKeyFrame(stateTimer);
            case RUNNING -> playerRun.getKeyFrame(stateTimer, true);
            case FALLING -> playerFall.getKeyFrame(stateTimer);
            case DEAD -> playerDead.getKeyFrame(stateTimer);
            default -> playerStand;
        };

        // if player is running left and is facing right
        if ((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        // if player is running right and is facing left
        else if ((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (playerIsDead) {
            return State.DEAD;
        }
        else if (body.getLinearVelocity().y > 0) {
            return State.JUMPING;
        }
        else if (body.getLinearVelocity().y < 0) {
            return State.FALLING;
        }
        else if (body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        }
        else {
            return State.STANDING;
        }
    }

    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(100 / Platformer.PPM, 100 / Platformer.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Platformer.PPM);

        // set the player's filter
        fdef.filter.categoryBits = Platformer.PLAYER_BIT;
        // what the player can collide with
        fdef.filter.maskBits = Platformer.Ground_BIT | Platformer.BOX_BIT | Platformer.COIN_BIT | Platformer.WALL_BIT | Platformer.ENEMY_BIT | Platformer.ENEMY_HEAD_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/Platformer.PPM, 6/Platformer.PPM), new Vector2(2/Platformer.PPM, 6/Platformer.PPM));
        fdef.shape = head;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("head");
    }

    public void hit() {
        playerIsDead = true;
        Filter filter = new Filter();
        filter.maskBits = Platformer.Nothing_BIT;
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        body.applyLinearImpulse(new Vector2(1f, 4f), body.getWorldCenter(), true);
    }
}
