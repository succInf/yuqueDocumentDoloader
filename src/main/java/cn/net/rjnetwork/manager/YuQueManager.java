package cn.net.rjnetwork.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.net.rjnetwork.config.Info;
import cn.net.rjnetwork.util.HtmlDealUtil;
import cn.net.rjnetwork.util.ScrollUtil;
import cn.net.rjnetwork.util.TemplateRenderUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private static ConcurrentHashMap<String, String> endMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> titleMap = new ConcurrentHashMap<>();

    @Autowired
    Info info;

    private  Integer documentIndex = 0;
    WebElement moveElement = null;

    @Value("${yueque.cookies}")
    private String cookies;

    public void download(String url) throws InterruptedException {
        log.info("开始下载语雀文档");

        WebDriver driver = chromeManager.getDriver();
        driver.manage().window().maximize();
        //设置浏览器指纹特征，防止反爬机制
        driver.get(url);
        if(!StrUtil.isBlankOrUndefined(cookies)){
            //cookies不为空 开始设置cookies。
            String[] ckes = cookies.split(";");
            for(String ck : ckes){
                ck = ck.trim();
                String[] aa = ck.split("=");
                if(aa.length == 2){
                    Cookie cookie = new Cookie(aa[0], aa[1]);
                    driver.manage().addCookie(cookie);
                }
            }


        }
        String title = getTitle(driver);
        log.info("获取的标题信息为{}",title);
        Boolean createDirFlag = false;
        //根据标题创建目录。
        if(!StrUtil.isBlankOrUndefined(title)){
            createDirFlag = createDir(title);
        }
        if(createDirFlag){
            getDirectoryTree(driver,title);
        }
        //先关闭 在退出。
        log.info("执行完毕，准备退出...");
        driver.close();
        driver.quit();
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
        //主div  第一层 代表是第一级标题。
        WebElement div =  larkVirtualTree.findElement(By.tagName("div"));
        Long viewHeight = ScrollUtil.getViewHeight(driver,div);
        boolean vv = exec(title,driver,div);
        //如何判断结束的标志。
        while (vv){
            //将元素滚动到底部，确保所有元素均可见。
            ScrollUtil.scrollSlowPx(driver,moveElement,viewHeight.intValue());
            vv= exec(title,driver,div);
        }
    }


    private WebElement getNeedScrollElement(WebElement element){
        List<WebElement> temps = element.findElements(By.tagName("a"));
        int size = temps.size();
        return temps.get(size-1);
    }

    private Boolean exec(String title,WebDriver driver,WebElement element){
        //获取左侧已显示的所有a标签元素信息。如果取到的a标签为空则代表 已经结束。
        List<WebElement> temps = element.findElements(By.tagName("a"));
        if(temps==null || temps.isEmpty()){
            return false;
        }
        if(isEnd(temps)){
            return false;
        }
        //在这需要执行点击操作。循环a标签并进行点击操作，从而获取右侧内容。
        //这里也需要判断是否已经爬取完毕。
        parseWebElements(title,driver,temps);
        return true;
    }



    private boolean isEnd( List<WebElement> temps){
        StringBuffer sb = new StringBuffer();
        temps.stream().peek((k)->{
            sb.append(k.getText());
        }).collect(Collectors.toList());
        //endMap.put()
       String md5 =  SecureUtil.md5(sb.toString());
       log.info("md5为{}",md5);
       if(endMap.containsKey(md5)){
           return true;
       }else{
           endMap.put(md5,sb.toString());
           return false;
       }
    }

    private void parseWebElements(String title,WebDriver driver,List<WebElement> webElements){
           for(WebElement wel:webElements){
            try{
                String leftTitle = wel.getText();
                log.info("执行点击操作");
                wel.click();
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
                //代表序号。
                documentIndex++;
                //组装本地文件所在地址。
                var path = info.getYuequeDowmloaderBasePath() + title + "/" +documentIndex+"-"+leftTitle;
                log.info("创建目录成功{}",path);
                if(!FileUtil.exist(path)){
                    FileUtil.mkdir(path);
                }
                //组装图片本地地址。
                var imgPath = path + File.separator + "imgs";
                if(!FileUtil.exist(imgPath)){
                    FileUtil.mkdir(imgPath);
                }
                //获取到文章元素
                WebElement el = driver.findElement(By.className("article-content")).findElement(By.className("ne-viewer-body"));
                //将该元素滚动到可视高度
                Long height = ScrollUtil.getViewHeight(driver,el);
                ScrollUtil.scrollSlowPx(driver,el,height.intValue(),1);

                //scrollTop(driver,el);
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
                log.error("已执行到最后一项，即将关闭...");
            }
        }

    }

}
