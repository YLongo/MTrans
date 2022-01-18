 ``` java
 import com.swjtu.lang.LANG;
 import com.swjtu.querier.Querier;
 import com.swjtu.trans.AbstractTranslator;
 import com.swjtu.trans.impl.GoogleTranslator;
 
 import java.util.List;
 
 public class Test {
     public static void main(String[] args) {
         Querier<AbstractTranslator> querierTrans = new Querier<>();                   // 获取查询器
 
         querierTrans.setParams(LANG.EN, LANG.ZH,"Happiness is a way station between too much and too little.");
 
         querierTrans.attach(new GoogleTranslator());                                  // 向查询器中添加 Google 翻译器
 
         List<String> result = querierTrans.execute();                                 // 执行查询并接收查询结果
 
         for (String str : result) {
             System.out.println(str);
         }
     }
 }
 ```

