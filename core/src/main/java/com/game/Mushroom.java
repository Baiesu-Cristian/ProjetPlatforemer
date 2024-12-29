package com.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

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
            setRegion(region);
        }
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

        // set the mushroom's filter
        fdef.filter.categoryBits = Platformer.ENEMY_BIT;
        // what the snail can collide with
        fdef.filter.maskBits = Platformer.Ground_BIT | Platformer.BOX_BIT | Platformer.COIN_BIT | Platformer.PLAYER_BIT | Platformer.WALL_BIT | Platformer.ENEMY_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

        //create mushroom's head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-6, 6).scl(1 / Platformer.PPM);
        vertices[1] = new Vector2(6, 6).scl(1 / Platformer.PPM);
        vertices[2] = new Vector2(-3, 4).scl(1 / Platformer.PPM);
        vertices[3] = new Vector2(3, 4).scl(1 / Platformer.PPM);
        head.set(vertices);

        fdef.shape = head;
        fdef.filter.categoryBits = Platformer.ENEMY_HEAD_BIT;
        //small bounce when player hits snail's head
        fdef.restitution = 0.5f;
        body.createFixture(fdef).setUserData(this);
    }

    // the shell disappears after 2 seconds
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
        if (enemy instanceof Snail && ((Snail) enemy).currentState == Snail.State.MOVING_SHELL) {
            setToDestroy = true;
        } else {
            reverseVelocity(true, false);
        }
    }
}
