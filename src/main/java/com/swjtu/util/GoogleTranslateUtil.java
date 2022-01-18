package com.swjtu.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swjtu.trans.impl.GoogleTranslator;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleTranslateUtil {
    private static GoogleTranslateUtil googleTranslateUtil = new GoogleTranslateUtil();

    public static GoogleTranslateUtil getInstance() {
        return googleTranslateUtil;
    }

    private GoogleTranslateUtil() {
    }

    String url = "https://translate.google.cn/translate_a/single";
    String tkk = "434674.96463358"; // 随时都有可能需要更新的TKK值

    public long uo(long a, String b) {
        long retDDD = 0L;
        for (int c = 0; c < b.length() - 2; c += 3) {
            char d = b.charAt(c + 2);
            int retD = 97 <= d ? (int) (d) - 87 : (int) (d);
            long retDD = 47 == b.charAt(c + 1) ? a >>> retD : a << retD;
            retDDD = 47 == b.charAt(c) ? a + retDD & 4294967295L : a ^ retDD;
        }
        return retDDD;
    }

//
//    public long wo(String a, String tkk) {
//        String[] d = tkk.split(".");
//        int b = Integer.parseInt(d[0]);
//        Map<Object,Integer> e = new HashMap();
//        int f = 0;
//        int g = 0;
//        for (; g < a.length(); g++) {
//            char h = a.charAt(g);
//            if (128 > h) {
//                e.put(f++, h);
//            } else if (2048 > h) {//(2048 > h ? e[f++] = h >> 6 | 192 :
//                e.put(f++, h >> 6 | 192);
//            } else if (55296 == (h & 64512) && g + 1 < a.length() && 56320 == (a.charAt(g + 1) & 64512)) {//(55296 == (h & 64512) && g + 1 < a.length && 56320 == (a.charCodeAt(g + 1) & 64512) ? (h = 65536 + ((h & 1023) << 10) + (a.charCodeAt(++g) & 1023), e[f++] = h >> 18 | 240, e[f++] = h >> 12 & 63 | 128) :
//                int retH = 65536 + ((h & 1023) << 10) + (a.charAt(++g) & 1023);
//                e.put(f++, retH >> 18 | 240);
//                e.put(f++, retH >> 12 & 63 | 128);
//            }else{
//                e.put(f++, h >> 12 | 224);
//                e.put(f++, h >> 6 & 63 | 128);
//                e.put(f++, h & 63 | 128);
//            }
//        }
//       long retA = b;
//        for (f = 0; f < e.size(); f++){
//            retA += e.get(f);
//            retA = uo(retA, "+-a^+6");
//        }
//
//        long retAA = uo(retA, "+-3^+b+-f");
//        a ^= (Integer.parseInt(d[1])) || 0;
//        0 > a && (a = (a & 2147483647) + 2147483648);
//        a %= 1E6;
//        return (a.toString() + "." + (a ^ b))
//
//    }


    public static String sendGet(String url) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080));
            
            URLConnection connection = realUrl.openConnection(proxy);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("cookie", "NID=188=M1p_rBfweeI_Z02d1MOSQ5abYsPfZogDrFjKwIUbmAr584bc9GBZkfDwKQ80cQCQC34zwD4ZYHFMUf4F59aDQLSc79_LcmsAihnW0Rsb1MjlzLNElWihv-8KByeDBblR2V1kjTSC8KnVMe32PNSJBQbvBKvgl4CTfzvaIEgkqss");
            connection.setRequestProperty("referer", "https://translate.google.cn/");
            connection.setRequestProperty("x-client-data", "CJK2yQEIpLbJAQjEtskBCKmdygEIqKPKAQi5pcoBCLGnygEI4qjKAQjxqcoBCJetygEIza3KAQ==");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！");
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }
        return result.toString();
    }

    public String getTKK(String q) {
        try {
            String tkkUrl = "https://api.yooul.net" + q;
            String req = sendGet(tkkUrl);
            tkk = new JSONObject(req).getString("token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tkk;

    }

    public String update_TKK() {
        try {
            String tkkUrl = "https://translate.google.cn/";
            String req = sendGet(tkkUrl);
            Pattern pattern = Pattern.compile("tkk:'([0-9]+\\.[0-9]+)'");
            Matcher matcher = pattern.matcher(req);
            if (matcher.find()) {
                tkk = matcher.group(1);
                System.out.println("=======tkk=======" + tkk);
            }
        } catch (Exception e) {
            System.out.println("=====update_TKK======");
            e.printStackTrace();
        }
        return tkk;
    }

    public String construct_url(String tkk, String q, String from, String to) {
        try {
            String base = url + "?client=webapp&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&otf=2&ssel=0&tsel=0&kc=1&tk=" 
                    + tkk + "&q=" + q + "&sl=" + from + "&tl=" + to;
            System.out.println("=====base======" + base);
            return base;
        } catch (Exception e) {
            System.out.println("=====update_TKK======" + e.toString());
        }
        return null;
    }


    public String query(String q, String from, String to) {
        String ret = null;
        try {
            q = URLEncoder.encode(q, "UTF-8");
//            getTKK(q);
            GoogleTranslator googleTranslator = new GoogleTranslator();
            String token = googleTranslator.token(q);
            System.out.println("token:" + token);
            String baseUrl = construct_url(token, q, from, to);
            ret = sendGet(baseUrl);
            System.out.println("=====ret=====" + ret);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(ret);
            return jsonNode.get(0).get(0).get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }

    public static void main(String[] args) {
        GoogleTranslateUtil translateUtil = getInstance();
        String query = translateUtil.query("translate", "en", "zh-CN");
        System.out.println(query);
    }


}