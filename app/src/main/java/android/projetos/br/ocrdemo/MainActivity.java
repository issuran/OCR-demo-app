package android.projetos.br.ocrdemo;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
Tiago Oliveira
11/04/2015
tiago_fernandes89@hotmail.com
*/


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    //Declaracao das variaveis
    private static final int RESULT_LOAD_IMAGE = 54321;
    private static String TAG;

    //Componentes
    private ImageView imgMain;
    private ImageButton btGallery;

    //Tesseract
    TessBaseAPI baseApi ;
    public String lang = "eng";
    public File INNER_PATH;

    //Streams
    InputStream in = null;
    OutputStream out = null;

    //Animaçao de clicar em um objeto
    private AlphaAnimation clickedEffect = new AlphaAnimation(1F, 0.5F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Endereco onde sera armazenado o arquivo de dado treinado
        INNER_PATH = this.getFilesDir();

        //Criando o diretorio caso nao exista
        File dir = new File(INNER_PATH + "/tessdata/");
        if (!dir.exists())
            dir.mkdirs();

        //Se o arquivo ja nao estiver la, sera copiado
        if (!(new File(INNER_PATH + "/tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                in = assetManager.open("tessdata/eng.traineddata");
                File outFile = new File(INNER_PATH, "tessdata/eng.traineddata");
                out = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Log.v(TAG, "Copiado " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Não foi possivel copiar " + lang + " traineddata " + e.toString());
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Recuperar componentes ImageView, Buttons
        imgMain = (ImageView) findViewById(R.id.imgMain);
        btGallery = (ImageButton) findViewById(R.id.btGallery);

        //Setando os listeners para os objetos
        imgMain.setOnClickListener(this);
        btGallery.setOnClickListener(this);

        baseApi = new TessBaseAPI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgMain:
                imgMain.startAnimation(clickedEffect);
                //Crio a Intent para a galeria
                Intent galleryIntent = new Intent();

                // Exibir somente imagens
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                //Inicio a Intent, neste caso ira buscar na Galeria
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), RESULT_LOAD_IMAGE);
                break;
            case R.id.btGallery:
                btGallery.startAnimation(clickedEffect);
                //Crio a Intent para a galeria
                Intent galleryIntent2 = new Intent();

                // Exibir somente imagens
                galleryIntent2.setType("image/*");
                galleryIntent2.setAction(Intent.ACTION_GET_CONTENT);

                //Inicio a Intent, neste caso ira buscar na Galeria
                startActivityForResult(Intent.createChooser(galleryIntent2, "Select Picture"), RESULT_LOAD_IMAGE);
                break;
        }
    }

    //Metodo subrescrito para conseguir obter o resultado das Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verifico se o resultado eh da Intent da Galeria (RESULT_LOAD_IMAGE)
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){

            //Pegando a imagem selecionada
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                ImageClass.getInstance().setImg(bitmap);
                imgMain.setImageBitmap(ImageClass.getInstance().getImg());

                Log.v(TAG, "Antes de baseApi init");
                baseApi.init(INNER_PATH.toString(), "eng");

                //Imagem que sera extraido o texto
                baseApi.setImage(ImageClass.getInstance().getImg());

                //recognizedText - variavel que recebe o texto extraido
                String recognizedText = baseApi.getUTF8Text();
                Log.v(TAG, "OCR Resultado: " + recognizedText);

                baseApi.end();

                Toast.makeText(this, recognizedText, Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
