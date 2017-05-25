package com.revo;

import java.util.HashMap;

public class RevoAPI {
    private ApiUtils api;

    public void setConfig(Config config)
    {
        this.api = new ApiUtils(config);
    }

    public HashMap<String,String> limitByPhone(String phone) throws ApiException {
        String payload = this.api.limitData(phone);
        String response = this.api.callService(payload, ApiUtils.ENDPOINT_TYPE_PHONE);
        return this.api.parsePhoneResponse(response);
    }
    
    public HashMap<String,String> returnOrder(Double amount, String orderId) throws ApiException {
        String payload = this.api.returnData(amount, orderId);
        String response = this.api.callService(payload, ApiUtils.ENDPOINT_TYPE_RETURN);
        return this.api.parseReturnResponse(response);
    }

    public String preorderIframeLink(String phone) throws ApiException {
        String randomStringId = String.format("%.0f", Math.random() * 1000000.0 + 1000000.0);
        String randomOrderId = "RANDOMORDERID" + randomStringId;
        String payload;
        
        if(phone != null && !phone.isEmpty())
        {
            payload =  this.api.orderData(1.0, randomOrderId, "{\"primary_phone\":\"" + phone + "\"}");
        }
        else
        {
            payload =  this.api.orderData(1.0, randomOrderId, null);
        }
        
        String response = this.api.callService(payload, ApiUtils.ENDPOINT_TYPE_PREORDER);
        
        return this.api.parseOrderResponse(response);
    }

    public String orderIframeLink(Double amount, String orderId, String additionalParams) throws ApiException {
        String payload =  this.api.orderData(amount, orderId, additionalParams);
        String response = this.api.callService(payload, ApiUtils.ENDPOINT_TYPE_ORDER);
        return this.api.parseOrderResponse(response);
    }
}
