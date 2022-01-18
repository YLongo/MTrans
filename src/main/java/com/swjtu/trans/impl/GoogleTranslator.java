package com.swjtu.trans.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swjtu.lang.LANG;
import com.swjtu.trans.AbstractTranslator;
import javafx.util.Pair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GoogleTranslator extends AbstractTranslator {

    private static final String url = "https://translate.google.com/translate_a/single";

    private final List<FormData> formDataList = new ArrayList<>();
    
    public GoogleTranslator(){
        super(url);
    }


    @Override
    public void setLangSupport() {
        langMap.put(LANG.ZH, "zh-CN");
        langMap.put(LANG.EN, "en");
        langMap.put(LANG.JP, "ja");
        langMap.put(LANG.KOR, "ko");
        langMap.put(LANG.FRA, "fr");
        langMap.put(LANG.RU, "ru");
        langMap.put(LANG.DE, "de");
    }

    @Override
    public void setFormData(LANG from, LANG to, String text) {
        formDataList.add(new FormData("client", "webapp"));
        formDataList.add(new FormData("sl", langMap.get(from)));
        formDataList.add(new FormData("tl", langMap.get(to)));
        formDataList.add(new FormData("dt", "at"));
        formDataList.add(new FormData("dt", "bd"));
        formDataList.add(new FormData("dt", "ex"));
        formDataList.add(new FormData("dt", "ld"));
        formDataList.add(new FormData("dt", "md"));
        formDataList.add(new FormData("dt", "qca"));
        formDataList.add(new FormData("dt", "rw"));
        formDataList.add(new FormData("dt", "rm"));
        formDataList.add(new FormData("dt", "ss"));
        formDataList.add(new FormData("dt", "t"));
        formDataList.add(new FormData("otf", "2"));
        formDataList.add(new FormData("ssel", "0"));
        formDataList.add(new FormData("tsel", "0"));
        formDataList.add(new FormData("kc", "1"));
        formDataList.add(new FormData("tk", token(text)));
        formDataList.add(new FormData("q", text));
    }

    @Override
    public String query() throws Exception {
        URIBuilder uri = new URIBuilder(url);
        for (FormData formData : formDataList) {
            uri.addParameter(formData.getKey(), formData.getValue());
        }
        HttpUriRequest request = new HttpGet(uri.toString());
        
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        String result = EntityUtils.toString(entity, "utf-8");

        EntityUtils.consume(entity);
        response.getEntity().getContent().close();
        response.close();

        return result;
    }

    @Override
    public String parses(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(text);
        return jsonNode.get(0).get(0).get(0).toString();
    }

    public String token(String text) {
        String tk = "";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        try {
            FileReader reader = new FileReader("./tk/Google.js");
            engine.eval(reader);

            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable)engine;
                tk = String.valueOf(invoke.invokeFunction("token", text));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }

    private static class FormData {
        private String key;
        private String value;

        public FormData(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
