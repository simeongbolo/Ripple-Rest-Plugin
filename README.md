Ripple-Rest-Plugin
==================

A simple client for the [Ripple Rest Api](https://github.com/ripple/ripple-rest/blob/develop/docs/api-reference.md) 

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

####setNewResourceid(id)


*Sets the resourceId that will be used for sending payments*
*this must be set in order to prevent double spending*
*param id - the new resource Id*


####generateNewResourceId()


*generates a new resource Id to be used for sending payments.*
**YOU MUST GENERATE A NEW RESOURCE ID**
*with every new payment*
*this helps prevent double spending*

####getUuid()[Refrence](https://dev.ripple.com/#create-client-resource-id)
*creates a universally unique identifier (UUID) value which can be used to calculate a client resource ID for a payment*

####getBalances(String address)[Balances Refrence](https://dev.ripple.com/#account-balances)
*Gets the balances for the provided account*
*Returns JSON Object Representation*
**Usage:**


    def response = rippleRestClientService.getBalances("rippleAddress")
        assert response.success == true
        assert response.balances.currency == ["XRP","USD"]

####getConnectionStatus()[Server Obj Refrence](https://dev.ripple.com/#get-server-status)
*Gets the server connection status*


####getNotification(String address, String hash)[Notification Obj Refrence](https://dev.ripple.com/#checking-notifications)
*Gets the transaction notification for a given address and hash*


     
####getAccountSettings(String address)
*Returns JSON object with the account settings for a given address*
**Usage:**


####getPayment(String address, Closure payArgs)[Payment Obj Refrence](https://dev.ripple.com/#confirming-a-payment)
*Gets the payment info for a given address and hash or uuid*
**payArgs's  possible args = [hash,uuid]**

**Usage:**
         
         
         def response =  rippleRestClientService.getPayment({Address}){
                 hash = {AddressPaymentHash}
                 uuid = {uuid}
         }
         assert response.payment.source_account = "Some account"
         
         
####getPaymentQuery(String address, Closure payArgs)[Payment Obj Refrence](https://dev.ripple.com/#payment-history)
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

####getTransaction()[Transaction Obj Refrence](https://dev.ripple.com/#retrieve-ripple-transaction)
*Get the transaction for  a give hash*


####getTrustLines(String address, Closure trustArgs)[Trustline Obj Refrence](https://dev.ripple.com/#trustlines)
*gets the trustline for a given address*
**Usage:**

        response = rippleRestClientService.getTrustLines{
          currency = "USD"
          counterparty = "trust's ripple address"
       }


####getAllTrustLines(String address)[Trustline Obj Refrence](https://dev.ripple.com/#trustlines)
*gets all trustlines for a given address*


####getPaths(address, destinationAddress, Closure paths)[Payment Obj Refrence](https://dev.ripple.com/#preparing-a-payment)
*This will generate a list of possible payments between the two parties for the desired amount, taking into account the established trustlines between the two parties for the currency being transferred*

**Usage:**

            response = rippleRestClientService.getPaths("SourceAddress","destinationAddress"){
               value = ".10"
               currency = "XRP"
               sourceCurrencies = ["USD","CHF","BTC"]
             }
             
####postAccountSettings(acctSecret, String address, Closure actArts)[Account settings Obj Refrence](https://dev.ripple.com/#account-settings)
*Post new account settings*

**Usage:**
            
            def response = rippleRestClientService.postAccountSettings({Secret},{Address}){
                transfer_rate = 0
                password_spent =  false
                require_destination_tag = false
                require_authorization = false
                disallow_xrp = false
                disable_master =  false
     }
     
     
####postPayment(acctSecret, Closure pay)[Payment Obj Refrence](https://dev.ripple.com/#sending-payments)
*Sends a payment from one account to another - Please see offical ripple docs in the provide link above*

**Usage:**

**Note: You must send an Amount object** 

      def response =  rippleRestClientService.postPayment({Secret}){
      
                    source_account = {Address}
                    destination_account ="{Address}"
                    destination_amount =  new Amount(value: ".0001",currency: "XRP" ,issuer: "")
     }
     
####grantTrustLine(String acctSecret, String address, Closure trustArgs)[TrustLine Obj refrence](https://dev.ripple.com/#granting-a-trustline)
*Grants a trustline*

**Usage:**

    
    def response = rippleRestClientService.grantTrustLine({Secret},{Address}){
       limit = 5
       currency ="USD"
       counterparty = "{counter party's address}"
       allows_rippling = true
    }
