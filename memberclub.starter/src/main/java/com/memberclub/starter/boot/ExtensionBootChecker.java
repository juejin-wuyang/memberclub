/**
 * @(#)BootExtensionChecker.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.boot;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.domain.common.BizTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.extension.bootcheck", havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "memberclub.extension")
public class ExtensionBootChecker {

    public static final Logger LOG = LoggerFactory.getLogger(ExtensionBootChecker.class);
    private static volatile boolean run = false;
    private final Set<Class<?>> classSet;
    private final Map<String, ProtocolHandler> handlerMap;
    @Setter
    @Getter
    private boolean bootcheck;
    private List<BizTypeEnum> checkBizs = Lists.newArrayList(
            BizTypeEnum.DEMO_MEMBER,
            BizTypeEnum.DOUYIN_COUPON_PACKAGE
    );
    @Autowired
    private ExtensionManager extensionManager;


    public ExtensionBootChecker() {
        classSet = new HashSet<>();
        handlerMap = new HashMap<>();
        //注册一个文件扫描器
        FileProtocolHandler fileProtocolHandler = new FileProtocolHandler();
        //注册一个jar包扫描器
        JarProtocolHandler jarProtocolHandler = new JarProtocolHandler();
        handlerMap.put(fileProtocolHandler.handleProtocol(), fileProtocolHandler);
        handlerMap.put(jarProtocolHandler.handleProtocol(), jarProtocolHandler);
    }

    public static List<Class<?>> scanPackage(String basePackage) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);


        // 添加一个过滤器来查找带有特定注解的类
        provider.addIncludeFilter(new AnnotationTypeFilter(ExtensionConfig.class, false, true));

        // 设置扫描的基础包
        //provider.setResourceLoader(null);
        //provider.setResourcePattern("**/*.class");

        List<Class<?>> list = Lists.newArrayList();


        // 扫描指定的包
        provider.findCandidateComponents(basePackage).forEach(beanDefinition -> {
            // 获取类的全限定名
            String className = beanDefinition.getBeanClassName();
            try {
                // 使用反射加载类
                Class<?> clazz = Class.forName(className);
                // 处理类，例如: 打印类名
                list.add(clazz);
            } catch (Exception e) {
                LOG.error("扫描包异常:{} className:{}", basePackage, className, e);
            }
        });
        return list;
    }

    @PostConstruct
    public void init() {
        if (!run) {
            run = true;
        }
        Set<Class<?>> classes = scan("com.memberclub");
        List<String> errorMessages = Lists.newArrayList();
        for (Class<?> clazz : classes) {
            if (!clazz.isInterface()) {
                continue;
            }
            ExtensionConfig extensionConfig = clazz.getDeclaredAnnotation(ExtensionConfig.class);
            if (extensionConfig == null) {
                continue;
            }


            if (!extensionConfig.must()) {
                continue;
            }
            for (BizTypeEnum checkBiz : checkBizs) {
                List<Object> extensions = extensionManager.getBizExtensionMeta().get(checkBiz, clazz.getSimpleName());
                if (extensions != null) {
                    LOG.info("扫描到 Extension接口 {}, biz:{} 有扩展点:{} 个", clazz.getSimpleName(), checkBiz, extensions.size());
                } else {
                    extensions = extensionManager.getBizExtensionMeta().get(BizTypeEnum.DEFAULT, clazz.getSimpleName());
                    if (extensions != null) {
                        LOG.info("扫描到 Extension接口 {}, biz:{} 无扩展点, 但默认业务线有扩展点:{} 个",
                                clazz.getSimpleName(), checkBiz, extensions.size());
                        continue;
                    }
                    LOG.error("扫描到 Extension接口 {}, biz:{} 没有扩展点, 请关注", clazz.getSimpleName(), checkBiz);
                    errorMessages.add(String.format("启动异常, 扫描到 Extension接口 %s, biz:%s 没有扩展点, 请关注",
                            clazz.getSimpleName(), checkBiz));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            for (String errorMessage : errorMessages) {
                LOG.error(errorMessage);
            }
            throw new RuntimeException(String.format("缺少扩展点实现"));
        }
    }

    public Set<Class<?>> scan(String... basePackages) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        for (String basePackage : basePackages) {
            //将com.aa.bb 替换成 com/aa/bb
            String resourceName = basePackage.replace('.', '/') + "/";
            Enumeration<URL> resources = null;
            try {
                //通过classLoader获取所有的resources
                resources = classLoader.getResources(resourceName);
            } catch (IOException e) {
                LOG.error("解析包名", e);
            }
            if (resources == null) {
                continue;
            }
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                //根据url中protocol类型查找适用的解析器
                ProtocolHandler protocolHandler = handlerMap.get(protocol);
                if (protocolHandler == null) {
                    throw new RuntimeException("need support protocol [" + protocol + "]");
                }
                protocolHandler.handle(basePackage, url);
            }
        }
        return classSet;
    }

    /**
     * 将class添加到结果中
     *
     * @param classFullName 形如com.aa.bb.cc.Test.class的字符串
     */
    private void addResult(String classFullName) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(classFullName.substring(0, classFullName.length() - 6));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (aClass != null) {
            classSet.add(aClass);
        }
    }

    /**
     * 检查一个文件名是否是class文件名
     *
     * @param fileName 文件名
     * @return
     */
    private boolean checkIsNotClass(String fileName) {
        //只要class类型的文件
        boolean isClass = fileName.endsWith(".class");
        if (!isClass) {
            return true;
        }
        //排除内部类
        return fileName.indexOf('$') != -1;
    }

    /**
     * 协议处理器
     */
    private interface ProtocolHandler {
        /**
         * 适配的协议
         *
         * @return
         */
        String handleProtocol();

        /**
         * 处理url，最后需要调用{@link #addResult(String)}将结果存储到result中
         *
         * @param url
         */
        void handle(String basePackage, URL url);
    }

    /**
     * jar包解析器
     */
    private class JarProtocolHandler implements ProtocolHandler {

        @Override
        public String handleProtocol() {
            return "jar";
        }

        @Override
        public void handle(String basePackage, URL url) {
            try {
                String resourceName = basePackage.replace('.', '/') + "/";
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                JarFile jarFile = conn.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    //遍历jar包中的所有项
                    JarEntry jarEntry = entries.nextElement();
                    String entryName = jarEntry.getName();
                    if (!entryName.startsWith(resourceName)) {
                        continue;
                    }
                    if (checkIsNotClass(entryName)) {
                        continue;
                    }
                    String classNameFullName = entryName.replace('/', '.');
                    addResult(classNameFullName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件解析器
     */
    private class FileProtocolHandler implements ProtocolHandler {

        @Override
        public String handleProtocol() {
            return "file";
        }

        @Override
        public void handle(String basePackage, URL url) {
            File rootFile = new File(url.getFile());
            findClass(rootFile, File.separator + basePackage.replace('.', File.separatorChar) + File.separator);
        }

        /**
         * 递归的方式查找class文件
         *
         * @param rootFile    当前文件
         * @param subFilePath 子路径
         */
        private void findClass(File rootFile, String subFilePath) {
            if (rootFile == null) {
                return;
            }
            //如果是文件夹
            if (rootFile.isDirectory()) {
                File[] files = rootFile.listFiles();
                if (files == null) {
                    return;
                }
                for (File file : files) {
                    findClass(file, subFilePath);
                }
            }
            String fileName = rootFile.getName();
            if (checkIsNotClass(fileName)) {
                return;
            }
            String path = rootFile.getPath();
            int i = path.indexOf(subFilePath);
            String subPath = path.substring(i + 1);
            String fullClassPath = subPath.replace(File.separatorChar, '.');
            addResult(fullClassPath);
        }
    }
}