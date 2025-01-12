package com.game.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.game.*;
import com.game.screens.PlayScreen;
import com.game.sprites.enemies.Turtle;
import com.game.sprites.items.Box;
import com.game.sprites.items.Coin;
import com.game.sprites.enemies.Mushroom;
import com.game.sprites.enemies.Snail;

public class WorldCreator {
    private Array<Snail> snails;
    private Array<Mushroom> mushrooms;
    private Array<Turtle> turtles;

    public WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        // create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // créer le terrain
        for (RectangleMapObject object : map.getLayers().get("ground").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Platformer.PPM, (rect.getY() + rect.getHeight() / 2) / Platformer.PPM);

            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2 / Platformer.PPM, rect.getHeight() / 2 / Platformer.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // créer les murs
        for (RectangleMapObject object : map.getLayers().get("wall").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Platformer.PPM, (rect.getY() + rect.getHeight() / 2) / Platformer.PPM);

            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2 / Platformer.PPM, rect.getHeight() / 2 / Platformer.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = Platformer.WALL_BIT;
            body.createFixture(fdef);
        }

        // créer les monnaies
        for (RectangleMapObject object : map.getLayers().get("coin").getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen, object);
        }

        // créer les boites
        for (RectangleMapObject object : map.getLayers().get("box").getObjects().getByType(RectangleMapObject.class)) {
            new Box(screen, object);
        }

        // créer les escargots
        snails = new Array<>();
        for (RectangleMapObject object : map.getLayers().get("snail").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            snails.add(new Snail(screen, rect.getX() / Platformer.PPM, rect.getY() / Platformer.PPM));
        }

        // créer les champignons
        mushrooms = new Array<>();
        for (RectangleMapObject object : map.getLayers().get("mushroom").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            mushrooms.add(new Mushroom(screen, rect.getX() / Platformer.PPM, rect.getY() / Platformer.PPM));
        }

        // créer les hérissons
        turtles = new Array<>();
        for (RectangleMapObject object : map.getLayers().get("turtle").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / Platformer.PPM, rect.getY() / Platformer.PPM));
        }
    }

    public Array<Snail> getSnails() {
        return snails;
    }

    public Array<Mushroom> getMushrooms() {
        return mushrooms;
    }

    public Array<Turtle> getTurtles() {
        return turtles;
    }
}
