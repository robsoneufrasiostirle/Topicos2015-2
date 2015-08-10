package br.grupointegrado.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
        palco.act(delta);
        palco.draw();
    }

    @Override
    public  void resize(int width, int height){
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }


    @Override
    public void dispose(){

        batch.dispose();
        palco.dispose();
        fonte.dispose();
    }


}
