1. 编译构建项目

```shell
mvn clean -f pom.xml
mvn clean package -Dmaven.test.skip=true -am -U -e -f pom.xml
```