package sfllhkhan95.doodle.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author saifkhichi96
 */
public class DoodleDatabase {

    private final static String PREFIX = "DOODLE_";
    private final static String DIR = "Pictures/Doodles/";
    private final static String EXT = ".jpg";

    public static void saveDoodle(final Bitmap doodle) {
        // Create file for storage with CURRENT_TIMESTAMP as name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy_hhmmss", Locale.US);
        String timestamp = simpleDateFormat.format(new Date());
        String fname = PREFIX + timestamp + EXT;

        saveDoodle(doodle, fname);
    }

    public static Bitmap loadDoodle(String fname) {
        String root = Environment.getExternalStorageDirectory().toString() + "/" + DIR;
        File myDir = new File(root);
        myDir.mkdirs();

        return BitmapFactory.decodeFile(root + fname);
    }

    public static String[] listDoodles() {
        String root = Environment.getExternalStorageDirectory().toString() + "/" + DIR;
        File myDir = new File(root);
        myDir.mkdirs();

        return myDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(PREFIX) && name.endsWith(EXT);
            }
        });
    }

    public static void saveDoodle(Bitmap doodle, String fname) {
        // Create or open storage directory
        String root = Environment.getExternalStorageDirectory().toString() + "/" + DIR;
        File myDir = new File(root);
        myDir.mkdirs();

        // Create file
        File file = new File(myDir, fname);

        // If a file with this name already exists, delete it.
        if (file.exists()) {
            file.delete();
        }

        // Write doodle to file
        try {
            FileOutputStream out = new FileOutputStream(file);
            doodle.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
