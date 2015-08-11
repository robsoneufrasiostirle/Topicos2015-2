package br.grupointegrado.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

    public  TelaJogo(MainGame game){
        super(game);

    }

    @Override
    public void show() {

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initfonte();
        initInformacao();
        initJogador();

    }

    private void initInformacao(){

        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbpontuacao = new Label(" 0 Pontos", lbEstilo);
        palco.addActor(lbpontuacao);

    }

    private  void initfonte(){
        fonte = new BitmapFont();
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(.15f,.15f,.25f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbpontuacao.setPosition(10, camera.viewportHeight - 20);

        capturateclas();

        atualizarJogador(delta);
        
        palco.act(delta);
        palco.draw();
    }
    // atualiza a posição do jodador
    private void atualizarJogador(float delta) {

        // velocidade de movimento do jogador
        float velocidade = 200;
        if (indodireita){

            if(jogador.getX() < camera.viewportWidth - jogador.getImageWidth() ){
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x,y);
            }


        }
        if (indoesquerda){

            if(jogador.getX() > 0  ) {
                float x = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }

        }
    }

    // verifica quais teclas estao pressionadas
    private void capturateclas() {

        indodireita = false;
        indoesquerda = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){

            indoesquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){

            indodireita = true;
        }
    }

    @Override
    public  void resize(int width, int height){
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause(){

    }

    private void initJogador() {
        texturajogador = new Texture("sprites/player.png");
        texturajogadordireita = new Texture("sprites/player-right.png");
        texturajogadoresquerda = new Texture("sprites/player-left.png");

        jogador = new Image(texturajogador);
        float x = camera.viewportWidth /2 -jogador.getWidth() /2;
        float y = 15;
        jogador.setPosition(x, y);
        palco.addActor(jogador);
    }

    @Override
    public void resume(){

    }


    @Override
    public void dispose(){

        batch.dispose();
        palco.dispose();
        fonte.dispose();
        texturajogadordireita.dispose();
        texturajogador.dispose();
        texturajogadoresquerda.dispose();
    }


}
