package ru.shevchenko.waterfallpuzzle.puzzle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import ru.shevchenko.waterfallpuzzle.R;
import ru.shevchenko.waterfallpuzzle.adapter.StorePreference;
import ru.shevchenko.waterfallpuzzle.cutter.CutMap;
import ru.shevchenko.waterfallpuzzle.cutter.ImageCutter;
import ru.shevchenko.waterfallpuzzle.models.PuzzlePiece;

/**
 * Created by Colan Infotech.
 */

public class Puzzle {

    Activity mActivity;
   public  Bitmap mSourceImage = null;
    ArrayList<PuzzlePiece> puzzlePieceArrayList;
    StorePreference storePreference;
    public ArrayList<PuzzlePiece> createPuzzlePieces(Activity aActivity, Bitmap aBitmap,
                                                     ImageView imageView, String path, int horizontalResolution, int verticalResolution) {
        this.mActivity = aActivity;
        this.puzzlePieceArrayList = new ArrayList<>();
        getDisplaySize(aBitmap,imageView);
        deleteDirectories(path);
        CutMap cutMap = new CutMap(horizontalResolution, verticalResolution);
        ImageCutter imageCutter = new ImageCutter(mSourceImage, cutMap);
        drawOrderedPuzzlePieces(imageCutter, cutMap);
        mSourceImage = null;
        return puzzlePieceArrayList;
    }

    private void getDisplaySize(Bitmap bitmap,ImageView imageView) {

        Bitmap image = null;

        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        int widthFinal;
        int heightFinal;

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {//Tablet
            widthFinal = width - 750;
            heightFinal = height - 100;
            try {
                image = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.jig);
                mSourceImage = Bitmap.createScaledBitmap(image, widthFinal, heightFinal, false);

            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            } finally {
                image = null;
            }
        } else {//Mobile
            widthFinal = width - 400;
            heightFinal = height - 100;
            try {
                image = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.jig);
                mSourceImage = Bitmap.createScaledBitmap(image, widthFinal, heightFinal, false);
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            } finally {
                image = null;
            }
        }
        storePreference=new StorePreference(mActivity);
        storePreference.setString("bitmap", String.valueOf(mSourceImage));
        imageView.setImageBitmap(mSourceImage);
    }

    private void deleteDirectories(String aPath) {
        File dir = new File(Environment.getExternalStorageDirectory().toString() + aPath);
        if (dir.exists() && dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
        } else {
            dir.mkdir();
        }
    }

    private void drawOrderedPuzzlePieces(ImageCutter imageCutter, CutMap cutMap) {
        PuzzlePiece[][] puzzlePieces = imageCutter.cutImage();
        for (int i = 0; i < cutMap.getHorizontalResolution(); i++) {
            for (int j = 0; j < cutMap.getVerticalResolution(); j++) {
                PuzzlePiece piece = puzzlePieces[i][j];
                puzzlePieceArrayList.add(piece);
            }
        }
    }

}
