/**
 * @(#)ExtensionManger.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.extension;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 掘金五阳
 */
@Service
public class ExtensionManager {


    @Autowired
    private ApplicationContext context;

    private Map<String, Object> extensionBeanMap = new HashMap<>();

    @Getter
    private Table<BizTypeEnum, String, List<Object>> bizExtensionMeta = HashBasedTable.create();

    @PostConstruct
    public void init() {
        String[] beanNames = context.getBeanNamesForAnnotation(ExtensionProvider.class);


        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Set<Class<?>> interfaces =
                    ClassUtils.getAllInterfacesForClassAsSet(bean.getClass());
            ExtensionProvider extension = AnnotationUtils.findAnnotation(bean.getClass(), ExtensionProvider.class);
            Route[] routes = extension.bizScenes();


            for (Class<?> anInterface : interfaces) {
                if (BaseExtension.class.isAssignableFrom(anInterface)) {
                    for (Route route : routes) {
                        for (SceneEnum scene : route.scenes()) {
                            String key = buildKey(anInterface, route.bizType().getCode(), scene.getValue());


                            Object value = extensionBeanMap.put(key, bean);
                            if (value != null) {
                                CommonLog.error("注册 Extension key:{}冲突", key);
                                throw new RuntimeException("注册 Extension 冲突");
                            }
                            CommonLog.info("注册 Extension key:{}, 接口:{}, 实现类:{}", key, anInterface.getSimpleName(), bean.getClass().getSimpleName());

                            List<Object> extensions = bizExtensionMeta.get(route.bizType(), anInterface.getSimpleName());
                            if (extensions == null) {
                                bizExtensionMeta.put(route.bizType(), anInterface.getSimpleName(), Lists.newArrayList(bean));
                            }
                        }
                    }
                }
            }
        }
    }

    private String buildKey(Class<?> anInterface, int bizType, String scene) {
        String key = String.format("%s_%s_%s", anInterface.getSimpleName(), bizType, scene);
        return key;
    }

    public <T> T getExtension(BizScene bizScene, Class<T> tClass) {
        if (!tClass.isInterface()) {
            throw new RuntimeException(String.format("%s 需要是一个接口", tClass.getSimpleName()));
        }
        if (!BaseExtension.class.isAssignableFrom(tClass)) {
            throw new RuntimeException(String.format("%s 需要继承 BaseExtension 接口", tClass.getSimpleName()));
        }

        String key = buildKey(tClass, bizScene.getBizType(), bizScene.getScene());
        T value = (T) extensionBeanMap.get(key);

        if (value == null) {
            key = buildKey(tClass, bizScene.getBizType(), SceneEnum.DEFAULT_SCENE.getValue());
            value = (T) extensionBeanMap.get(key);
        }
        if (value == null) {
            key = buildKey(tClass, BizTypeEnum.DEFAULT.getCode(), bizScene.getScene());
            value = (T) extensionBeanMap.get(key);
        }

        if (value == null) {
            key = buildKey(tClass, BizTypeEnum.DEFAULT.getCode(), SceneEnum.DEFAULT_SCENE.getValue());
            value = (T) extensionBeanMap.get(key);
        }

        if (value == null) {
            throw new RuntimeException(String.format("%s 没有找到实现类%s", tClass.getSimpleName(), bizScene.getKey()));
        }
        return value;
    }

    public BizSceneBuildExtension getSceneExtension(BizScene bizScene) {
        return getExtension(bizScene, BizSceneBuildExtension.class);
    }

}