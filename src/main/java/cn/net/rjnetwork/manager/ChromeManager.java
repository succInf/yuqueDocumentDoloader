package cn.net.rjnetwork.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.net.rjnetwork.config.Info;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huzhenjie
 * @email huzhenjie@rjnetwork.net.cn
 * @date 2024/3/11 10:29
 * @desc
 */
@Component
@Slf4j
public class ChromeManager {

    @Getter
    private WebDriver driver;

    @Autowired
    private Info info;
    @PostConstruct
    private void init(){
        ChromeOptions chromeOptions = new ChromeOptions();
        if(!StrUtil.isBlankOrUndefined(info.getChrome())){
            chromeOptions.setBinary(info.getChrome());
            System.setProperty("webdriver.chrome.bin", info.getChrome());
        }
        if(StrUtil.isBlankOrUndefined(info.getChromedriver())){
            throw new RuntimeException("谷歌驱动不存在，请检查配置");
        }
        System.setProperty("webdriver.chrome.driver",info.getChromedriver());
        //设置浏览器指纹信息。
        chromeOptions.setExperimentalOption("useAutomationExtension",false);
        chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        chromeOptions.addArguments("disable-blink-features");
        chromeOptions.addArguments("disable-blink-features=AutomationControlled");
        //添加参数 防止 403
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--no-sandbox");
        this.driver = new ChromeDriver(chromeOptions);
        try{
            //https://www.browserscan.net/zh 浏览器指纹检测网站。
            ChromeDriver chromeDriver = (ChromeDriver)driver;
            String stealthJs = FileUtil.readUtf8String(ChromeManager.class.getResource("/").getPath()+"/templates/stealth.min.js");
            Map<String, Object> params = new HashMap<>();
            params.put("script", stealthJs);
            params.put("runInPageContext", true);
            chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument",params);
        }catch (Exception e){
            log.error("设置浏览器指纹失败");
        }

    }

}
