package cn.net.rjnetwork.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.net.rjnetwork.config.Info;
import cn.net.rjnetwork.util.HtmlDealUtil;
import cn.net.rjnetwork.util.TemplateRenderUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author huzhenjie
 * @email huzhenjie@rjnetwork.net.cn
 * @date 2024/3/11 10:37
 * @desc
 */
@Component
@Slf4j
public class YuQueManager {

    @Autowired
    ChromeManager chromeManager;

    private static ConcurrentHashMap<String, String> titleMap = new ConcurrentHashMap<>();

    @Autowired
    Info info;

    private  Integer documentIndex = 0;

    private Integer loopIndex = 0;

    WebElement moveElement = null;

    public void download(String url) throws InterruptedException {
        log.info("开始下载语雀文档");

        WebDriver driver = chromeManager.getDriver();
        driver.manage().window().maximize();
        driver.get(url);
        //ReaderLayout-module_bookName_
        String title = getTitle(driver);
        log.info("获取的标题信息为{}",title);


        Boolean createDirFlag = false;
        //根据标题创建目录。
        if(!StrUtil.isBlankOrUndefined(title)){
            createDirFlag = createDir(title);
        }
        getDirectoryTree(driver,title);
        driver.close();

    }

    /**
     * 获取文档总标题
     *
     */
    private String getTitle(WebDriver driver)  {
        return driver.findElement(By.id("main-right-content"))
                .findElement(By.id("asideHead")).
                findElements(By.tagName("div")).get(2).getText();
    }

    /**
     * 根据标题创建目录
     *
     */
    private Boolean createDir(String title){
        String path = info.getYuequeDowmloaderBasePath() + title;
        if(!FileUtil.exist(path)){
            FileUtil.mkdir(path);
            log.info("创建目录成功{}",path);
        }
        return true;
    }

    /**
     * 获取左侧目录树
     *
     */
    private void getDirectoryTree(WebDriver driver,String title){
        //ant-tabs-content-holder
        WebElement webElement =  driver.findElement(By.className("ant-tabs-content-holder"));
        WebElement antTabsTabpaneActive = webElement.findElement(By.className("ant-tabs-tabpane-active"));
        WebElement larkVirtualTree = antTabsTabpaneActive.findElement(By.className("lark-virtual-tree"));
        //主div  第一层 代表是第一季标题。
        WebElement div =  larkVirtualTree.findElement(By.tagName("div"));
        //List<WebElement> webElements = new ArrayList<>();
       // WebElement needScrollElement = getNeedScrollElement(div);
        Long viewHeight = getViewHeight(driver,div);
        Boolean vv = exec(title,driver,div);
        while (vv){
            loopIndex++;
            scrollSlowPx(driver,moveElement,viewHeight.intValue());
            vv= exec(title,driver,div);
            if(loopIndex>info.getLoop()){
                vv = false;
            }
        }

       // parseWebElements(title,driver,webElements,i);
    }


    private WebElement getNeedScrollElement(WebElement element){
        List<WebElement> temps = element.findElements(By.tagName("a"));
        int size = temps.size();
        return temps.get(size-1);
    }

    private Boolean exec(String title,WebDriver driver,WebElement element){
        List<WebElement> temps = element.findElements(By.tagName("a"));
        //在这需要执行点击操作。
        parseWebElements(title,driver,temps);
        return true;
    }

