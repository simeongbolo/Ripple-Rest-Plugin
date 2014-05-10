package sim.entity
/**
 * Created by simeongbolo on 5/4/14.
 * Ripple Api payment Object for mapping requests
 */
class Payment {

    public static final PAYMENT_PATHS = "accounts/{address}/payments/paths/{destination_account}/{destination_amount}"
    public static final PAYMENT_PATHS_QUERY = "accounts/{address}/payments/paths/{destination_account}/{destination_amount}?"

    def source_account
    def destination_account
    Amount destination_amount
    Amount source_amount
    def source_tag
    def destination_tag
    def source_slippage
    def invoice_id
    def paths
    def direction
    def flag_no_direct_ripple
    def flag_partial_payment
    def partial_payment
    def timestamp
    Amount destination_balance_changes
    Amount source_balance_changes
    def state
    def no_direct_ripple
    def ledger
    def fee
    def earliest_first
    def hash
    def uuid
    def result

    public static final String paths(String source, String destination , Amount amount){
        if(amount.issuer){
            return PAYMENT_PATHS.replace("{address}",source).replace("{destination_account}",destination).replace("{destination_amount}",amount?.value+"+"+amount?.currency+"+"+amount?.issuer)
        }else{
            return PAYMENT_PATHS.replace("{address}",source).replace("{destination_account}",destination).replace("{destination_amount}",amount?.value+"+"+amount?.currency)

        }
    }

    public static  final String pathsQuery(String source, String destination , Amount amount,def sourceCurrencies){
        String s =  PAYMENT_PATHS_QUERY.replace("{address}",source).replace("{destination_account}",destination).replace("{destination_amount}",amount.value+"+"+amount.currency)
        if(amount.issuer){
             s =  PAYMENT_PATHS_QUERY.replace("{address}",source).replace("{destination_account}",destination).replace("{destination_amount}",amount.value+"+"+amount.currency+"+"+amount?.issuer)

        }
        sourceCurrencies.each{ it->
          s = s.concat(it+",")
        }
        return s[0..-2]
    }





}
