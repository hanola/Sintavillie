package no.olav.samples.facedetect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;

/** Takes snapshots of views and saves them to a bitmap. */
public class SnapShot {

		private final View view;
        
        /** Create snapshots based on the view and its children. */
        public SnapShot(View root) {
                this.view = root;
        }
        
        /** Create snapshot handler that captures the root of the whole activity. */
        public SnapShot(Activity activity) {
                final View contentView = activity.findViewById(android.R.id.content);
                this.view = contentView.getRootView();
        }
        
        /** Take a snapshot of the view. */
        public Bitmap snap() {
                Bitmap bitmap = Bitmap.createBitmap(this.view.getWidth(), this.view.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                Log.d("SnapShot", "The snap shot has been taken successfully");
                
                
                
                return bitmap;
        }
}
