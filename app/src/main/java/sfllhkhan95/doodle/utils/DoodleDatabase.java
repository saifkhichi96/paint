package sfllhkhan95.doodle.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author saifkhichi96
 */
public class DoodleDatabase {

    private final static String PREFIX_V1 = "DOODLE_";
    private final static String PREFIX = "DOODLEv2_";
    private final static String DIR = "Pictures/Doodles/";
    private final static String EXT = ".jpg";

    private final static File rootDir;
    private final static String rootDirPath;

    static {
        rootDirPath = Environment.getExternalStorageDirectory().toString() + "/" + DIR;
        rootDir = new File(rootDirPath);
        rootDir.mkdirs();
    }

    private static String upgradeToV2(String oldName) {
        String date = oldName.split("_")[1];
        String time = oldName.split("_")[2];

        String day = date.substring(0, 2);
        String month = date.substring(2, 4);
        String year = date.substring(4, 8);

        date = year + month + day;
        String newName = PREFIX + date + "_" + time;

        Bitmap doodle = DoodleFactory.loadFromPath(rootDirPath + oldName);
        saveDoodle(doodle, newName);
        removeDoodle(oldName);

        return newName;
    }

    public static void saveDoodle(final Bitmap doodle) {
        // Create file for storage with CURRENT_TIMESTAMP as name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
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
        String[] list = rootDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(PREFIX) || name.startsWith(PREFIX_V1)) && name.endsWith(EXT);
            }
        });
        if (list == null) {
            return null;
        }

        List<String> newList = new ArrayList<>();

        for (String name : list) {
            // If file is from an older version, upgrade filename
            if (name.startsWith(PREFIX_V1)) {
                name = upgradeToV2(name);
            }

            newList.add(name);
        }
        list = newList.toArray(list);

        Arrays.sort(list, Collections.<String>reverseOrder());
        return list;
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