package com.mobilesolutionworks.android.httpcache.store;

import com.mobilesolutionworks.android.httpcache.WorksCache;
import com.mobilesolutionworks.android.httpcache.config.WorksCacheConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yunarta on 17/5/15.
 */
public class WorksCacheFileStore implements WorksCacheStore {

    @Override
    public WorksCache load(String path) {
        File root = WorksCacheConfig.getInstance().getContext().getFilesDir();

        File dir = new File(root, "works-cache");
        dir.mkdirs();


        File file = new File(dir, md5(path));
        if (file.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);

                ByteArrayOutputStream out = new ByteArrayOutputStream((int) file.length());
                byte[] buffer = new byte[10240];
                int read;

                while (-1 != (read = in.read(buffer))) {
                    out.write(buffer, 0, read);
                }

                return new WorksCache(out.toByteArray(), file.lastModified());
            } catch (IOException e) {
                return null;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public void deleteCache(String path) {
        File root = WorksCacheConfig.getInstance().getContext().getFilesDir();

        File dir = new File(root, "works-cache");
        dir.mkdirs();


        File file = new File(dir, md5(path));
        file.delete();
    }

    @Override
    public void store(String path, WorksCache cache) throws IOException {
        File root = WorksCacheConfig.getInstance().getContext().getFilesDir();

        File dir = new File(root, "works-cache");
        dir.mkdirs();


        File file = new File(dir, md5(path));
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(cache.data());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        file.setLastModified(cache.time());
    }

    private String md5(String text) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());

            byte[] digest = md.digest();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            // e.printStackTrace();
        }

        return sb.toString();
    }
}
