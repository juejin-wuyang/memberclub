/**
 * @(#)SceneEnum.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.common;

import com.memberclub.domain.contants.StringContants;

import static com.memberclub.domain.contants.StringContants.DISCOUNT_COUPON_RIGHT_TYPE;

/**
 * @author 掘金五阳
 */
public enum SceneEnum {

    DEFAULT_SCENE(StringContants.DEFAULT_SCENE.toString()),

    RIGHT_TYPE_SCENE_COUPON(StringContants.COUPON_RIGHT_TYPE.toString()),

    RIGHT_TYPE_SCENE_DISCOUNT_COUPON(DISCOUNT_COUPON_RIGHT_TYPE.toString()),

    RIGHT_TYPE_SCENE_MEMBERSHIP(StringContants.MEMBERSHIP_RIGHT_TYPE.toString()),
    
    RIGHT_TYPE_SCENE_LESSONSHIP(StringContants.LESSON_RIGHT_TYPE.toString()),

    RIGHT_TYPE_SCENE_MEMBER_DISCOUNT_PRICE(StringContants.MEMBER_DISCOUNT_PRICE.toString()),//会员价

    RIGHT_TYPE_SCENE_FREE_FREIGHT_COUPON(StringContants.FREE_FREIGHT_COUPON.toString()), //免运费券


    SCENE_MONTH_CARD("201"),//履约上下文执行 scene, 月卡部分

    SCENE_MUTIL_PERIOD_CARD("202"),//履约上下文执行 scene, 多周期卡部分

    SCENE_AFTERSALE_MONTH_CARD("301"),//售后月卡

    SCENE_AFTERSALE_MUTIL_PERIOD_CARD("302"),//售后多周期卡

    SCENE_AFTERSALE_RENEW("304"),//售后续费


    /*****************************************/
    //提单部分
    HOMEPAGE_SUBMIT_SCENE(StringContants.HOMEPAGE_VALUE + ""),


    /*****************************************/
    /**
     * @see com.memberclub.domain.context.oncetask.common.TaskTypeEnum
     * 任务
     */
    PERIOD_PERFORM_TASK_TYPE("1"),

    FINANCE_EXPIRE_TASK_TYPE("2"),

    AFTERSALE_EXPIRE_REFUND_TASK_TYPE("3"),

    /*****************************************/

    ;


    /*****************************************/
    private String value;

    SceneEnum(String value) {
        this.value = value;
    }


    public static String buildRightTypeScene(int rightType) {
        return "" + rightType;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return this.value;
    }

}
