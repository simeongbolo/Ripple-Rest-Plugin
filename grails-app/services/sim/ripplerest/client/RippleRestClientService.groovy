package sim.ripplerest.client

import grails.converters.JSON
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.beans.factory.InitializingBean
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import sim.entity.*
import sim.rest.ErrorResponse
import sim.rest.InvalidRippleServerException
import sim.rest.RestBuilder
import sim.rest.RestResponse

/**
 * This is the main service to access the Ripple rest api
 * It will:
 *  Load properties from config.groovy
 *  build the correct urls for accessing the ripple rest api
 *
 *You must have the proper configuration in place before using this service
 * Example - Config.groovy
 * rippleRest{
 api{
 serverUrl = "http://localhost:5990/v1/"

 }
 }
 */
class RippleRestClientService implements InitializingBean {

    def grailsApplication
    private String resourceId
    private RestBuilder restBuilder
    private String serverUrl
    private EntityCreationDelegate delegate = new EntityCreationDelegate()

    @Override
    void afterPropertiesSet() throws Exception {

        Map conf = fetchConfig()

        try {

            buildService(conf)
            generateNewResourceId()

        } catch (Exception e) {
            throw new InvalidRippleServerException(e.toString(), this.class)
        }

    }

    private Map fetchConfig() {

        Map conf = grailsApplication.config.rippleRest.api
        if (!conf) {
            throw new IllegalStateException('No configuration found. Please configure the ripple rest client plugin')
        }

        return conf
    }

    private void buildService(Map conf) {

        if(conf.proxy){
            def settings = [connectTimeout:conf.connectTimeout,
                    readTimeout:conf.readTimeout,
                    proxy:conf.proxy]

            restBuilder = new RestBuilder(delegate.deEmpty(settings))
        }else{
            restBuilder = new RestBuilder()
        }

        verifyConfiguration(conf)
        serverUrl = conf.serverUrl



    }

    private void verifyConfiguration(conf) {
        def urlS = conf?.serverUrl
        if (!urlS) {
            throw new IllegalStateException("Missing Server Url in configuration for ")
        }

        def serverAvil = restBuilder.get(conf.serverUrl.concat("server/connected"))

        if (serverAvil instanceof ErrorResponse) {
            throw new IllegalStateException("Cannot connect to Ripple Server {$conf.serverUrl}")

        } else if (serverAvil instanceof RestResponse) {

            if (!JSON.parse(serverAvil.body).connected) {
                throw new IllegalStateException("Cannot connect to Ripple Server {$conf.serverUrl}")
            }
        }
    }

    /**
     * Sets the resourceId that will be used for sending payments
     * this must be set in order to prevent double spending
     * @param id - the new resource Id
     */
    public void setNewResourceid(id) {
        resourceId = id
    }

    /**
     * Gets the account settings for a given address
     * @param address
     * @return -  A new Account Object
     */
    def getAccountSettings(String address) {
        def resp = restBuilder.get(serverUrl.concat(Account.settings(address)))
        def args  = getJSONmap(JSON.parse(resp.body))
        handleResponse(resp , new Account(), args)
    }

    /**
     * Gets the balances for the provided account
     * @param address
     * @return a List of Balance Objects
     */
    def getBalances(String address) {
        def resp = restBuilder.get(serverUrl.concat(Account.balances(address)))
        if(hasErrors(resp))
            return JSON.parse(resp.text)

        def args  = getJSONmap(JSON.parse(resp.body));
        def balances = []

        args.balances.each{bal ->
            Balance b = new Balance()
            this.delegate.update(b,bal)
            balances<<b
        }

        return balances
    }

    /**
     *
     * @return the server connection status as in JSON Object Representation
     */
    def getConnectionStatus() {
        def resp = restBuilder.get(serverUrl.concat("server/connected"))
        return handleResponse(resp)
    }

    /**
     * Gets the transaction notification for a given address and hash
     * @param address
     * @param hash
     * @return A Notification Object
     */
    def getNotification(String address, String hash) {
        def resp = restBuilder.get(serverUrl.concat(Account.notification(address, hash)))
        if(hasErrors(resp))
            return JSON.parse(resp.text)
        def args  = getJSONmap(JSON.parse(resp.body).notification);
        handleResponse(resp , new NotificationResponse(), args)
    }

