package com.game.sprites.items;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.game.Platformer;
import com.game.screens.PlayScreen;

public abstract class Interactive {
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected MapObject object;

    public Interactive(PlayScreen screen, MapObject object) {
        this.object = object;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Platformer.PPM, (bounds.getY() + bounds.getHeight() / 2) / Platformer.PPM);
        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / Platformer.PPM, bounds.getHeight() / 2 / Platformer.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
    }

    public void onHeadHit(){
        // si la tête du joueur touche l'objet, il est détruit
        setCategoryFilter(Platformer.DESTROYED_BIT);
        getCell().setTile(null);
    }
    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell() {
        // gere les objets à partir de la mappe Tiled
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Graphics");
        return layer.getCell((int) (body.getPosition().x * Platformer.PPM / 16), (int) (body.getPosition().y * Platformer.PPM / 16));
    }
}

