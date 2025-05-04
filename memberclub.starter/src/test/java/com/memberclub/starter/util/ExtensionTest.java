/**
 * @(#)TestExtension.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.util;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.sdk.perform.extension.reverse.ReversePerformExtension;
import com.memberclub.starter.AppStarter;
import com.memberclub.starter.demomember.DefaultTestExtension;
import com.memberclub.starter.demomember.JustTestExtension;
import com.memberclub.starter.mock.MockBaseTest;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 掘金五阳
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppStarter.class})
public class ExtensionTest extends MockBaseTest {

    @Autowired
    ExtensionManager extensionManager;


    @Test
    @SneakyThrows
    public void test() {
        JustTestExtension extension = extensionManager.getExtension(
                BizScene.of(BizTypeEnum.DEMO_MEMBER.getCode(),
                        SceneEnum.DEFAULT_SCENE.toString()), JustTestExtension.class);
        PerformCmd cmd = new PerformCmd();
        cmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        cmd.setUserId(1000);
        extension.execute(cmd);
    }

    @Test(expected = RuntimeException.class)
    @SneakyThrows
    public void testInterfaceException() {
        JustTestExtension extension = extensionManager.getExtension(
                BizScene.of(BizTypeEnum.DEMO_MEMBER.getCode(),
                        SceneEnum.DEFAULT_SCENE.toString()), DefaultTestExtension.class);
        PerformCmd cmd = new PerformCmd();
        cmd.setBizType(BizTypeEnum.DEMO_MEMBER);
        cmd.setUserId(1000);
        extension.execute(cmd);
    }

    @Test(expected = RuntimeException.class)
    @SneakyThrows
    public void testInterfaceExceptionAndLossBaseException() {
        ReversePerformExtension extension = extensionManager.getExtension(
                BizScene.of(BizTypeEnum.DEMO_MEMBER.getCode() + 100,
                        SceneEnum.DEFAULT_SCENE.toString()), ReversePerformExtension.class);
        extension.reverse(null);
    }
}