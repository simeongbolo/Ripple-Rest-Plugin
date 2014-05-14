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
*Returns A List of Balance Objects*
**Usage:**


    def balancesResponse = rippleRestClientService.getBalances(testAddress)
        println "\nTesting rippleRestClientService.getBalances\n"

        balancesResponse.each{ it ->
            println  "The balance is "+it.value 
            println  "The Currency is " + it.currency
            println "the Counter Party  is " + it?.counterparty 
        }
        

####getConnectionStatus()[Server Obj Refrence](https://dev.ripple.com/#get-server-status)
*Gets the server connection status*


####getNotification(String address, String hash)[Notification Obj Refrence](https://dev.ripple.com/#checking-notifications)
*Gets the transaction notification for a given address and hash*
**Usage**
                
                NotificationResponse notificationResponseResponse =      
                rippleRestClientService.getNotification(testAddress,testAddressTransactionHash)
        println "\nTesting rippleRestClientService.getNotification\n"

        println "The Direction is " + notificationResponseResponse.direction +"\n"
        println "The Hash is " + notificationResponseResponse.hash +"\n"
        println "The state is " + notificationResponseResponse.state +"\n"
        println "The type is " + notificationResponseResponse.type +"\n"
        println "The timestamp is " + notificationResponseResponse.timestamp +"\n"
        println "The account is " + notificationResponseResponse.account +"\n"
        println "The Ledger  is " + notificationResponseResponse.ledger +"\n"
        println "The next hash  is " + notificationResponseResponse.next_hash
        println "The next notification url  is " + notificationResponseResponse.next_notification_url
        println "The previous  is " + notificationResponseResponse.previous_hash


     
####getAccountSettings(String address)
*Returns an Account object with the account settings for a given address*
**Usage:**

            Account acct =  rippleRestClientService.getAccountSettings("rippleAddress")
            
####getPayment(String address, Closure payArgs)[Payment Obj Refrence](https://dev.ripple.com/#confirming-a-payment)
*Gets the payment info for a given address and hash or uuid Returns a Payment Object*
**payArgs's  possible args = [hash,uuid]**

**Usage:**
         
         
         Payment thePayment = rippleRestClientService.getPayment(testAddress){
            hash = testAddressPaymentHash
        }
        println "Test for rippleRestClientService.getPayment\n"
        assert thePayment.result == "tesSUCCESS"
        println "The Direction is " + thePayment.direction
        println "The Hash is " + thePayment.hash 
        println "The state is " + thePayment.state 
        println "The timestamp is " + thePayment.timestamp
        println "The account is " + thePayment.source_account 
        println "The Ledger  is " + thePayment.ledger 
         
         
####getPaymentQuery(String address, Closure payArgs)[Payment Obj Refrence](https://dev.ripple.com/#payment-history)
*Gets payment information using query objects provided in the closure returns a list of Payment Objects*
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
        
        def payments2  = rippleRestClientService.getPaymentQuery(testAddress){
            earliest_first = true
            direction = "incoming"
        }

        println "Test For rippleRestClientService.getPaymentQuery\n"
        payments2.each{it ->
           println "====Payent info===="
            assert it.result == "tesSUCCESS"
            println "The Direction is " + it.direction
            println "The Hash is " + it.hash
            println "The state is " + it.state
            println "The timestamp is " + it.timestamp
            println "The account is " + it.source_account
            println "The fee  is " + it.fee
            println "The invoice_id  is " + it.invoice_id 
            println "The destination_account  is " + it.destination_account 
            println "The partial_payment  is " + it.partial_payment

        }

####getTransaction()[Transaction Obj Refrence](https://dev.ripple.com/#retrieve-ripple-transaction)
*Get the transaction for  a give hash*
**Usage**            
            response = rippleRestClientService.getTransaction(testAddressTransactionHash)
        println "Test for rippleRestClientService.getTransaction\n"
        assert response.success == true

        def transaction = response.transaction
        println "The Amount is " + transaction.Amount
        println "The Hash is " + transaction.hash
        println "The fee is " + transaction.fee
        println "The date is " + transaction.date
        println "The timestamp is " + transaction.timestamp
        println "The account is " + transaction.account 
        println "The Ledger  is " + transaction.ledger 

