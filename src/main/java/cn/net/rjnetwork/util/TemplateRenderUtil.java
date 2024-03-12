package cn.net.rjnetwork.util;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.extra.template.engine.enjoy.EnjoyEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板渲染处理类
 *
 * */
public class TemplateRenderUtil {

  private static    TemplateEngine engine = null;
  private static void init(){
      if(engine == null){
          TemplateConfig templateConfig =  new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH);
          templateConfig.setCustomEngine(EnjoyEngine.class);
          engine = TemplateUtil.createEngine(templateConfig);
      }
  }

  /**
   * 渲染enjoy模板
   *
   */
  public static String renderStr(HashMap<String,Object> data,String templatePath){
      init();
      Template template = engine.getTemplate(templatePath);
      return template.render(data);
  }

    /**
     * 渲染enjoy模板
     *
     */
    public static String renderStr(Map<String,Object> data, String templatePath){
        init();
        Template template = engine.getTemplate(templatePath);
        return template.render(data);
    }

    /**
     * 渲染enjoy模板
     *
     */
    public static String renderTxt(Map<String,Object> data, String text){
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig());
        Template template = engine.getTemplate(text);
        return template.render(data);
    }


    public static void main(String[] args) {
        HashMap<String,Object> map = new HashMap();
        map.put("templateId","111");
        TemplateRenderUtil.renderStr(map,null);
    }
}
