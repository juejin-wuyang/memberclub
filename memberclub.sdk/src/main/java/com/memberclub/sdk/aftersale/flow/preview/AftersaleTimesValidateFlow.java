/**
 * @(#)GetAndCheckAftersaleTImesFlow.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersaleTimesValidateFlow extends FlowNode<AftersalePreviewContext> {

    @Override
    public void process(AftersalePreviewContext context) {
        // TODO : 2024/12/22 补充售后次数
    }
}