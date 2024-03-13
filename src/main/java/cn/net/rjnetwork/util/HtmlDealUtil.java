package cn.net.rjnetwork.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @auther huzhenjie
 * @email huzhenjie@rjnetwork.net.cn
 * @date 2024/3/13 12:17
 * @desc html处理类
 */

public class HtmlDealUtil {

    /**
     * 清除html元素中所有的 display:none 属性
     *
     */
    public static String clearDisplayNone(String html) {
        return html.replaceAll("style","dataStyle");
    }

    public static void main(String[] args) {
        System.out.println(clearDisplayNone("<div style=\"display:none\">123</div>"));
    }
}
