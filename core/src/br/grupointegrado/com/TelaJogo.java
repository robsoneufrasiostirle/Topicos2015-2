package br.grupointegrado.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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

    private Stage palcoInformacoes;

    private BitmapFont fonte;
    private Label lbpontuacao;
    private Label lbgameover;
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

    private Array<Texture> texturasExplosao = new Array<Texture>();
    private  Array<Explosao> explosoes = new Array<Explosao>();

    private Sound somTiro;
    private Sound somExplosao;
    private Sound somGameOver;
    private Music musicaFundo;

    public TelaJogo(MainGame game) {
        super(game);

    }

    @Override
    public void show() {

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        palcoInformacoes = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initSons();
        initTexturas();
        initfonte();
        initInformacao();
        initJogador();

    }

    public void initSons(){

        somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));

        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);
    }

    private void initTexturas() {
        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");


        for (int i = 1; i<=17; i++){
            Texture text = new Texture("sprites/explosion-" + i + ".png");
            texturasExplosao.add(text);
        }
    }

    // instancia as informações escritas na tela
    private void initInformacao() {

        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbpontuacao = new Label(" 0 Pontos", lbEstilo);
        palcoInformacoes.addActor(lbpontuacao);

        lbgameover = new Label("Game Over", lbEstilo);
        palcoInformacoes.addActor(lbgameover);

    }

    // instancia os objetos da fonte
    private void initfonte() {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = Color.WHITE;
        param.size = 24;
        param.shadowOffsetX = 1;
        param.shadowOffsetY = 1;
        param.shadowColor = Color.BLUE;

        fonte = generator.generateFont(param);
        generator.dispose();

     //   fonte = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbpontuacao.setPosition(10, camera.viewportHeight - lbpontuacao.getPrefHeight() - 20);

        lbpontuacao.setText(pontuacao + " pontos");

        lbgameover.setPosition(camera.viewportWidth / 2 - lbgameover.getPrefWidth() / 2, camera.viewportHeight / 2);
        lbgameover.setVisible(gameOver == true);

        atualizarExplosoes(delta);
        if (gameOver == false){

            if(!musicaFundo.isPlaying()){ //  se nao esta tocando
                musicaFundo.play(); // iniciar musica
            }
            capturateclas();

            atualizarJogador(delta);
            atualizarTiros(delta);
            atualizarMeteoros(delta);

            detectarColisoes(meteoros1, 5);
            detectarColisoes(meteoros2, 15);
        }else{
            if (musicaFundo.isPlaying()){ // se esta tocando
                musicaFundo.stop(); // parar musica
            }
        }



        palco.act(delta);
        palco.draw();

        palcoInformacoes.act(delta);
        palcoInformacoes.draw();
    }

    private void atualizarExplosoes(float delta) {
        for (Explosao explosao: explosoes){

            // verifica se a explosao chegou ao fim
            if (explosao.getEstagio() >= 16){
                //remove a explosao do array
                explosoes.removeValue(explosao, true);
                explosao.getAtor().remove(); // remove o ator do palco
            }else {
                explosao.atualizar(delta);
            }
        }
    }

    private Rectangle recJogador = new Rectangle();
    private Rectangle recTiro = new Rectangle();
    private Rectangle recMeteoro = new Rectangle();
    private int pontuacao = 0;
    private boolean gameOver = false;

    private void detectarColisoes(Array<Image> meteoros, int valorPont) {
        recJogador.set(jogador.getX(), jogador.getY(), jogador.getWidth(), jogador.getHeight());
        // detecta colisao com o tiro
        for(Image meteoro: meteoros){
            recMeteoro.set(meteoro.getX(), meteoro.getY(), meteoro.getWidth(), meteoro.getHeight());
            for(Image tiro : tiros){
                recTiro.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getHeight());

                if(recMeteoro.overlaps(recTiro)){
                    // se sim ocorre uma colisao do tiro com o meteoro 1
                    pontuacao += valorPont; // incrementa a pontuacao
                    tiro.remove();// remove do palco
                    tiros.removeValue(tiro, true); // remove da lista
                    meteoro.remove(); // remove do palco
                    meteoros.removeValue(meteoro, true); // remove da lista

                    criarExplosao(meteoro.getX() + meteoro.getWidth() / 2, meteoro.getY() + meteoro.getHeight() /2);

                }
            }
            // detecta colisao com o player
            if(recJogador.overlaps(recMeteoro)){
                // ocorre colisao do jogador com meteoro 1
                gameOver = true;
                somGameOver.play();
            }
        }
    }

    // cria a explosao na posicao x e y
    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturasExplosao.get(0));
        ator.setPosition(x - ator.getWidth() / 2, y - ator.getHeight() / 2);
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator,texturasExplosao);
        explosoes.add(explosao);

        somExplosao.play();

    }

    private void atualizarMeteoros(float delta) {

        int qtdMeteoro = meteoros1.size + meteoros2.size; // retorna a quantidade de meteoros criados

        if (qtdMeteoro < 15) {

            int tipo = MathUtils.random(1, 4); // retorna 1 ou 2 aleatoriamente
            if (tipo == 1) {
                // cria meteoro 1
                Image meteoro = new Image(texturaMeteoro1);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros1.add(meteoro);
                palco.addActor(meteoro);
            } else if (tipo == 2){
                // cria meteoro 2
                Image meteoro = new Image(texturaMeteoro2);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros2.add(meteoro);
                palco.addActor(meteoro);
            }
        }
            float velocidade1 = 100; // 200 pixels/segundos
            for (Image meteoro : meteoros1) {

                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade1 * delta;
                meteoro.setPosition(x, y);

                // remove os tiros que sairam da tela
                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove(); // remove do palco
                    meteoros1.removeValue(meteoro, true); // remove da lista
                }
            }

            float velocidade2 = 150; // 200 pixels/segundos
            for (Image meteoro : meteoros2) {

                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade2 * delta;
                meteoro.setPosition(x, y);

                // remove os tiros que sairam da tela
                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove(); // remove do palco
                    meteoros2.removeValue(meteoro, true); // remove da lista
                }

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
                somTiro.play();
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
        palcoInformacoes.dispose();


        for (Texture text: texturasExplosao){
            text.dispose();
        }
        somTiro.dispose();
        somExplosao.dispose();
        musicaFundo.dispose();
    }


}
