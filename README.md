汇付接口文档
```
https://api.cloudpnr.com/appplus/user.html
```
安装汇付jar包
```bash
mvn install:install-file -DgroupId=com.huifu.saturn -DartifactId=saturn-cfca -Dversion=1.0.6 -Dpackaging=jar  -Dfile=D:/huifu/saturn-cfca-1.0.6.jar
mvn install:install-file -DgroupId=com.huifu.cfca -DartifactId=cryptokit.jni -Dversion=1.0 -Dpackaging=jar  -Dfile=D:/huifu/cryptokit.jni-1.0.jar
mvn install:install-file -DgroupId=com.huifu.cfca -DartifactId=sadk -Dversion=3.2.0.8-unlimit -Dpackaging=jar  -Dfile=D:/huifu/sadk-3.2.0.8-unlimit.jar
mvn install:install-file -DgroupId=com.huifu.cfca -DartifactId=sansec.swxajce -Dversion=2.1.3 -Dpackaging=jar  -Dfile=D:/huifu/sansec.swxajce-2.1.3.jar

mvn install:install-file -DgroupId=org.jodd -DartifactId=jodd-http -Dversion=3.7.0-20150904 -Dpackaging=jar  -Dfile=D:/huifu/jodd-http-3.7.0-20150904.jar
mvn install:install-file -DgroupId=org.jodd -DartifactId=jodd-core -Dversion=3.7.0-20150904 -Dpackaging=jar  -Dfile=D:/huifu/jodd-core-3.7.0-20150904.jar
mvn install:install-file -DgroupId=org.jodd -DartifactId=jodd-upload -Dversion=3.7.0-20150904 -Dpackaging=jar  -Dfile=D:/huifu/jodd-upload-3.7.0-20150904.jar

```
