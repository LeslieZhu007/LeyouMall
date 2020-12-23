package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/22 16:10
 */
@Component
@Slf4j
public class SmsUtil {

    @Autowired
    private IAcsClient acsClient;

    @Autowired
    private SmsProperties prop;

    /**
     * 发送短信验证码
     * @param phoneNumber
     * @param code
     */
    public void sendVerificationCode(String phoneNumber,String code) {
        //格式化参数
        String param = String.format( "{\"code\":\"%s\"}",code);

        //发送短信验证码
        sendMessage(phoneNumber,prop.getSignName(),prop.getVerifyCodeTemplate(),param);
    }

    /**
     * 通用发送短信工具类
     * @param phoneNumber
     * @param signName
     * @param templateCode
     * @param templateParam
     */
    public void sendMessage(String phoneNumber,String signName,String templateCode,String templateParam) {
        //准备短信发送的请求
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(prop.getDomain());
        request.setSysVersion(prop.getVersion());
        request.setSysAction(prop.getAction());
        request.putQueryParameter("RegionId", prop.getRegionID());
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode",templateCode);
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            CommonResponse response = acsClient.getCommonResponse(request);
            //System.out.println(response.getData());
            //解析结果
            Map<String, String> data = JsonUtils.toMap(response.getData(), String.class, String.class);
            //判断结果
            if("ok".equals(data.get("Code"))) {
                //失败
                log.info("短信发送失败!原因:{}",data.get("Message"));

                throw new LyException(500,"短信发送失败！");
            }
                /*
                * {
                "Message": "OK",
                "RequestId": "873043ac-bcda-44db-9052-2e204c6ed20f",
                "BizId": "607300000000000000^0",
                "Code": "OK"
                }*/
                //发送成功
            log.info("发送短信成功!");
        } catch (ServerException e) {
            log.info("发送短信失败，服务端异常：",e);
            throw new RuntimeException(e);
        } catch (ClientException e) {
            log.info("发送短信失败，客户端异常：",e);
            throw new RuntimeException(e);
        }
    }
    }

