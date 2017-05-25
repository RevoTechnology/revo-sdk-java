package com.revo;

public class Config {

    private final String callbackUrl;
    private final String redirectUrl;
    private final String secret;
    private final String storeId;
    private final String baseHost;

    public Config(Boolean testMode, String callbackUrl, String redirectUrl, String secret, String storeId) throws ApiException {
        if(callbackUrl == null || callbackUrl.isEmpty())
            throw new ApiException("Callback Url cannot be empty");
        else
            this.callbackUrl = callbackUrl;
        
        if(redirectUrl == null || redirectUrl.isEmpty())
            throw new ApiException("Redirect Url cannot be empty");
        else
            this.redirectUrl = redirectUrl;
        
        if(secret == null || secret.isEmpty())
            throw new ApiException("Secret cannot be empty");
        else
            this.secret      = secret;
        
        if(storeId == null || storeId.isEmpty())
            throw new ApiException("Store ID cannot be empty");
        else
            this.storeId     = storeId;

        if(testMode)
            this.baseHost = "http://demo.revoup.ru/";
        else
            this.baseHost = "http://r.revoup.ru";
    }

    public String getCallbackUrl()
    {
        return callbackUrl;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    public String getBaseHost() {
        return baseHost;
    }

    public String getSecret() {
        return secret;
    }

    public String getStoreId() {
        return storeId;
    }
}
