package com.game.tools;

import com.badlogic.gdx.physics.box2d.*;
import com.game.sprites.enemies.Enemy;
import com.game.sprites.items.Interactive;
import com.game.Platformer;
import com.game.sprites.Player;

// est appelée quand deux objets se touchent
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            // verifier quelle fixture est la tete, est laquelle est l'objet
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            // renvoie vrai si le joueur touche un objet interactive
            if (object.getUserData() != null && Interactive.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Interactive) object.getUserData()).onHeadHit();
            }
        }

        // gere les collisions
        switch (cDef) {
            // joueur touche la tete de l'ennemie
            case Platformer.ENEMY_HEAD_BIT | Platformer.PLAYER_BIT:
                // verifier quelle fixture est le joueur, est laquelle est l'ennemie
                if (fixA.getFilterData().categoryBits == Platformer.ENEMY_HEAD_BIT) {
                    ((Enemy) fixA.getUserData()).hitOnHead((Player) fixB.getUserData());
                } else {
                ((Enemy) fixB.getUserData()).hitOnHead((Player) fixA.getUserData());
                }
                break;
            // joueur touche l'ennemie et meurt
            case Platformer.PLAYER_BIT | Platformer.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == Platformer.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                } else {
                    ((Player) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                }
                break;
            // ennemie touche un mur et doit changer la direction
            case Platformer.ENEMY_HEAD_BIT | Platformer.WALL_BIT:
                if (fixA.getFilterData().categoryBits == Platformer.ENEMY_HEAD_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            // ennemie touche un autre ennemie et les deux changent direction
            case Platformer.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
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