    /**
     * Gets the payment info for a given address and hash or uuid
     * @param address
     * @param payArgs - possible args = [hash,uuid]
     *
     *
     * Usage:
     * Payment response =  rippleRestClientService.getPayment({Address}){
     hash = {AddressPaymentHash}
     }*
     * @return Payment Object
     */
    def getPayment(String address, Closure payArgs) {
        Payment pay = new Payment()
        delegate.createEntity(pay, payArgs)

        def resp
        if (pay.hash) {
            resp = restBuilder.get(serverUrl.concat(Account.paymentWithHash(address, pay.hash)))
        } else if (pay.uuid) {
            resp = restBuilder.get(serverUrl.concat(Account.paymentWithUuid(address, pay.uuid)))
        } else {
            return new ErrorResponse("Please provide a Uuid or Hash")
        }
        if(hasErrors(resp))
            return JSON.parse(resp.text)
        return handleResponse(resp , new Payment(), getJSONmap(JSON.parse(resp.body)).payment)
    }

    /**
     *  Gets payment information using query objects provided in the closure
     * @param address
     * @param payArgs - possible args = [ source_account(String),destination_account(String),exclude_failed(bool),
     *                                     start_ledger(int),end_ledger(int),end_ledger(bool),results_per_page(int),
     *                                     page(int)]
     *
     * Usage
     * def payments = rippleRestClientService.getPaymentQuery(testAddress){
     earliest_first = true
     direction = "incoming"
     }

     @return A List of Payment Objects
     */
    def getPaymentQuery(String address, Closure payArgs) {
        Payment pay = new Payment()
        delegate.createEntity(pay, payArgs)

        def resp = restBuilder.get(serverUrl.concat(Account.paymentWithQuery(address, delegate.current)))
        if(hasErrors(resp))
            return JSON.parse(resp.text)

        def args  = getJSONmap(JSON.parse(resp.body));
        def payments = []

        args.payments.each{pa ->
            Payment p = new Payment()
            this.delegate.update(p,pa.payment)
            payments<<p
        }

        return payments
    }

    /**
     *  gets the server info
     * @return JSON Object
     */
    def getServerInfo() {
        def resp = restBuilder.get(serverUrl.concat("server"))
        return handleResponse(resp)
    }

    /**
     * get the transaction for  a given hash
     * @param hash - transaction hash
     * @return JSON Object Representation
     */
    def getTransaction(String hash) {
        def resp = restBuilder.get(serverUrl.concat("tx/").concat(hash))
        return handleResponse(resp)
    }

    /**
     *
     * @param address - Required
     * @param trustArgs - Closure Aguments
     * getTrustLines{
     *     currency = "USD"
     *      counterparty = "trust's ripple address"
     *}* @return TrustLine Object
     */
    def getTrustLines(String address, Closure trustArgs) {
        TrustLine trustL = new TrustLine()
        delegate.createEntity(trustL, trustArgs)
        def resp = restBuilder.get(serverUrl.concat(TrustLine.trustlineWithQuery(address, delegate.current)))
        if(hasErrors(resp))
            return JSON.parse(resp.text)
        def args  = getJSONmap(JSON.parse(resp.body));
        def trustLines = []

        args.trustLines.each{tl ->
            TrustLine t = new TrustLine()
            this.delegate.update(t,tl)
            trustLines<<t
        }

        trustLines
    }

    /**
     * gets all trustlines for a given address
     * @param address
     * @return JSON Object Representation
     */
    def getAllTrustLines(String address) {
        def resp = restBuilder.get(serverUrl.concat(TrustLine.trustlineWithQuery(address, [:])))
        return handleResponse(resp)
    }

    /**
     *
     * @return A generated uuid
     */
    def getUuid() {
        def resp = restBuilder.get(serverUrl.concat("uuid"))
        if(hasErrors(resp))
            return JSON.parse(resp.text)

        def obj = JSON.parse(resp.body)
        if (obj?.success) {
            return obj.uuid
        } else
            return obj
    }

    /**
     *
     * @param address - Required
     * @param destinationAddress - Reguired
     * @param amount - Required
     * @param c - possible params Closure
     * Usage
     *  def payments  =  getPaths{
     *           value = ".10"
     currency = "XRP"
     issuer  = "...I"
     sourceCurrencies = ["USD","CHF","BTC"]
     }*
     * @return  a list of Payment objects
     */
    def getPaths(address, destinationAddress, Closure paths) {
        Amount amt = new Amount()
        delegate.createEntity(amt, paths)

        def resp

        if (amt?.sourceCurrencies) {
            resp = restBuilder.get(serverUrl.concat(Payment.pathsQuery(address, destinationAddress, amt, amt?.sourceCurrencies)))
        } else {
            resp = restBuilder.get(serverUrl.concat(Payment.paths(address, destinationAddress, amt)))
        }

        if(hasErrors(resp))
            return JSON.parse(resp.text)
        def args  = getJSONmap(JSON.parse(resp.body));
        def payments = []

        args.payments.each{pay ->
            Payment p = new Payment()
            this.delegate.update(p,pay)
            payments<<p
        }

        return payments

    }

