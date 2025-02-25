package com.memberclub.starter.controller;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyCmd;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyResponse;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCmd;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewResponse;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.aftersale.service.AftersaleBizService;
import com.memberclub.starter.controller.vo.aftersale.AftersalePreviewVO;
import com.memberclub.starter.controller.vo.aftersale.AftersaleSubmitVO;
import com.memberclub.starter.controller.vo.base.DataResponse;
import com.memberclub.starter.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/memberclub/aftersale")
public class AftersaleController {

    @Autowired
    private AftersaleBizService aftersaleBizService;

    @RequestMapping("/preview")
    public DataResponse<AfterSalePreviewResponse> preview(HttpServletRequest servletRequest, @RequestBody AftersalePreviewVO vo) {
        DataResponse<AfterSalePreviewResponse> response = new DataResponse<>();

        try {
            SecurityUtil.securitySet(servletRequest);
            AfterSalePreviewCmd cmd = new AfterSalePreviewCmd();
            cmd.setSource(AftersaleSourceEnum.findByCode(vo.getSource()));
            cmd.setBizType(BizTypeEnum.findByCode(vo.getBizType()));
            cmd.setUserId(SecurityUtil.getUserId());
            cmd.setOperator(String.valueOf(SecurityUtil.getUserId()));
            cmd.setTradeId(vo.getTradeId());

            AfterSalePreviewResponse previewResponse = aftersaleBizService.preview(cmd);
            response.setSucc(previewResponse.isSuccess());
            response.setErrorCode(previewResponse.getUnableCode());
            response.setErrorMsg(previewResponse.getUnableTip());
            response.setData(previewResponse);
        } catch (Exception e) {
            response.setSucc(false);
            response.setErrorCode(ResultCode.INTERNAL_ERROR.getCode());
            response.setErrorMsg(ResultCode.PERFORM_ITEM_GRANT_ERROR.getMsg());
        } finally {
            SecurityUtil.clear();
        }
        return response;
    }

    @RequestMapping("/apply")
    public DataResponse<AftersaleApplyResponse> submit(HttpServletRequest servletRequest, @RequestBody AftersaleSubmitVO vo) {
        DataResponse response = new DataResponse();

        try {
            SecurityUtil.securitySet(servletRequest);
            AftersaleApplyCmd cmd = new AftersaleApplyCmd();
            cmd.setSource(AftersaleSourceEnum.findByCode(vo.getSource()));
            cmd.setBizType(BizTypeEnum.findByCode(vo.getBizType()));
            cmd.setUserId(SecurityUtil.getUserId());
            cmd.setOperator(String.valueOf(SecurityUtil.getUserId()));
            cmd.setTradeId(vo.getTradeId());
            cmd.setDigests(vo.getPreviewDigest());
            cmd.setDigestVersion(vo.getDigestVersion());

            AftersaleApplyResponse applyResponse = aftersaleBizService.apply(cmd);
            response.setSucc(applyResponse.isSuccess());
            response.setErrorCode(applyResponse.getUnableCode());
            response.setErrorMsg(applyResponse.getUnableTip());
            response.setData(applyResponse);
        } catch (Exception e) {
            response.setSucc(false);
            response.setErrorCode(ResultCode.INTERNAL_ERROR.getCode());
            response.setErrorMsg(ResultCode.PERFORM_ITEM_GRANT_ERROR.getMsg());
        } finally {
            SecurityUtil.clear();
        }
        return response;
    }
}
