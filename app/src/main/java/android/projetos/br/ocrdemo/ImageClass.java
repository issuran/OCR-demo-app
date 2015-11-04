package android.projetos.br.ocrdemo;



import android.graphics.Bitmap;

/*
Tiago Oliveira
11/04/2015
tiago_fernandes89@hotmail.com
*/

public class ImageClass{

    private static ImageClass _instance;
    private Bitmap img;

    private ImageClass()
    {
    }

    public static ImageClass getInstance()
    {
        if (_instance == null)
        {
            _instance = new ImageClass();
        }
        return _instance;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

}
