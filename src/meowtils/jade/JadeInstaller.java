package meowtils.jade;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import wtf.tatp.meowtils.Meowtils;

public class JadeInstaller {
    private static final String LOADER_PAYLOAD = "/meowtils/jade/jade-loader.jar";
    private static final String JADE_PAYLOAD = "/meowtils/jade/happy.jar";

    public static byte[] readResource(String path) {
        InputStream is = openResource(path);
        if (is == null) {
            return null;
        }
        try {
            int n;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            while ((n = is.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try { is.close(); } catch (Exception ignored) {}
        }
    }

    private static InputStream openResource(String path) {
        String relative = path.startsWith("/") ? path.substring(1) : path;
        InputStream is = JadeInstaller.class.getResourceAsStream(path);
        if (is != null) return is;
        
        ClassLoader own = JadeInstaller.class.getClassLoader();
        if (own != null) {
            is = own.getResourceAsStream(relative);
            if (is != null) return is;
            
            is = fromDelegate(own, relative);
            if (is != null) return is;
        }
        
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        if (context != null) {
            is = context.getResourceAsStream(relative);
            if (is != null) return is;
        }
        
        return ClassLoader.getSystemResourceAsStream(relative);
    }

    private static InputStream fromDelegate(ClassLoader loader, String relative) {
        for (Class<?> type = loader.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (ClassLoader.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        ClassLoader delegate = (ClassLoader) field.get(loader);
                        if (delegate != null && delegate != loader) {
                            InputStream is = delegate.getResourceAsStream(relative);
                            if (is != null) return is;
                        }
                    } catch (Throwable ignored) {}
                }
            }
        }
        return null;
    }

    public static String install() {
        try {
            byte[] loaderBytes = readResource(LOADER_PAYLOAD);
            byte[] jadeBytes = readResource(JADE_PAYLOAD);
            
            if (loaderBytes == null || jadeBytes == null) {
                return "Payload missing! Rebuild extension.";
            }
            
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "jade_inject");
            tempDir.mkdirs();
            
            File loaderFile = new File(tempDir, "jade-loader.jar");
            File jadeFile = new File(tempDir, "jade.jar"); // Must be named jade.jar for the loader
            
            write(loaderFile, loaderBytes);
            write(jadeFile, jadeBytes);
            
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            
            String javaPath = "C:\\Program Files\\Java\\jdk-22\\bin\\java.exe";
            if (!new File(javaPath).exists()) {
                javaPath = "java"; 
            }
            
            ProcessBuilder pb = new ProcessBuilder(javaPath, "-jar", "jade-loader.jar", "--pid", pid);
            pb.directory(tempDir);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append(" ");
            }
            
            return "Jade injected instantly! Try pressing P or type .bind gui u in chat.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error injecting: " + e.getMessage();
        }
    }

    public static String uninstall() {
        return "To unload Jade, please restart the game completely.";
    }

    public static String status() {
        return "Click 'Load Jade' to instantly inject the client into the game!";
    }

    private static void write(File file, byte[] bytes) throws Exception {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(bytes);
        }
    }
}
