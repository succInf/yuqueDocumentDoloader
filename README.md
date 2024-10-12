# yuqueDocumentDoloader
### 项目介绍
语雀文档通过url地址访问，但是不支持导出，这很让人捉急，语雀上次崩溃，导致大量的文档无法正常访问，因此我开发了这个项目，
该项目主要是根据语雀的url地址来直接解析文档并把文本和html下载至本地，从而后续可离线查找对应的文档。
### 项目特点
 - 直接指定url 运行main方法即可下载

### 后续计划
- 界面可视化操作
- 批量下载
- 多线程批量下载
- html转md
- html转pdf
- html转word
- html转png
- ...
### 说明
- 版本 v0.0.1
- 依赖：jdk17 springboot3+ hutools enjoy chrome driver
- 功能： 下载语雀文档
### 如何使用
git clone 项目

cd 项目

修改 application.yml 文件
downloadUrl 属性为语雀文档的url
然后运行main方法即可
### 项目结构
- img 
- webs
- winPackage
- macPackage
- src/main/java
- src/main/java/cn/net/rjnetwork/YuqueDocumentDownloaderApplication.java
- src/main/java/cn/net/rjnetwork/config
- src/main/java/cn/net/rjnetwork/config/info.java
- src/main/java/cn/net/rjnetwork/manager/ChromeManager.java
- src/main/java/cn/net/rjnetwork/manager/YuQueManager.java
- src/main/java/cn/net/rjnetwork/utils
- src/main/java/cn/net/rjnetwork/utils/TemplateRenderUtil.java
- src/main/java/cn/net/rjnetwork/run
- src/main/java/cn/net/rjnetwork/run/YuQueRun.java
- src/main/resources
- src/main/resources/application.yml
- src/main/resources/templates
- src/main/resources/templates/document.html

### 支持作者
- 微信: worker_680
- 公众号：灰白黑680



