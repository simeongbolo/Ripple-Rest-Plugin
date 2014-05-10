package sim.entity
/**
 * Created by simeongbolo on 5/4/14.
 */
class Account {

    public static final ACCOUNT_SETTINGS = "accounts/{address}/settings"
    public static final ACCOUNT_BALANCES = "accounts/{address}/balances"
    public static final ACCOUNT_NOTIFICATION = "accounts/{address}/notifications/{transaction_hash}"
    public static final ACCOUNT_PAYMENT = "accounts/{address}/payments/{hash}or{client_resource_id}"
    public static final ACCOUNT_PAYMENT_QUERY = "accounts/{address}/payments?"

    def secret //– The key that will be used to sign the transaction
    def address //– The Ripple address of the account in question
    Settings settings
    def url
    def hash
    def success
    def ledger

    public static String settings(String address){
        return ACCOUNT_SETTINGS.replace("{address}",address)
    }

    public static String balances(String address){
        return ACCOUNT_BALANCES.replace("{address}",address)
    }

    public static String notification(String address, hash){
        return ACCOUNT_NOTIFICATION.replace("{address}",address).replace("{transaction_hash}",hash)
    }

    public static String paymentWithHash(String address, String hash){
        return ACCOUNT_PAYMENT.replace("{address}",address).replace("{hash}or{client_resource_id}",hash)

    }

    public static String paymentWithUuid(String address,String uuid){
        return ACCOUNT_PAYMENT.replace("{address}",address).replace("{hash}or{client_resource_id}",uuid)
    }

    public static String paymentWithQuery(String address,def queryMap){
        String s = ACCOUNT_PAYMENT_QUERY.replace("{address}",address)
        String q = ""
        queryMap.each{it->
            q= q.concat(it.key+"="+it.value+"&")
        }
        return s.concat(q)[0..-2]


    }
}