    /**
     * Post new account settings
     * @param acctSecret
     * @param address
     * @param actArts
     * Usage:
     * def response = rippleRestClientService.postAccountSettings({Secret},{Address}){
     transfer_rate = 0
     password_spent =  false
     require_destination_tag = false
     require_authorization = false
     disallow_xrp = false
     disable_master =  false
     }
     * @return  An Account Object with thr new account settings
     */
    def postAccountSettings(acctSecret, String address, Closure actArts) {
        Settings acct = new Settings()
        delegate.createEntity(acct, actArts)

        def json = new JsonBuilder()
        def tmpAct = delegate.current
        def val = json {
            secret acctSecret
            settings tmpAct
        }

        def resp = restBuilder.post(serverUrl.concat(Account.settings(address))) {
            body json.toString()
        }

        if(hasErrors(resp))
            return JSON.parse(resp.text)

        def args  = getJSONmap(JSON.parse(resp.body));
        handleResponse(resp , new Account(), args)
    }

    /**
     *
     * @param acctSecret
     * @param pay
     * @return PaymentResponse Object
     *
     * Usage:
     * rippleRestClientService.postPayment({Secret}){
     source_account = {Address}destination_account ="{Address}"
     destination_amount =  new Amount(value: ".0001",currency: "XRP" ,issuer: "")
     }
     */
    def postPayment(acctSecret, Closure pay) {
        Payment thePayment = new Payment();
        delegate.createEntity(thePayment, pay)

        def json = new JsonBuilder()
        def tmpPayment = delegate.current
        def val = json {
            secret acctSecret
            client_resource_id resourceId
            payment tmpPayment
        }
        def resp = restBuilder.post(serverUrl.concat("payments")) {
            body json.toString()
        }

        if(hasErrors(resp))
            return JSON.parse(resp.text)

        def args  = getJSONmap(JSON.parse(resp.body));
        handleResponse(resp , new PaymentResponse(), args)
    }

    /**
     *
     * @param acctSecret
     * @param address
     * @param trustArgs - possible args = [limit(int), currency(String),counterparty(String),allows_rippling(bool)]
     * Usage:
     * def response = rippleRestClientService.grantTrustLine({Secret},{Address}){
     limit = 5
     currency ="USD"
     counterparty = "{counter party's address}"
     allows_rippling = true
     }* @return TrustLineGrant Object
     */
    def grantTrustLine(String acctSecret, String address, Closure trustArgs) {
        TrustLineGrant trustL = new TrustLineGrant()
        delegate.createEntity(trustL, trustArgs)

        def json = new JsonBuilder()
        def tmpTrust = delegate.current
        def val = json {
            secret acctSecret
            trustline tmpTrust
        }

        def resp = restBuilder.post(serverUrl.concat(TrustLine.grantTrustline(address))) {
            body json.toString()
        }

        if(hasErrors(resp))
            return JSON.parse(resp.text)

        def args  = getJSONmap(JSON.parse(resp.body).trustline);
        handleResponse(resp , new TrustLineGrant(), args)

    }

    /**
     * set the resource id
     * @param newId
     */
    def setResourceId(String newId) {
        this.resourceId = newId

    }

    /**
     * generates a new resource Id to be used for sending payments.
     * YOU MUST GENERATE A NEW RESOURCE ID with every new payment
     * this helps prevent double spending
     *
     */
    def generateNewResourceId() {
        this.resourceId = getUuid()
    }

   private def handleResponse(resp, entity, args) {

        delegate.update(entity,args)

        return  entity

    }

    private def handleResponse(resp) {

        if(hasErrors(resp))
            return JSON.parse(resp.text)

        def obj = JSON.parse(resp.body)

        return obj

    }

    private def hasErrors(resp){
        if (resp instanceof ErrorResponse) {
            return true
        }

        if(JSON.parse(resp.body).success == false){
            return true

        }
        return false
    }


    private def getJSONmap(resp){

        return new JsonSlurper().parseText(resp.toString())

    }


}