    private void parseWebElements(String title,WebDriver driver,List<WebElement> webElements){

           for(WebElement wel:webElements){
            try{
                String leftTitle = wel.getText();
                log.info("执行点击操作");
                wel.click();
                log.info("开始休眠{}秒",info.getSleepTime()/1000);
                Thread.sleep(info.getSleepTime());
                if(StrUtil.isBlankOrUndefined(leftTitle)){
                    log.info("获取标题为空，则本次跳过");
                    continue;
                }
                if(titleMap.containsKey(leftTitle)){
                    continue;
                }else{
                    titleMap.put(leftTitle,leftTitle);
                }
                documentIndex++;
                var path = info.getYuequeDowmloaderBasePath() + title + "/" +documentIndex+"-"+leftTitle;
                log.info("创建目录成功{}",path);
                if(!FileUtil.exist(path)){
                    FileUtil.mkdir(path);
                }
                var imgPath = path + File.separator + "imgs";
                if(!FileUtil.exist(imgPath)){
                    FileUtil.mkdir(imgPath);
                }
                //获取到文章元素
                WebElement el = driver.findElement(By.className("article-content")).findElement(By.className("ne-viewer-body"));
                //将该元素滚动到可视高度
                Long height = getViewHeight(driver,el);
                scrollSlowPx(driver,el,height.intValue());


                String html = el.getAttribute("innerHTML");
                List<WebElement> imgs =  el.findElements(By.tagName("img"));
                for(WebElement img:imgs){
                    String src = img.getAttribute("src");
                    //把图片下载至本地。
                    String fileName =  RandomUtil.randomNumbers(10) +".png";
                    HttpUtil.downloadFile(src,imgPath+File.separator+fileName);
                    //将html对应的图片替换掉。
                    html = html.replace(src,"./imgs"+File.separator+fileName);
                }
                html = html.replace("<div class=\"ne-viewer-header\"><button type=\"button\" class=\"ne-ui-exit-max-view-btn\">返回文档</button></div>","");
                html = HtmlDealUtil.clearDisplayNone(html);
                HashMap<String,Object> data = new HashMap<>();
                data.put("content",html);
                html = TemplateRenderUtil.renderStr(data,"document.html");
                FileUtil.writeString(html,path+File.separator+leftTitle+".html","UTF-8");
                //图片处理。
                String content = el.getText();
                FileUtil.writeString(content,path+File.separator+leftTitle+".txt","UTF-8");
                log.info("写入文件成功{}",leftTitle);
                moveElement = wel;
            }catch (Exception e){
                log.error("元素报错 {}",e.getMessage(),e);
                continue;
            }
        }

    }

    private void scrollToBottom(WebDriver driver, WebElement element) {
        // 确保元素是可滚动的
        String scrollIntoViewCommand = "arguments[0].scrollIntoView(true);";
        ((JavascriptExecutor) driver).executeScript(scrollIntoViewCommand, element);

        // 滚动到底部
        String scrollToBottomCommand = "window.scrollBy(0, document.body.scrollHeight);";
        ((JavascriptExecutor) driver).executeScript(scrollToBottomCommand);
    }

    private Long getViewHeight(WebDriver driver, WebElement element){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long elementHeight = (Long) js.executeScript("return arguments[0].clientHeight;", element);
        return elementHeight;
    }

    private void scrollPx(WebDriver driver, WebElement element,Integer px){
            String scrollToPixel = "window.scrollBy(0, "+px+");";
            //滚动到元素顶部
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            js.executeScript(scrollToPixel);
        log.info("滚动了{}像素",px);

    }

    private void scrollSlowPx(WebDriver driver, WebElement element,Integer px)  {
            String scrollToPixel = null;
            //缓慢滑动到指定位置。
            //滚动到元素顶部
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            int spacing = px/20;
            int temp = spacing;
            for(int i =0;i<20;i++){
                scrollToPixel = "window.scrollBy(0, "+temp+");";
                temp = temp + spacing;
                js.executeScript(scrollToPixel);
                try{
                    TimeUnit.MILLISECONDS.sleep(100);
                }catch (Exception e){
                    log.error("休眠异常 {}",e.getMessage(),e);
                }
            }
        log.info("滚动了{}像素",temp);

    }

    private Boolean webPageIsComplete(WebDriver driver){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String readyState = (String) js.executeScript("return document.readyState;");
        log.info("网页加载状态 {}",readyState);
        if(readyState.equals("complete")){
            return false;
        }
        return true;
    }

    private void removeStyles(WebDriver driver){
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


}
