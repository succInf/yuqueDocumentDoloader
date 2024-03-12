package cn.net.rjnetwork.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author huzhenjie
 * @email huzhenjie@rjnetwork.net.cn
 * @date 2024/3/11 10:27
 * @desc
 */
@Data
@Configuration
public class Info implements Serializable {

    @Value("${yueque.yuequeDowmloaderBasePath}")
    private String yuequeDowmloaderBasePath;
    @Value("${yueque.chromedriver}")
    private String chromedriver;
    @Value("${yueque.chrome}")
    private String chrome;
    @Value("${yueque.sleepTime}")
    private Integer sleepTime;
    @Value("${yueque.movePx}")
    private Integer movePx;
    @Value("${yueque.scrollSleepTime}")
    private Integer scrollSleepTime;
    @Value("${yueque.loop}")
    private Integer loop;
    @Value("${yueque.downloadUrl}")
    private String downloadUrl;
}
