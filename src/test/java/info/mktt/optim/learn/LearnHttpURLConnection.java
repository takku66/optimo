package info.mktt.optim.learn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import static org.assertj.core.api.Assertions.assertThat;

import info.mktt.optimo.OptimoApplication;

@SpringBootTest(classes=OptimoApplication.class)
public class LearnHttpURLConnection {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    Environment environment;

    @Test
    void 接続がどのタイミングで発生しているかを知る() throws IOException, InterruptedException{

        // テスト用のため、Googleのサイトに接続してみる
        String url = "http://www.google.com/";

        List<String> beforeNewUrl = netstat();

        // URLオブジェクトの生成による変化を見る
        URL urlObj = new URL(url);
        List<String> urlCreated = netstat();
        Set<String> diffUrlCreate = extractDiff( new HashSet<String>(beforeNewUrl), new HashSet<String>(urlCreated));
        System.out.println("URLオブジェクトの生成による変化を見る");
        System.out.println(diffUrlCreate.toString());

        // openConnectionメソッド実行による変化を見る
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        List<String> connectionOpened = netstat();
        Set<String> diffConnectionOpen = extractDiff( new HashSet<String>(urlCreated), new HashSet<String>(connectionOpened));
        System.out.println("openConnectionメソッド実行による変化を見る");
        System.out.println(diffConnectionOpen.toString());

        // リクエストを送信することによる変化を見る
        con.connect();
        List<String> connected = netstat();
        Set<String> diffConnect = extractDiff( new HashSet<String>(connectionOpened), new HashSet<String>(connected));
        System.out.println("リクエストを送信することによる変化を見る");
        System.out.println(diffConnect.toString());

        // 接続を解除することによる変化を見る
        con.disconnect();
        List<String> disconnected = netstat();
        Set<String> diffDisconnect = extractDiff( new HashSet<String>(connected), new HashSet<String>(disconnected));
        System.out.println("接続を解除することによる変化を見る");
        System.out.println(diffDisconnect.toString());

    }

    @Test
    void connectやgetResponseCode_getInputStreamでリクエストの発生有無を調べる() throws IOException, InterruptedException{

        // テスト用のため、Googleのサイトに接続してみる
        String url = "http://www.google.com/";
        String ip = "172.19.147.110";

        // URLオブジェクトの生成による変化を見る
        URL url1 = new URL(url);
        URL url2 = new URL(url);
        URL url3 = new URL(url);

        HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
        HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
        HttpURLConnection con3 = (HttpURLConnection) url3.openConnection();

        List<String> beforeGetUrl = netstat();
        con1.getURL();
        List<String> afterGetUrl = netstat();
        System.out.println("con1");
        Set<String> urlGetted = extractDiff( new HashSet<String>(beforeGetUrl), new HashSet<String>(afterGetUrl));
        System.out.println(urlGetted.toString());
        assertThat(urlGetted.contains(ip)).isEqualTo(false);


        con1.connect();
        List<String> connected1 = netstat();
        con1.disconnect();
        List<String> disconnected1 = netstat();
        System.out.println("con1");
        Set<String> diffDisconnect1 = extractDiff( new HashSet<String>(connected1), new HashSet<String>(disconnected1));
        System.out.println(diffDisconnect1.toString());
        assertThat(diffDisconnect1.toString().contains(ip)).isEqualTo(true);


        con2.getResponseCode();
        List<String> connected2 = netstat();
        con2.disconnect();
        List<String> disconnected2 = netstat();
        System.out.println("con2");
        Set<String> diffDisconnect2 = extractDiff( new HashSet<String>(connected2), new HashSet<String>(disconnected2));
        System.out.println(diffDisconnect2.toString());
        assertThat(diffDisconnect2.toString().contains(ip)).isEqualTo(true);


        con3.getInputStream();
        List<String> connected3 = netstat();
        con3.disconnect();
        List<String> disconnected3 = netstat();
        System.out.println("con3");
        Set<String> diffDisconnect3 = extractDiff( new HashSet<String>(connected3), new HashSet<String>(disconnected3));
        System.out.println(diffDisconnect3.toString());
        assertThat(diffDisconnect3.toString().contains(ip)).isEqualTo(true);


    }

    private List<String> netstat() throws IOException, InterruptedException{

        var connectionList = new ArrayList<String>();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("netstat", "-an");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                connectionList.add(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            throw e;
        }
        return connectionList;
    }
    
    private <T> Set<T> extractDiff(Set<T> set1, Set<T> set2 ){
        final List<T> resultList = set1.stream()
                .filter(p -> {
                    return (! set2.contains(p));
                })
                .collect(Collectors.toList());
        return new HashSet<T>(resultList);
    }

}
