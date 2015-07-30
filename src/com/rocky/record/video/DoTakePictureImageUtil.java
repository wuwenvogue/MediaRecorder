package com.rocky.record.video;
import android.graphics.Bitmap;  
import android.graphics.Matrix;  
  
/**
 * 
 * @author Rocky
 *
 * @Time 2015年7月30日  下午3:43:50
 *
 * @description 对图片进行旋转处理
 */
public class DoTakePictureImageUtil {  
    /** 
     * 旋转Bitmap 
     * @param b 
     * @param rotateDegree 
     * @return 
     */  
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){  
        Matrix matrix = new Matrix();  
        matrix.postRotate((float)rotateDegree);  
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);  
        return rotaBitmap;  
    }  
}  