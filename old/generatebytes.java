package com.klemstinegroup.sunshinelab;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class generatebytes {
    public static void  main(String[] args){
        String boundary = "12345678901234567890"; // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.
        String out1 = "--" + boundary +
                CRLF + "Content-Disposition: form-data; name=\"file\"" +
                CRLF + "Content-Type: image/gif" +
                CRLF + CRLF;
//        GetString(data);
        String out2 = CRLF + "--" + boundary + "--" + CRLF;
        try {
            byte[] b1= out1.getBytes("US-ASCII");
            byte[] b2= out2.getBytes("US-ASCII");
            System.out.println(Arrays.toString(b1));
            System.out.println(Arrays.toString(b2));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
