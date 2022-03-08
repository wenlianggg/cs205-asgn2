package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Printer {

    public static final boolean ENABLED = false;

    public static volatile LocalDateTime lastAction = LocalDateTime.now();

    public static void printf(String fmt, Object ...objs) {
        println(String.format(fmt, objs));
    }

    public static void println(Object ...objs) {
        String[] strs = new String[objs.length];

        lastAction = LocalDateTime.now();

        for (int i = 0; i < objs.length; i++) {
            strs[i] = objs[i].toString();
        }

        System.out.println(String.join(" ", strs));
        System.out.flush();

        try (FileWriter file = new FileWriter("foodmanager.log", true)) {
            file.write(String.join(" ", strs) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void debugln(Object ...objs) {
        String[] strs = new String[objs.length];

        for (int i = 0; i < objs.length; i++) {
            strs[i] = objs[i].toString();
        }

        if (ENABLED) {
            System.out.println(Thread.currentThread().getName() + " : " +  String.join(" ", strs));
        }
    }

}