####getTrustLines(String address, Closure trustArgs)[Trustline Obj Refrence](https://dev.ripple.com/#trustlines)
*gets the trustline for a given address Returns a List of TrustLine Objects*
**Usage:**

        def trustLines  = rippleRestClientService.getTrustLines(testAddress){
            currency = "USD"
        }


        println "Test for rippleRestClientService.getTrustLines\n"
        trustLines.each {tl->
            println "The limit is " + tl.limit
            println "The account is " + tl.account
            println "The currency is " + tl.currency
            println "The counter party is " + tl.counterparty
        }


####getAllTrustLines(String address)[Trustline Obj Refrence](https://dev.ripple.com/#trustlines)
*gets all trustlines for a given address*


####getPaths(address, destinationAddress, Closure paths)[Payment Obj Refrence](https://dev.ripple.com/#preparing-a-payment)
*This will generate a list of possible payments between the two parties for the desired amount, taking into account the established trustlines between the two parties for the currency being transferred*
*Returns a List of Payment Objects*
**Usage:**

            def payments = rippleRestClientService.getPaths("rDdwBhw5ypG7jgg2HTD8g3ntw3vrXq8ssQ",testAddress){
               value = ".10"
            currency = "XRP"
            sourceCurrencies = ["USD","BTC"]
        }
        println "Test for rippleRestClientService.getPaths\n"

        payments.each {p ->
           assert p.source_account != null
            println p.destination_amount
            println p.destination_tag
            println p.direction

        }
             
####postAccountSettings(acctSecret, String address, Closure actArts)[Account settings Obj Refrence](https://dev.ripple.com/#account-settings)
*Post new account settings returns a new Account object with the new account settings*

**Usage:**
            
             Account newAccountSettings = rippleRestClientService.postAccountSettings(testAddressSecret,testAddress){
            transfer_rate = 0
            password_spent =  false
            require_destination_tag = false
            require_authorization = false
            disallow_xrp = false
            disable_master =  false
        }

        assert newAccountSettings.success == true
        assert newAccountSettings.settings.transfer_rate == 0
        assert newAccountSettings.settings.require_destination_tag == false
        assert  newAccountSettings.settings.disallow_xrp == false
     
####postPayment(acctSecret, Closure pay)[Payment Obj Refrence](https://dev.ripple.com/#sending-payments)
*Sends a payment from one account to another - Please see offical ripple docs in the provide link above*
*Returns a PaymentResponse Object*
**Usage:**

**Note: You must send an Amount object** 

      PaymentResponse  paymentResponse =  rippleRestClientService.postPayment(testAddressSecret){
            source_account =testAddress
            destination_account =testDestAddress
            destination_amount =  new Amount(value: ".0001",currency: "XRP" ,issuer: "")
        }

        println "\n Testing rippleRestClientService.postPayment full Response\n"
        assert paymentResponse.success == true
        println "The Client Resouce id is: " +  paymentResponse.client_resource_id
        println "The Status Url is: " +  paymentResponse.status_url 
     
####grantTrustLine(String acctSecret, String address, Closure trustArgs)[TrustLine Obj refrence](https://dev.ripple.com/#granting-a-trustline)
*Grants a trustline - returns a TrustLineGrant object*

**Usage:**

    
    TrustLineGrant trustLine = rippleRestClientService.grantTrustLine(testAddressSecret,testAddress){
            limit = 5
            currency ="USD"
            counterparty = "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"
            account_allows_rippling = true
        }
        println "Test For rippleRestClientService.grantTrustLine\n"
         assert   trustLine.counterparty != null
        println "The limit is " + trustLine.limit
        println "The account is " + trustLine.account
        println "The currency is " + trustLine.currency 
        println "The counter party is " + trustLine.counterparty 


#Tests
##>>[Ripple rest api test project](https://github.com/simeongbolo/Ripple-Rest-Client-PluginTest)
