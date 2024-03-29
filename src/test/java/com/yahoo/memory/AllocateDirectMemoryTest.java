/*
 * Copyright 2017, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class AllocateDirectMemoryTest {

  @Test
  public void simpleAllocateDirect() {
    int longs = 32;
    WritableMemory wMem1;
    try (WritableHandle wh = WritableMemory.allocateDirect(longs << 3)) {
      wMem1 = wh.get();
      for (int i = 0; i<longs; i++) {
        wMem1.putLong(i << 3, i);
        assertEquals(wMem1.getLong(i << 3), i);
      }
    }
    try {
      wMem1.checkValid();
      fail();
    } catch (IllegalStateException e) {
      //ok
    }
  }

  @Test
  public void checkDefaultMemoryRequestServer() {
    int longs1 = 32;
    int bytes1 = longs1 << 3;
    try (WritableHandle wh = WritableMemory.allocateDirect(bytes1)) {
      WritableMemory origWmem = wh.get();
      for (int i = 0; i<longs1; i++) { //puts data in wMem1
        origWmem.putLong(i << 3, i);
        assertEquals(origWmem.getLong(i << 3), i);
      }
      println(origWmem.toHexString("Test", 0, 32 * 8));

      int longs2 = 64;
      int bytes2 = longs2 << 3;
      MemoryRequestServer memReqSvr = origWmem.getMemoryRequestServer();
      WritableMemory newWmem = memReqSvr.request(bytes2);
      assertFalse(newWmem.isDirect()); //on heap by default
      for (int i = 0; i < longs2; i++) {
          newWmem.putLong(i << 3, i);
          assertEquals(newWmem.getLong(i << 3), i);
      }
      memReqSvr.requestClose(origWmem, newWmem);
      //The default MRS doesn't actually release because it could be misused.
      // so we let the TWR release it.
    }
  }

  @Test
  public void checkNullMemoryRequestServer() {
    try (WritableHandle wh = WritableMemory.allocateDirect(128, null)) {
      WritableMemory wmem = wh.get();
      assertNotNull(wmem.getMemoryRequestServer());


    }
  }


  @Test
  public void checkNonNativeDirect() { //not allowed in public API
    try (WritableDirectHandle h =
        BaseWritableMemoryImpl.wrapDirect(8, Util.nonNativeOrder, null)) {
      WritableMemory wmem = h.get();
      wmem.putChar(0, (char) 1);
      assertEquals(wmem.getByte(1), (byte) 1);
    }
  }

  @Test
  public void checkExplicitClose() {
    final long cap = 128;
    try (WritableDirectHandle wdh = WritableMemory.allocateDirect(cap)) {
      wdh.close(); //explicit close. Does the work of closing
      wdh.direct.close(); //redundant
    } //end of scope call to Cleaner/Deallocator also will be redundant
  }

  @AfterClass
  public void checkDirectCounter() {
    final long count = BaseState.getCurrentDirectMemoryAllocations();
    if (count != 0) {
      println(""+count);
      fail();
    }
  }

  @Test
  public void printlnTest() {
    println("PRINTING: "+this.getClass().getName());
  }

  /**
   * @param s value to print
   */
  static void println(String s) {
    //System.out.println(s); //disable here
  }
}
