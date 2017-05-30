package com.revo;

import java.util.HashMap;
import junit.framework.TestCase;
import static org.mockito.Mockito.*;


public class ApiUtilsTest extends TestCase {
    
    public ApiUtilsTest(String testName) {
        super(testName);
    }

    /**
     * Test of limitData method, of class ApiUtils.
     */
    public void testLimitData() {
        System.out.println("limitData");
        
        Config config = mock(Config.class);
        ApiUtils instance = new ApiUtils(config);
        
        String limitJSON = instance.limitData("9031234567");
        assertEquals("{\"client\":{\"mobile_phone\":\"9031234567\"}}", limitJSON);
    }

    /**
     * Test of orderData method, of class ApiUtils.
     */
    public void testOrderData() {
        System.out.println("orderData");
        
        Config config = mock(Config.class);
        when(config.getCallbackUrl()).thenReturn("callback");
        when(config.getRedirectUrl()).thenReturn("redirect");
        
        ApiUtils instance = new ApiUtils(config);
        
        try {
            String orderJSON = instance.orderData(1.0, "OR12", null);
            String expectedJSON = "{\"callback_url\":\"callback\",\"current_order\":{\"sum\":\"1.00\",\"order_id\":\"OR12\"},\"redirect_url\":\"redirect\"}";
            
            assertEquals(expectedJSON, orderJSON);
        }
        catch(ApiException e) {
            fail("Failed to parse JSON: " + e.getMessage());
        }
        
        try {
            String paramsOrderJSON = instance.orderData(1.0, "OR12", "{\"primary_phone\": \"9031234567\"}");
            String expectedParamsJSON = "{\"callback_url\":\"callback\",\"current_order\":{\"sum\":\"1.00\",\"order_id\":\"OR12\"},\"redirect_url\":\"redirect\",\"primary_phone\":\"9031234567\"}";

            assertEquals(expectedParamsJSON, paramsOrderJSON);
        }
        catch(ApiException e) {
            fail("Failed to parse JSON: " + e.getMessage());
        }
    }

    /**
     * Test of returnData method, of class ApiUtils.
     */
    public void testReturnData() {
        System.out.println("returnData");
        
        Config config = mock(Config.class);
        ApiUtils instance = new ApiUtils(config);
        
        String returnJSON = instance.returnData(1.0, "OR12");
        String expectedJSON = "{\"kind\":\"cancel\",\"sum\":\"1.00\",\"order_id\":\"OR12\"}";
        assertEquals(expectedJSON, returnJSON);
    }

    /**
     * Test of parseOrderResponse method, of class ApiUtils.
     */
    public void testParseOrderResponse() {
        System.out.println("parseOrderResponse");
        
        Config config = mock(Config.class);
        ApiUtils instance = new ApiUtils(config);
        
        String successResponse = "{\"status\":0,\"message\":\"Payload valid\",\"iframe_url\":\"link\"}";
        
        try {
            String result = instance.parseOrderResponse(successResponse);
            assertEquals("link", result);
        }
        catch(ApiException e)
        {
            fail("Exception caught:" + e.getMessage());
        }
        
        String failResponse = "{\"status\":10,\"message\":\"Payload invalid\"}";
        try {
            instance.parseOrderResponse(failResponse);
            fail("Expected exception to be thrown");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Payload invalid");
        }
    }
    
    /**
     * Test of parsePhoneResponse method, of class ApiUtils.
     */
    public void testParsePhoneResponse() throws Exception {
        System.out.println("parsePhoneResponse");
        
        HashMap<String, String> expResult = new HashMap();
        HashMap<String, String> result;
        
        expResult.put("Limit", "9950.0");
        expResult.put("Status", "active");
        
        System.out.println("parseOrderResponse");
        
        Config config = mock(Config.class);
        ApiUtils instance = new ApiUtils(config);
        
        String successResponse = "{\"meta\":{\"status\":0,\"message\":\"Payload valid\"},\"client\":{\"limit_amount\":\"9950.0\",\"status\":\"active\"}}";
        
        try {
            result = instance.parsePhoneResponse(successResponse);
            assertEquals(expResult, result);
        }
        catch(ApiException e)
        {
            fail("Exception caught:" + e.getMessage());
        }
        
        String failResponse = "{\"meta\":{\"status\":61,\"message\":\"Signature wrong\"}}";
        try {
            instance.parsePhoneResponse(failResponse);
            fail("Expected exception to be thrown");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Signature wrong");
        }
    }

    /**
     * Test of parseReturnResponse method, of class ApiUtils.
     */
    public void testParseReturnResponse() throws Exception {
        System.out.println("parseReturnResponse");
        
        HashMap<String, String> expResult = new HashMap();
        HashMap<String, String> result;
        
        expResult.put("Status", "OK");
        
        Config config = mock(Config.class);
        ApiUtils instance = new ApiUtils(config);
        
        String successResponse = "{\"status\":0,\"message\":\"Payload valid\"}";
        
        try {
            result = instance.parseReturnResponse(successResponse);
            assertEquals(expResult, result);
        }
        catch(ApiException e)
        {
            fail("Exception caught:" + e.getMessage());
        }
        
        String failResponse = "{\"status\":21,\"message\":\"Order not exist\"}";
        try {
            instance.parseReturnResponse(failResponse);
            fail("Expected exception to be thrown");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Order not exist");
        }
    }
}
