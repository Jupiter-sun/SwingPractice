简易Swing成绩管理系统.

## 事前准备
- 设置数据库
    1. 表结构在 [这里](db/src/main/resources/sql/init.ddl.sql)
    2. 测试数据在 [这里](db/src/main/resources/sql/dump.sql)
    3. 注意数据库中的用户密码，是MD5摘要结果，并用用户ID加盐
- 编写数据库连接配置文件
    1. 参照 [配置文件样例](db/src/main/resources/app.properties)
    2. 如果使用不同数据库，注意添加驱动到ClassPath
    3. 文件放在执行路径下($CWD)
    4. Main函数的第一个参数是配置文件路径

## 如何构建

```bash
./gradlew installDist
```

## 如何运行

```bash
cd ui/build/install/ui
bin/ui
```
