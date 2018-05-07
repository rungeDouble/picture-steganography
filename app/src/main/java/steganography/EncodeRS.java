package steganography;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class EncodeRS {

    public static Bitmap encode(String input,Context c,File imgFile){
        RenderScript encodeRS= RenderScript.create(c);
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        int width = bmp.getWidth()-1;

        String toEncode= "!n17"+input+"$n%";

        if(toEncode.length()>bmp.getHeight()*bmp.getWidth()) return null;

        byte[] str_bytes= null;
        try {
            str_bytes = toEncode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.exit(0);
        }

        ScriptC_encode encodeScript= new ScriptC_encode(encodeRS);

        Allocation img= Allocation.createFromBitmap(encodeRS, bmp,Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        //bmp.recycle();

        Allocation char_array= Allocation.createSized(encodeRS, Element.U8(encodeRS),str_bytes.length,Allocation.USAGE_SCRIPT);
        char_array.copyFrom(str_bytes);
        encodeScript.bind_input_string(char_array);

        encodeScript.set_string_length(str_bytes.length);
        encodeScript.set_width(width);

        encodeScript.forEach_root(img);

        img.copyTo(bmp);

        return bmp;
    }

}