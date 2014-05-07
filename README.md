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

You may also add proxy settings, this is **optional**:

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


*Sets the resourceId that will be used for sending payments*
*this must be set in order to prevent double spending*
*param id - the new resource Id*


###generateNewResourceId()


*generates a new resource Id to be used for sending payments.*
*YOU MUST GENERATE A NEW RESOURCE ID*
*with every new payment*
*this helps prevent double spending*

###getBalances(String address)[Balances Refrence](https://dev.ripple.com/#account-balances)
*Gets the balances for the provided account*
*Returns JSON Object Representation*
**Usage:**


    def response = rippleRestClientService.getBalances("rippleAddress")
        assert response.success == true
        assert response.balances.currency == ["XRP","USD"]

###getConnectionStatus()[Server Obj Refrence](https://dev.ripple.com/#get-server-status)
*Gets the server connection status*


###getNotification(String address, String hash)[Notification Obj Refrence](https://dev.ripple.com/#checking-notifications)
*Gets the transaction notification for a given address and hash*


     
###getAccountSettings(String address)
*Returns JSON object with the account settings for a given address*
**Usage:**


###getPayment(String address, Closure payArgs)[Payment Obj Refrence](https://dev.ripple.com/#confirming-a-payment)
*Gets the payment info for a given address and hash or uuid*
**payArgs's  possible args = [hash,uuid]**

**Usage:**
         
         
         def response =  rippleRestClientService.getPayment({Address}){
                 hash = {AddressPaymentHash}
                 uuid = {uuid}
         }
         assert response.payment.source_account = "Some account"
         
         
###getPaymentQuery(String address, Closure payArgs)[Payment Obj Refrence](https://dev.ripple.com/#payment-history)
*Gets payment information using query objects provided in the closure*
**payArgs - possible args:**
*source_account(String)*
*destination_account(String)*
*exclude_failed(bool)*
*start_ledger(int)*
*end_ledger(int)*
*end_ledger(bool)*
*results_per_page(int)*
*page(int)*
*direction*
*earliest_first*

**Usage:**
        
        def response = rippleRestClientService.getPaymentQuery(testAddress){
            earliest_first = true
            direction = "incoming"
        }
