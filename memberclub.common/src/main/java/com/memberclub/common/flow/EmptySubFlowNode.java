package com.memberclub.common.flow;

public class EmptySubFlowNode<T> extends SubFlowNode<T, T> {

    @Override
    public void process(T t) {
        getSubChain().execute(t);
    }
}
