package br.grupointegrado.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by Robson on 04/08/2015.
 */
public class TelaJogo extends TelaBase {

    private OrthographicCamera camera;

    private SpriteBatch batch;

    private Stage palco;

    private BitmapFont fonte;
    private Label lbpontuacao;
    private Image jogador;
    private Texture texturajogador;
    private Texture texturajogadoresquerda;
    private Texture texturajogadordireita;

    private boolean indodireita;
    private boolean indoesquerda;

    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture texturaTiro;
    private Texture texturaMeteoro1;
    private Texture texturaMeteoro2;
    private Array<Image> meteoros1 = new Array<Image>();
    private Array<Image> meteoros2 = new Array<Image>();

    public TelaJogo(MainGame game) {
        super(game);

    }

    @Override
    public void show() {

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initTexturas();
        initfonte();
        initInformacao();
        initJogador();

    }

    private void initTexturas() {
        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");
    }

    // instancia as informações escritas na tela
    private void initInformacao() {

        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbpontuacao = new Label(" 0 Pontos", lbEstilo);
        palco.addActor(lbpontuacao);

    }

    // instancia os objetos da fonte

    private void initfonte() {
        fonte = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbpontuacao.setPosition(10, camera.viewportHeight - 20);

        capturateclas();

        atualizarJogador(delta);
        atualizarTiros(delta);
        atualizarMeteoros(delta);

        palco.act(delta);
        palco.draw();
    }

    private void atualizarMeteoros(float delta) {
        int tipo = MathUtils.random(1, 3);
        if (tipo == 1) {
            // cria meteoro 1
            Image meteoro = new Image(texturaMeteoro1);
            float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
            float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
            meteoro.setPosition(x, y);
            meteoros1.add(meteoro);
            palco.addActor(meteoro);
        } else {
            // cria meteoro 2
        }

        float velocidade = 200;
        for (Image meteoro : meteoros1) {

            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade * delta;
            meteoro.setPosition(x, y);

        }
    }

    private final float MIN_INTERVALO_TIROS = 0.4f; // min de tempo entre os tiros
    private float intervaloTiros = 0;// tempo acumulado entre os tiros

    private void atualizarTiros(float delta) {

        if (atirando) {
            intervaloTiros = intervaloTiros + delta; // acumula o tempo percorrido
            // verifica se o tempo minimo e atingido
            if (intervaloTiros >= MIN_INTERVALO_TIROS) {
                Image tiro = new Image(texturaTiro);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);

                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
            }

        }

        float velocidade = 200; // velocidade de movimentacao do tiro
        // percorre todos os tiros existentes
        for (Image tiro : tiros) {
            // movimenta o tiro em direcao ao topo
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);

            // remove os tiros que sairam da tela
            if (tiro.getY() > camera.viewportHeight) {
                tiros.removeValue(tiro, true); // remove da lista
                tiro.remove(); // remove do palco
            }

        }

    }

    // atualiza a posição do jogador
    private void atualizarJogador(float delta) {

        // velocidade de movimento do jogador
        float velocidade = 200;
        if (indodireita) {

            // verifica se o jogador esta dentro da tela
            if (jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }


        }
        if (indoesquerda) {
            // verifica se o jogador esta dentro da tela
            if (jogador.getX() > 0) {
                float x = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }

        }

        if (indodireita) {
            // trocando imagem direita
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturajogadordireita)));

        } else if (indoesquerda) {
            // trocando imagem esquerda
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturajogadoresquerda)));
        } else {
            // trocando imagem centro
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturajogador)));
        }

    }

    // verifica quais teclas estao pressionadas
    private void capturateclas() {

        indodireita = false;
        indoesquerda = false;
        atirando = false;


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            indoesquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            indodireita = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            atirando = true;
        }
    }


    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause() {

    }

    //instancia os objetos do jogador e adiciona o palco
    private void initJogador() {
        texturajogador = new Texture("sprites/player.png");
        texturajogadordireita = new Texture("sprites/player-right.png");
        texturajogadoresquerda = new Texture("sprites/player-left.png");

        jogador = new Image(texturajogador);
        float x = camera.viewportWidth / 2 - jogador.getWidth() / 2;
        float y = 15;
        jogador.setPosition(x, y);
        palco.addActor(jogador);
    }

    @Override
    public void resume() {

    }


    @Override
    public void dispose() {

        batch.dispose();
        palco.dispose();
        fonte.dispose();
        texturajogadordireita.dispose();
        texturajogador.dispose();
        texturajogadoresquerda.dispose();
        texturaTiro.dispose();
        texturaMeteoro1.dispose();
        texturaMeteoro2.dispose();
    }


}
