package cn.net.rjnetwork.manager;

import cn.hutool.core.util.StrUtil;
import cn.net.rjnetwork.config.Info;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author huzhenjie
 * @email huzhenjie@rjnetwork.net.cn
 * @date 2024/3/11 10:29
 * @desc
 */
@Component
@Slf4j
public class ChromeManager {

    private ChromeOptions chromeOptions;

    private WebDriver driver;

    @Autowired
    private Info info;
    @PostConstruct
    private void init(){
        this.chromeOptions = new ChromeOptions();
        if(!StrUtil.isBlankOrUndefined(info.getChrome())){
            this.chromeOptions.setBinary(info.getChrome());
            System.setProperty("webdriver.chrome.bin", info.getChrome());
        }
        if(StrUtil.isBlankOrUndefined(info.getChromedriver())){
            throw new RuntimeException("谷歌驱动不存在，请检查配置");
        }
        System.setProperty("webdriver.chrome.driver",info.getChromedriver());
        //添加参数 防止 403
        this.chromeOptions.addArguments("--remote-allow-origins=*");
        this.chromeOptions.addArguments("--no-sandbox");
        this.driver = new ChromeDriver(chromeOptions);
    }

    public WebDriver getDriver(){
        return this.driver;
    }
}
