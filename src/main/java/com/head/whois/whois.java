package com.head.whois;

import org.apache.commons.net.whois.WhoisClient;
import java.io.IOException;

/**
 * Created by user on 17-1-10.
 */
public class whois {
    public static void main(String[] args)
    {
        String  strAddr = "www.baidu.com" ;
        if ( args.length > 0 )
            strAddr = args[0] ;

        System.out.println("=====================whois: " + strAddr);
        String strGet =query( strAddr ) ;
        if ( strGet != null )
            System.out.println( strGet );
        else
            System.out.println( "Error!" );
    }

    public static String query( String strAddr )
    {
        WhoisClient whois = new WhoisClient();
        String      strQuery = null ;
        try {
            whois.connect(WhoisClient.DEFAULT_HOST);
            strQuery = whois.query(strAddr) ;
            whois.disconnect();
        } catch(IOException e) {
            System.err.println("Error I/O exception: " + e.getMessage());
            strQuery = null ;
        }
        return strQuery ;
    }


}
