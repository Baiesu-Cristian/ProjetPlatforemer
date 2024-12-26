package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

// gets called when two fixtures collide with each other
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // collision with player's head, check which fixture is the head and which is the object
        // se scoate in ep.25
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            // return true if the player collides with an interactive object
            if (object.getUserData() != null && Interactive.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Interactive) object.getUserData()).onHeadHit();
            }
        }

        // manages collisions
        switch (cDef) {
            // Player collides with enemy's head
            case Platformer.ENEMY_HEAD_BIT | Platformer.PLAYER_BIT:
                Gdx.app.log("snail", "moare");
                // check which fixture is the player and which is the enemy's head
                if (fixA.getFilterData().categoryBits == Platformer.ENEMY_HEAD_BIT) {
                    ((Enemy) fixA.getUserData()).hitOnHead();
                } else {
                ((Enemy) fixB.getUserData()).hitOnHead();
                }
                break;
            // Player collides with enemy and dies
            case Platformer.PLAYER_BIT | Platformer.ENEMY_BIT:
                Gdx.app.log("player", "moare");
                if (fixA.getFilterData().categoryBits == Platformer.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).hit();
                } else {
                    ((Player) fixB.getUserData()).hit();
                }
                break;
            /*// Enemy collides with wall and has to reverse
            case Platformer.ENEMY_BIT | Platformer.WALL_BIT:
                if (fixA.getFilterData().categoryBits == Platformer.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;*/
            // Enemy head collides with wall and has to reverse
            case Platformer.ENEMY_HEAD_BIT | Platformer.WALL_BIT:
                if (fixA.getFilterData().categoryBits == Platformer.ENEMY_HEAD_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            // Enemy collides with another enemy and they both reverse
            case Platformer.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
