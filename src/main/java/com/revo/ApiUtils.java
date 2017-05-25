package com.revo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;


class ApiUtils {
    private final Config config;
    
    public static final String ENDPOINT_TYPE_PHONE      = "limit";
    public static final String ENDPOINT_TYPE_ORDER      = "order";
    public static final String ENDPOINT_TYPE_PREORDER   = "preorder";
    public static final String ENDPOINT_TYPE_RETURN     = "return";
    
    private static final String ENDPOINT_URI_PHONE  = "/api/external/v1/client/limit";
    private static final String ENDPOINT_URI_ORDER  = "/iframe/v1/auth";
    private static final String ENDPOINT_URI_RETURN = "/online/v1/return";
    
    public ApiUtils(Config config) {
        this.config = config;
    }

    protected String limitData(String phone) {
        JSONObject hash = new JSONObject();
        JSONObject phone_hash = new JSONObject();
        
        phone_hash.put("mobile_phone", phone);
        hash.put("client", phone_hash);
        
        return hash.toString();
    }

    protected String orderData(Double amount, String orderId, String additionalParams) throws ApiException {
        JSONObject hash = new JSONObject();
        JSONObject order_hash = new JSONObject();
        JSONObject resultJSON;
        
        order_hash.put("sum", String.format("%.2f", amount));
        order_hash.put("order_id", orderId);

        hash.put("callback_url", this.config.getCallbackUrl());
        hash.put("redirect_url", this.config.getRedirectUrl());
        hash.put("current_order", order_hash);
        
        if(additionalParams != null && !additionalParams.isEmpty())
        {
            try {
                JSONObject paramsJSON = new JSONObject(additionalParams);
                resultJSON = deepMerge(hash, paramsJSON);
            }
            catch(JSONException e)
            {
                throw new ApiException("Invalid additional params: " + e.getMessage());
            }
        }
        else
        {   
            resultJSON = hash;
        }
        
        return resultJSON.toString();
    }

    protected String returnData(Double amount, String orderId) {
        JSONObject hash = new JSONObject();
        
        hash.put("order_id", orderId);
        hash.put("sum", String.format("%.2f", amount));
        hash.put("kind", "cancel");
        
        return hash.toString();
    }
    
    protected String callService(String payload, String type) throws ApiException {
        String signature = createSignature(payload + this.config.getSecret());
        URL url = createUrl(type, signature);
            
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }
        catch (Exception ex)
        {
            throw new ApiException("Can't get response from " + config.getBaseHost() + ": " + ex.getMessage());
        }
    }

    protected String parseOrderResponse(String response) throws ApiException {
        JSONObject result = new JSONObject(response);
        
        if(0 == result.getInt("status"))
            return result.getString("iframe_url");
        else
            throw new ApiException(result.getString("message"));
    }
    
    protected HashMap<String,String> parsePhoneResponse(String response) throws ApiException {
        JSONObject result = new JSONObject(response);
        
        if(0 == result.getJSONObject("meta").getInt("status"))
        {
            HashMap<String,String> results = new HashMap<>();
            
            results.put("Status", result.getJSONObject("client").getString("status"));
            results.put("Limit", result.getJSONObject("client").getString("limit_amount"));
            
            return results;
        }
        else
        {
            throw new ApiException(result.getJSONObject("meta").getString("message"));
        }
            
    }
    
    protected HashMap<String,String> parseReturnResponse(String response) throws ApiException {
        JSONObject result = new JSONObject(response);
        
        if(0 == result.getInt("status"))
        {
            HashMap<String,String> results = new HashMap<>();
            results.put("Status", "OK");
            return results;
        }
        else
        {
            throw new ApiException(result.getString("message"));
        }
            
    }


    private URL createUrl(String type, String signature) throws ApiException {
        String query = "?store_id=" + this.config.getStoreId() + "&signature=" + signature;

        try {
            switch (type) {
                case ENDPOINT_TYPE_PHONE:
                    return new URL(this.config.getBaseHost() + ENDPOINT_URI_PHONE + query);

                case ENDPOINT_TYPE_ORDER:
                case ENDPOINT_TYPE_PREORDER:
                    return new URL(this.config.getBaseHost() + ENDPOINT_URI_ORDER + query);


                case ENDPOINT_TYPE_RETURN:
                    return new URL(this.config.getBaseHost() + ENDPOINT_URI_RETURN + query);

            }
        }
        catch(MalformedURLException Ex)
        {
            throw new ApiException("Failed create service URL: " + Ex.getMessage());
        }

        throw new ApiException("Invalid service type: " + type);
    }

    private static String createSignature(String payload) throws ApiException {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(payload.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new ApiException("Failed to create signature: " + e.getMessage());
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    
    private static JSONObject deepMerge(JSONObject source, JSONObject target) throws JSONException {
        for (String key: JSONObject.getNames(source)) {
                Object value = source.get(key);
                if (!target.has(key)) {
                    target.put(key, value);
                } else {
                    if (value instanceof JSONObject) {
                        JSONObject valueJson = (JSONObject)value;
                        deepMerge(valueJson, target.getJSONObject(key));
                    } else {
                        target.put(key, value);
                    }
                }
        }
        
        return target;
    }
}
