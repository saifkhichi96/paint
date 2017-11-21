package sfllhkhan95.doodle.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;

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

    private final static File rootDir;
    private final static String rootDirPath;

    static {
        rootDirPath = Environment.getExternalStorageDirectory().toString() + "/" + DIR;
        rootDir = new File(rootDirPath);
        rootDir.mkdirs();
    }

    public static void saveDoodle(final Bitmap doodle) {
        // Create file for storage with CURRENT_TIMESTAMP as name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US);
        String timestamp = simpleDateFormat.format(new Date());
        String filename = PREFIX + timestamp + EXT;

        saveDoodle(doodle, filename);
    }

    @Nullable
    public static Bitmap loadDoodle(String filename, int wd, int ht) {
        File file = new File(rootDir, filename);
        if (file.exists()) {
            return DoodleFactory.loadFromPath(rootDirPath + filename, wd, ht);
        } else {
            return null;
        }
    }

    public static String[] listDoodles() {
        return rootDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(PREFIX) && name.endsWith(EXT);
            }
        });
    }

    public static void saveDoodle(Bitmap doodle, String filename) {
        File file = new File(rootDir, filename);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            doodle.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean contains(String filename) {
        File file = new File(rootDir, filename);
        return (file.exists());
    }

    public static File removeDoodle(String filename) {
        File file = new File(rootDir, filename);
        if (file.exists()) file.delete();
        return file;
    }

}