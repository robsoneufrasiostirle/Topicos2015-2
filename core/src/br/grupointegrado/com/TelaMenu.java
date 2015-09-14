package br.grupointegrado.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Robson on 14/09/2015.
 */
public class TelaMenu extends TelaBase {

    private OrthographicCamera camera;
    private Stage palco;
    private ImageTextButton btnIniciar;
    private Label lbTitulo;
    private Label lbPontuacao;

    private BitmapFont fonteTitulo;
    private BitmapFont fontBotoes;

    private Texture texturaBotao; // textura normal do botao
    private Texture texturaBotaoPressionado; // textura botao pressionado

    public TelaMenu(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight));
        Gdx.input.setInputProcessor(palco); // define o palco como processador de entrada


        initFontes();
        initLabels();
    }

    private void initLabels() {
        Label.LabelStyle estilo = new Label.LabelStyle();

        estilo.font = fonteTitulo;

        lbTitulo = new Label("Space Invader", estilo);
        palco.addActor(lbTitulo);
    }

    private void initFontes() {
        FreeTypeFontGenerator gerador = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.color = new Color(.25f, .25f, .25f, 1); // tom de azul
        params.size = 48;
        params.shadowOffsetX = 2;
        params.shadowOffsetY = 2;
        params.shadowColor = Color.BLUE;

        fonteTitulo = gerador.generateFont(params);

        gerador.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        atualizarLabels();
        palco.act(delta);
        palco.draw();

    }

    private void atualizarLabels() {

        float x = camera.viewportWidth /2 - lbTitulo.getPrefWidth() / 2;
        float y = camera.viewportHeight - 100;
        lbTitulo.setPosition(x,y);
    }

    @Override
    public void resize(int width, int height) {

        camera.setToOrtho(false, width,height);
        camera.update();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

        palco.dispose();
        fonteTitulo.dispose();

    }
}
