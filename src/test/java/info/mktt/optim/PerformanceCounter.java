package info.mktt.optim;

import java.util.function.BiFunction;

public class PerformanceCounter {

    public static <T, U, R> R execute(BiFunction<T, U, R> func, T t, U u){
        long start = System.currentTimeMillis();

        R r = func.apply(t, u);
        
        long end = System.currentTimeMillis();
        System.out.println((end - start)  + "ms");
        return r;
    }
}
