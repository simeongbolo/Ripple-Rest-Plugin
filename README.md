Ripple-Rest-Plugin
==================

A simple client for the [Ripple Rest Api](https://dev.ripple.com/#introduction) 

## Installation
Run  

`grails install-plugin grails-ripple-rest-client-api-0.1.zip` - change the version number if needed


## Basic Usage

Edit `Config.groovy` and add the following configuration, replaced with your server url. 
This is **required**:

    rippleRest{
              api{
                serverUrl = "http://localhost:5990/v1/"
                 }
              }

You may also add proxy settings, this is **optinal**:

  ```
  rippleRest{
    api{
        serverUrl = "http://localhost:5990/v1/"
        proxy = ['localhost':8080]
        connectTimeout= 30000
        readTimeout = 20000
    }
  }
  ```
  The proxy setting can either be map containing the key for the host name and a value for the port or an instance of `java.net.Proxy`.
  
  
  **Then simply inject the `rippleRestClientService` bean into your controller/service**
  ```
  class TestRippleController {

    def rippleRestClientService


    def index() {
        def uuid = rippleRestClientService.getUuid()
        render "<h1>Got the uuid from the server = $uuid </h1>"
    }
  }
  ```
## Method Detail

###setNewResourceid(id)


     Sets the resourceId that will be used for sending payments
     this must be set in order to prevent double spending
     param id - the new resource Id

    

  
###generateNewResourceId()


     generates a new resource Id to be used for sending payments.
     YOU MUST GENERATE A NEW RESOURCE ID 
     with every new payment
     this helps prevent double spending
     
     

