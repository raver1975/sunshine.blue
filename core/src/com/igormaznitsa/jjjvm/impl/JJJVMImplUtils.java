package com.igormaznitsa.jjjvm.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class JJJVMImplUtils {

  private JJJVMImplUtils() {
  }

//  public static void makeAccessible(final AccessibleObject obj) {
//    if (obj != null && !obj.isAccessible()) {
//      AccessController.doPrivileged(new PrivilegedAction() {
//        public Object run() {
//          obj.setAccessible(true);
//          return null;
//        }
//      });
//    }
//  }

  public static void assertNotNull(final String text, final Object value) {
    if (value == null) {
      throw new NullPointerException(text);
    }
  }

  public static void skip(final DataInputStream stream, final int bytesToSkip) throws IOException {
    myskip(stream,bytesToSkip);
  }

  /**
   * Skips n bytes. Best effort.
   */
  public static void myskip(InputStream is, long n) throws IOException {
    while(n > 0) {
      long n1 = is.skip(n);
      if( n1 > 0 ) {
        n -= n1;
      } else if( n1 == 0 ) { // should we retry? lets read one byte
        if( is.read() == -1)  // EOF
          break;
        else
          n--;
      } else // negative? this should never happen but...
        throw new IOException("skip() returned a negative value. This should never happen");
    }
  }
}
