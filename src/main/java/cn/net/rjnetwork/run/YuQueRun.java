package cn.net.rjnetwork.run;

import cn.net.rjnetwork.config.Info;
import cn.net.rjnetwork.manager.YuQueManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @auther huzhenjie
 * @email huzhenjie@rjnetwork.net.cn
 * @date 2024/3/11 10:39
 * @desc
 */
@Component
public class YuQueRun implements ApplicationRunner {

    @Autowired
    YuQueManager yuQueManager;

    @Autowired
    Info info;
    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        //https://www.yuque.com/a00yangguang/au36hm
        //https://www.yuque.com/starblues/spring-brick-3.0.0
        yuQueManager.download(info.getDownloadUrl());
    }
}
