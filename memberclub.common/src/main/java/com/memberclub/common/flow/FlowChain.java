/**
 * @(#)FlowChain.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.flow;

import com.google.common.collect.Lists;
import com.memberclub.common.util.ApplicationContextUtils;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class FlowChain<T> {

    public List<FlowNode<T>> nodes = Lists.newArrayList();

    public List<Class<? extends FlowNode<T>>> clazzs = Lists.newArrayList();

    public FlowChainService flowChainService;

    private Class<T> contextClass;


    public static <T> FlowChain<T> newChain(FlowChainService flowChainService, Class<T> clazz) {
        FlowChain<T> chain = new FlowChain<>();
        chain.setContextClass(clazz);
        chain.setFlowChainService(flowChainService);
        return chain;
    }


    public static <T> FlowChain<T> newChain(Class<T> clazz) {
        FlowChain<T> chain = new FlowChain<>();
        chain.setContextClass(clazz);
        chain.setFlowChainService(ApplicationContextUtils.getContext().getBean(FlowChainService.class));
        return chain;
    }

    public void execute(T context) {
        ApplicationContextUtils.getContext().getBean(FlowChainService.class).execute(this, context);
    }


    public FlowChain<T> addNode(Class<? extends FlowNode<T>> clazz) {
        clazzs.add(clazz);
        FlowNode<T> bean = (FlowNode<T>) flowChainService.getApplicationContext().getBean(clazz);
        nodes.add(bean);
        return this;
    }

    public <S> FlowChain<T> addNodeWithSubNodes(Class<? extends SubFlowNode<T, S>> clazz,
                                                Class<S> subContextClass,
                                                List<Class<? extends FlowNode<S>>> subNodes) {
        SubFlowNode<T, S> bean = (SubFlowNode<T, S>) flowChainService.getApplicationContext().getBean(clazz);

        FlowChain<S> subChain = newChain(flowChainService, subContextClass);
        for (Class<? extends FlowNode<S>> subNode : subNodes) {
            subChain.addNode(subNode);
        }
        bean.setSubChain(subChain);
        nodes.add(bean);
        return this;
    }

    public <S> FlowChain<T> addNodeWithSubNodes(Class<? extends SubFlowNode<T, S>> clazz,
                                                FlowChain<S> subChain) {
        SubFlowNode<T, S> bean = (SubFlowNode<T, S>) flowChainService.getApplicationContext().getBean(clazz);
        bean.setSubChain(subChain);
        nodes.add(bean);
        return this;
    }

}