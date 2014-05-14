package sim.entity

/**
 * Created by simeongbolo on 5/5/14.
 */
class TrustLine {


    public static final TRUSTLINE   = "accounts/{address}/trustlines"


   def authorized_by_counterparty // – Set to true if the counterparty has explicitly authorized the account to hold currency it issues. This is only necessary if the counterparty’s settings include require_authorization_for_incoming_trustlines
   def limit //– The maximum value of the currency that the account may hold issued by the counterparty
   def reciprocated_limit //– The maximum value of the currency that the counterparty may hold issued by the account
   def authorized_by_account //– Set to true if the account has explicitly authorized the counterparty to hold currency it issues. This is only necessary if the account’s settings include require_authorization_for_incoming_trustlines
   def counterparty_allows_rippling //– If true it indicates that the counterparty allows pairwise rippling out through this trustline
   def account_allows_rippling //– If true it indicates that the account allows pairwise rippling out through this trustline
   def hash// – If this object was returned by a historical query this value will be the hash of the transaction that modified this Trustline. The transaction hash is used throughout the Ripple Protocol to uniquely identify a particular transaction
   def counterparty //– The other party in this trustline
   def previous //– If the trustline was changed this will be a full Trustline object representing the previous values. If the previous object also had a previous object that will be removed to reduce data complexity. Trustline changes can be walked backwards by querying the API for previous.hash repeatedly
   def currency //– The code of the currency in which this trustline denotes trust
   def ledger //– The string representation of the index number of the ledger containing this trustline or, in the case of historical queries, of the transaction that modified this Trustline
   def account //– The account from whose perspective this trustline is being viewed

    public static final trustlineWithQuery(String address,def args){
         String s  = TrustLine.TRUSTLINE.replace("{address}",address)
        s=  s.concat("?")
        String q = ""
        args?.each{it->
            q= q.concat(it.key+"="+it.value+"&")
        }

        if(args){
          s = s.concat(q)[0..-2]
        }

        return s
    }

    public static final grantTrustline(String address){
         return TrustLine.TRUSTLINE.replace("{address}",address)
    }
}
