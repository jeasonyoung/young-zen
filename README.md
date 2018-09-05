# 基于springboot2的私有仓库

## maven 添加
```
<project>
    <repositories>
        <repository>
            <id>young-repo</id>
            <name>young-repo</name>
            <url>https://raw.github.com/jeasonyoung/young-zen/master/maven-repo/</url>
        </repository>
    </repositories>
</project>

<dependencies>
    <dependency>
        <groupId>org.young</groupId>
        <artifactId>young-common</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
