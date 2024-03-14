package cn.net.rjnetwork.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author huzhenjie
 * @email huzhenjie@rjnetwork.net.cn
 * @date 2024/3/14 09:58
 * @desc
 */
@Slf4j
public class ScrollUtil {

    public static void scrollSlowPx(WebDriver driver, WebElement element, Integer px)  {
        String scrollToPixel = null;
        int size = 200;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        int spacing = px/size;
        int temp = spacing;
        for(int i =0;i<size;i++){
            scrollToPixel = "window.scrollBy(0, "+temp+");";
            temp = temp + spacing;
            js.executeScript(scrollToPixel);
        }
        log.info("滚动了{}像素",temp);
    }

    public static String scrollSlowPx(WebDriver driver, WebElement element, Integer px,Integer type)  {
        // px 视窗总高度
        String scrollToPixel = "window.scrollBy(0, "+px/2+");";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        js.executeScript(scrollToPixel);
        try{
            TimeUnit.SECONDS.sleep(2);
        }catch (Exception e){

        }
        return null;
    }

    public static void scrollTop(WebDriver driver,WebElement element){
        String jj = "window.scrollTo(0, 0);";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(jj,element);
    }

    public static void scrollToBottom(WebDriver driver, WebElement element) {
        // 确保元素是可滚动的
        String scrollIntoViewCommand = "arguments[0].scrollIntoView(true);";
        ((JavascriptExecutor) driver).executeScript(scrollIntoViewCommand, element);
        // 滚动到底部
        String scrollToBottomCommand = "window.scrollBy(0, document.body.scrollHeight);";
        ((JavascriptExecutor) driver).executeScript(scrollToBottomCommand);
    }

    public static Long getViewHeight(WebDriver driver, WebElement element){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long elementHeight = (Long) js.executeScript("return arguments[0].clientHeight;", element);
        return elementHeight;
    }

    public static void removeStyles(WebDriver driver){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        var jj = "var styles = document.getElementsByTagName('style');\n" +
                " \n" +
                "// 循环遍历并移除\n" +
                "for (var i = styles.length - 1; i >= 0; i--) {\n" +
                "    var style = styles[i];\n" +
                "    style.parentNode.removeChild(style);\n" +
                "}";
        js.executeScript(jj);
    }

    public static Boolean webPageIsComplete(WebDriver driver){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String readyState = (String) js.executeScript("return document.readyState;");
        log.info("网页加载状态 {}",readyState);
        if(readyState.equals("complete")){
            return false;
        }
        return true;
    }


}
