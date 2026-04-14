package id.rahmat.projekakhir.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AppExecutors {

    private static final ExecutorService IO = Executors.newFixedThreadPool(4);

    private AppExecutors() {
    }

    public static ExecutorService io() {
        return IO;
    }
}
