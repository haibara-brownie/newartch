[ENGLISH](./README_EN.md)|简体中文

## 相关简介

1. [后端开发指南](https://yunzhihui.feishu.cn/wiki/No28wwVFIiiIs5kTQb8cIHppnKg)
2. [必读规范-后端开发规范](https://yunzhihui.feishu.cn/wiki/VVG0wNqxwiecjIk40gVc9uZ5nOh)
3. [必读规范-依赖包引用规范](https://yunzhihui.feishu.cn/wiki/IrdswpSzPiFjH1kPKBucfT42nAh)
4. [必读规范-API设计规范](https://yunzhihui.feishu.cn/wiki/VFB0wm4s8iu5FekjuK8cakVznLX)
5. [必读规范-dubbo接口规范](https://yunzhihui.feishu.cn/wiki/VElgwSYeMibV9WkxCuucsug4nkd)
6. [必读规范-Git操作及分支管理规范](https://yunzhihui.feishu.cn/wiki/ARLgwTbwliyjA5kRra1cgivfnBg)
7. [必读规范-源代码风格规范](https://yunzhihui.feishu.cn/wiki/OB2QwCjdpizvG8kbVgZcR0gen0f)
8. [必读规范-日志输出规范](https://yunzhihui.feishu.cn/wiki/IxFAwmS73iUhfBkjW7Kcghe0nfe)
9. [运维工具包 OMP - 产品 & 服务包制作规范](https://yunzhihui.feishu.cn/docx/DgeedAHHHoD7h0x3Ul5c7XI6nub)
10. [运维工具包 OMP - 产品 & 服务 yaml 和脚本样例](https://yunzhihui.feishu.cn/docx/Nstqdppg7orpcHxYp39cyg22nnc)

## 支持服务及构建方式

1. 编译构建项目

```shell
mvn clean -f pom.xml
mvn clean package -Dmaven.test.skip=true -am -U -e -f pom.xml
```