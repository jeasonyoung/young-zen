# 基于springboot2的私有仓库

## maven 添加
```
<project>
    <repository>
        <id>young-repo</id>
        <name>young-repo</name>
        <url>https://raw.github.com/jeasonyoung/young-zen/maven-repo/</url>
    </repository>
</project>

<dependencies>
    <dependency>
        <groupId>org.young</groupId>
        <artifactId>young-common</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
