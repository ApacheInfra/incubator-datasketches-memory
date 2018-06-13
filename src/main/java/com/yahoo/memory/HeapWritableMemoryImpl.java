/*
 * Copyright 2018, Yahoo! Inc. Licensed under the terms of the
 * Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.memory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Implementation of {@link WritableMemory} for heap-based, native byte order.
 *
 * @author Roman Leventov
 * @author Lee Rhodes
 */
class HeapWritableMemoryImpl extends WritableMemoryImpl {
  private final Object unsafeObj;

  HeapWritableMemoryImpl(
      final Object unsafeObj,
      final long regionOffset,
      final long capacityBytes,
      final boolean readOnly) {
    super(unsafeObj, 0, regionOffset, capacityBytes, readOnly, null, null);
    this.unsafeObj = unsafeObj;
  }

  @Override
  public ByteBuffer getByteBuffer() {
    return null;
  }

  @Override
  public ByteOrder getByteOrder() {
    return Util.nativeOrder;
  }

  @Override //TODO remove from baseWMemImpl NOTE WRITABLE ONLY
  public MemoryRequestServer getMemoryRequestServer() {
    return null;
  }

  @Override
  long getNativeBaseOffset() {
    return 0;
  }

  @Override
  Object getUnsafeObject() {
    return unsafeObj;
  }

  @Override
  public boolean isValid() {
    return true;
  }

}
