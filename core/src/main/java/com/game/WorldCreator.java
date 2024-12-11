package com.game;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class WorldCreator {
    private Array<Snail> snails;

    public WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        // create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // create ground bodies
        for (RectangleMapObject object : map.getLayers().get("ground").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Platformer.PPM, (rect.getY() + rect.getHeight() / 2) / Platformer.PPM);

            body = world.createBody(bdef);
            shape.setAsBox(rect.getWidth() / 2 / Platformer.PPM, rect.getHeight() / 2 / Platformer.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // create wall bodies (for enemies)
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

        //create coin bodies
        for (RectangleMapObject object : map.getLayers().get("coin").getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen, object);
        }

        // create box bodies
        for (RectangleMapObject object : map.getLayers().get("box").getObjects().getByType(RectangleMapObject.class)) {
            new Box(screen, object);
        }

        // create snails
        snails = new Array<Snail>();
        for (RectangleMapObject object : map.getLayers().get("snail").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            snails.add(new Snail(screen, rect.getX() / Platformer.PPM, rect.getY() / Platformer.PPM));
        }
    }

    public Array<Snail> getSnails() {
        return snails;
    }
}
