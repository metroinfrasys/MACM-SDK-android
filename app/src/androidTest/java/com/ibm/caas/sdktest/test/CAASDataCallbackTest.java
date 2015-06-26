/*
 ********************************************************************
 * Licensed Materials - Property of IBM                             *
 *                                                                  *
 * Copyright IBM Corp. 2015 All rights reserved.                    *
 *                                                                  *
 * US Government Users Restricted Rights - Use, duplication or      *
 * disclosure restricted by GSA ADP Schedule Contract with          *
 * IBM Corp.                                                        *
 *                                                                  *
 * DISCLAIMER OF WARRANTIES. The following [enclosed] code is       *
 * sample code created by IBM Corporation. This sample code is      *
 * not part of any standard or IBM product and is provided to you   *
 * solely for the purpose of assisting you in the development of    *
 * your applications. The code is provided "AS IS", without         *
 * warranty of any kind. IBM shall not be liable for any damages    *
 * arising out of your use of the sample code, even if they have    *
 * been advised of the possibility of such damages.                 *
 ********************************************************************
 */

package com.ibm.caas.sdktest.test;

import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;

/**
 * A generic callback used for unit-testing.
 */
public class CAASDataCallbackTest<T> implements CAASDataCallback<T> {
  boolean successful = false;
  T result;
  CAASErrorResult error;

  @Override
  public final void onSuccess(T t) {
    result = t;
    successful = true;
    notifyCompletion();
  }

  @Override
  public final void onError(CAASErrorResult error) {
    //System.out.println("onError(" + error + ")");
    this.error = error;
    successful = false;
    notifyCompletion();
  }

  synchronized void notifyCompletion() {
    notifyAll();
  }

  public void awaitCompletion() {
    awaitCompletion(-1L);
  }

  public synchronized void awaitCompletion(long timeout) {
    try {
      if (timeout <= 0L) wait();
      else wait(timeout);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}