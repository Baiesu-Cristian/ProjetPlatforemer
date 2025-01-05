package com.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.*;
import com.game.sprites.enemies.Enemy;
import com.game.sprites.Player;
import com.game.tools.WorldContactListener;
import com.game.tools.WorldCreator;

/** First screen of the application. Displayed after the application is created. */
public class PlayScreen implements Screen {
    private Platformer game;
    private TextureAtlas atlas;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    // Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    // box2d variables
    private World world;
    private WorldCreator creator;
    private Box2DDebugRenderer b2dr;
    // sprites
    private Player player;

    public PlayScreen(Platformer game) {
        this.game = game;

        atlas = new TextureAtlas("test4");
        // create cam used to follow player through world
        gamecam = new OrthographicCamera();
        // create a fitViewport to maintain virtual aspect ratio despite screen adjustments
        gamePort = new FitViewport(Platformer.V_WIDTH / Platformer.PPM, Platformer.V_HEIGHT / Platformer.PPM, gamecam);
        // load the map and setup the map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("new.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Platformer.PPM);
        // center the gamecam at the start of the game
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        // create world and gravity
        world = new World(new Vector2(0, -10), true);
        // de scos
        b2dr = new Box2DDebugRenderer();
        // create player
        player = new Player(this);

        creator = new WorldCreator(this);

        world.setContactListener(new WorldContactListener());
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void show() {
    }

    public void handleInput() {
        if (player.currentState != Player.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.currentState != Player.State.JUMPING && player.currentState != Player.State.FALLING) {
                player.body.applyLinearImpulse(new Vector2(0, 4f), player.body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 2) { //max speed
                player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -2) { //max speed
                player.body.applyLinearImpulse(new Vector2(-0.1f, 0), player.body.getWorldCenter(), true);
            }
        }
    }

    public boolean gameOver() {
        return player.currentState == Player.State.DEAD && player.getStateTimer() > 3;
    }

    public void update(float delta) {
        handleInput();
        // physics
        world.step(1/60f, 6, 2);
        // update player's position
        player.update(delta);
        // update snails
        for (Enemy enemy : creator.getSnails()) {
            enemy.update(delta);
        }
        // update mushrooms
        for (Enemy enemy : creator.getMushrooms()) {
            enemy.update(delta);
        }
        // camera follows player's x coordinates
        if (player.currentState != Player.State.DEAD) {
            gamecam.position.x = player.body.getPosition().x;
        }
        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        // Clear the game screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // render the game map
        renderer.render();
        // render the debug lines (DE SCOS)
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getSnails()) {
            enemy.draw(game.batch);
        }
        for (Enemy enemy : creator.getMushrooms()) {
            enemy.draw(game.batch);
        }
        game.batch.end();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        gamePort.update(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
    }
}
