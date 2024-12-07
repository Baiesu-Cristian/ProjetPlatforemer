package com.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class Snail extends Enemy{
    private float stateTime;
    private Animation<TextureRegion> snailWalk;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Snail(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 10; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("snail"), i * 38, 0, 38, 24));
        }
        snailWalk = new Animation<>(0.1f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 19 / Platformer.PPM, 12 / Platformer.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float delta) {
        stateTime += delta;
        if (setToDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("snail"), 380, 0, 38, 24));
        } else if (!destroyed) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRegion(snailWalk.getKeyFrame(stateTime, true));
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

        // set the player's filter
        fdef.filter.categoryBits = Platformer.ENEMY_BIT;
        // what the player can collide with
        fdef.filter.maskBits = Platformer.Ground_BIT | Platformer.BOX_BIT | Platformer.COIN_BIT | Platformer.PLAYER_BIT;

        fdef.shape = shape;
        body.createFixture(fdef);

        //create snail's head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-7, 6).scl(1 / Platformer.PPM);
        vertices[1] = new Vector2(7, 6).scl(1 / Platformer.PPM);
        vertices[2] = new Vector2(-3, 3).scl(1 / Platformer.PPM);
        vertices[3] = new Vector2(3, 3).scl(1 / Platformer.PPM);
        head.set(vertices);

        fdef.shape = head;
        fdef.filter.categoryBits = Platformer.ENEMY_HEAD_BIT;
        //small bounce when player hits snail's head
        fdef.restitution = 0.5f;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
    }
}
