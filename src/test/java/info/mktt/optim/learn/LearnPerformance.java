package info.mktt.optim.learn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import info.mktt.optim.PerformanceCounter;
import info.mktt.optimo.OptimoApplication;

@SpringBootTest(classes=OptimoApplication.class)
public class LearnPerformance {

    // プロパティファイルから設定値を取得する
    @Autowired
    Environment environment;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void ListとSetのcontainsにどの程度の性能差があるか調べる() {

        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        for(int i = 0; i < 10000; i++){
            list1.add(i);
        }
        for(int i = 5000; i < 25000; i++){
            list2.add(i);
        }

        // ListからSetに格納する処理も計測してみる
        long start = System.currentTimeMillis();
        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);
        long end = System.currentTimeMillis();
        System.out.println((end - start)  + "ms");

        // Listは線形探索となるため O(n)となり、(Hash)Setはキーマッチによる探索となるため O(1)となる。
        PerformanceCounter.execute(LearnPerformance::extractListDiff, list1, list2);
        PerformanceCounter.execute(LearnPerformance::extractSetDiff, set1, set2);

    }

    private static <T> Set<T> extractSetDiff(Set<T> set1, Set<T> set2 ){
        final List<T> resultList = set1.stream()
                .filter(p -> {
                    return (! set2.contains(p));
                })
                .collect(Collectors.toList());
        return new HashSet<T>(resultList);
    }

    private static <T> List<T> extractListDiff(List<T> list1, List<T> list2 ){
        final List<T> resultList = list1.stream()
                .filter(p -> {
                    return (! list2.contains(p));
                })
                .collect(Collectors.toList());
        return new ArrayList<T>(resultList);
    }
}
