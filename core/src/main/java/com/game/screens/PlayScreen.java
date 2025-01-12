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

public class PlayScreen implements Screen {
    private Platformer game;
    private TextureAtlas atlas;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    // variables box2d
    private World world;
    private WorldCreator creator;
    // sprites
    private Player player;

    public PlayScreen(Platformer game) {
        this.game = game;

        atlas = new TextureAtlas("test5");
        // camera pour suivre le personnage dans le monde
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Platformer.V_WIDTH / Platformer.PPM, Platformer.V_HEIGHT / Platformer.PPM, gamecam);
        // charger la mappe
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("new.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Platformer.PPM);
        // centrer la camera au debut du jeu
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        // créer le monde et la gravité
        world = new World(new Vector2(0, -10), true);
        // créer le joueur
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
        // physiques
        world.step(1/60f, 6, 2);
        // mettre à jour la position du joueur
        player.update(delta);
        // update escargots
        for (Enemy enemy : creator.getSnails()) {
            enemy.update(delta);
        }
        // update champignons
        for (Enemy enemy : creator.getMushrooms()) {
            enemy.update(delta);
        }
        // update hérissons
        for (Enemy enemy : creator.getTurtles()) {
            enemy.update(delta);
        }
        // camera suit la coordonnée x du joueur
        if (player.currentState != Player.State.DEAD) {
            gamecam.position.x = player.body.getPosition().x;
        }
        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        // effacer l'écran du jeu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // render la mappe
        renderer.render();

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getSnails()) {
            enemy.draw(game.batch);
        }
        for (Enemy enemy : creator.getMushrooms()) {
            enemy.draw(game.batch);
        }
        for (Enemy enemy : creator.getTurtles()) {
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
        gamePort.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
    }
}
