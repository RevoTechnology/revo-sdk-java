# RevoSDK Java Library

Java library to access Revo API

## Basic usage

Revo API implements four methods:

* check client's limit by phone number
* getting form link for preorder
* getting form link for full order process
* performing partial of full order return

API client must be configured as follows:

```java
import com.revo;

public void initSDK() {
     config = new Config(false,  "http://example.com/callback_url", "http://example.com/redirect_url", "secret_hash", "103" );
}
```

* `testMode` indicates whether demo (`true`) or production (`false`) mode to use
* `callbackUrl` must be URL to which Revo will send callback data
* `redirectUrl` must be URL to which user will be redirected after form submit
* `secret` - hash-like string for creating signature from Revo (must be stored privately)
* `storeId` - store id in Revo system


## Basic methods

After setting up `Config` you may access API methods in `RevoAPI` following way:

```java

import com.revo;

public void testSDK() {
	Config config = new Config(false, "http://example.com/redirect_url", "http://localhost/callback_url", "secret_hash", "103" );
	
	RevoAPI API = new RevoAPI();
    	API.setConfig(config);
    
    	try {
	    	// get limit by phone service
		HashMap<String,String> limit = API.limitByPhone("9123456789");
    
		// get preorder link with initial phone number
		String preorder_link = API.preorderIframeLink("9876543210");
    
		// get order link with additional params
		String order_link = API.orderIframeLink(99.99, "OR123", "{\"primary_phone\":\"9213456789\"}");
    
		// make return on existsing order
		HashMap<String,String> result = API.returnOrder(9.99, "OR12");
    	}
	// ApiException is standart Exception class for this Revo API library
	catch(ApiException e) { 
    		System.out.println("Failed to proceed request: " + e.getMessage());
    	}
}
```

## Contributing ##

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request


Since here in RevoTechnologies we try to provide any developer with proper instruments, we greatly appreciate your help in improving this or any other language SDK :)
