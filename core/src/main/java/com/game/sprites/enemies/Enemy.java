package com.game.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.game.Platformer;
import com.game.screens.PlayScreen;
import com.game.sprites.Player;

public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-0.15f, 0);
        //velocity = new Vector2(0, 0);
    }

    // posibil de scos y
    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }
        if (y) {
            velocity.y = -velocity.y;
        }
    }

    public abstract void hitOnHead(Player player);
    public abstract void onEnemyHit(Enemy enemy);
    public abstract void update(float delta);

    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / Platformer.PPM);

        // set the enemy's filter
        fdef.filter.categoryBits = Platformer.ENEMY_BIT;
        // what the enemy can collide with
        fdef.filter.maskBits = Platformer.Ground_BIT | Platformer.BOX_BIT | Platformer.COIN_BIT | Platformer.PLAYER_BIT | Platformer.WALL_BIT | Platformer.ENEMY_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

        //create enemy's head
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
        //fdef.restitution = 1.5f;
        body.createFixture(fdef).setUserData(this);
    }
}
