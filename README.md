# picocli-plus

命令行工具包`picocli`的增强版

## 使用

### 引入依赖
```xml
<dependency>
    <groupId>io.github.dudiao</groupId>
    <artifactId>picocli-plus</artifactId>
    <version>0.0.1</version>
</dependency>
```

### 实现接口

实现接口`PicocliPlusCliService`，按照`picocli`编写自己的命令行业务代码。

## 进阶使用

### 帮助命令
```shell
java -jar xxx.jar -h
```
输出如下：
```
Usage: PicocliPlus [-hV] [COMMAND]
常用工具集合
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  autoComplete  生成自动补全脚本
```

### 输出版本信息
```shell
java -jar xxx.jar -V
```
输出如下：
```
:: SpringBoot  :: v(3.0.0)
:: PicocliPlus :: v(0.0.1)
:: null :: v(null)
```

### 生成命令自动补全
```shell
java -jar xxx.jar autoComplete
```

### 自动升级
```shell
java -jar xxx.jar upgrade
```

