/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

public class IntArray implements Cloneable {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    int[] mValues;
    int mSize;

    private IntArray(int[] array, int size) {
        this.mValues = array;
        this.mSize = Preconditions.checkArgumentInRange(size, 0, array.length, "size");
    }

    public IntArray() {
        this(10);
    }

    public IntArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mValues = new int[0];
        } else {
            this.mValues = ArrayUtils.newUnpaddedIntArray(initialCapacity);
        }

        this.mSize = 0;
    }

    public static IntArray wrap(int[] array) {
        return new IntArray(array, array.length);
    }

    public static IntArray fromArray(int[] array, int size) {
        return wrap(Arrays.copyOf(array, size));
    }

    public void resize(int newSize) {
        Preconditions.checkArgumentNonnegative(newSize);
        if (newSize <= this.mValues.length) {
            Arrays.fill(this.mValues, newSize, this.mValues.length, 0);
        } else {
            this.ensureCapacity(newSize - this.mSize);
        }

        this.mSize = newSize;
    }

    public void add(int value) {
        this.add(this.mSize, value);
    }

    public void add(int index, int value) {
        this.ensureCapacity(1);
        int rightSegment = this.mSize - index;
        ++this.mSize;
        ArrayUtils.checkBounds(this.mSize, index);
        if (rightSegment != 0) {
            System.arraycopy(this.mValues, index, this.mValues, index + 1, rightSegment);
        }

        this.mValues[index] = value;
    }

    public int binarySearch(int value) {
        return ContainerHelpers.binarySearch(this.mValues, this.mSize, value);
    }

    public void addAll(IntArray values) {
        int count = values.mSize;
        this.ensureCapacity(count);
        System.arraycopy(values.mValues, 0, this.mValues, this.mSize, count);
        this.mSize += count;
    }

    private void ensureCapacity(int count) {
        int currentSize = this.mSize;
        int minCapacity = currentSize + count;
        if (minCapacity >= this.mValues.length) {
            int targetCap = currentSize + (currentSize < 6 ? 12 : currentSize >> 1);
            int newCapacity = targetCap > minCapacity ? targetCap : minCapacity;
            int[] newValues = ArrayUtils.newUnpaddedIntArray(newCapacity);
            System.arraycopy(this.mValues, 0, newValues, 0, currentSize);
            this.mValues = newValues;
        }

    }

    public void clear() {
        this.mSize = 0;
    }

    public IntArray clone() throws CloneNotSupportedException {
        IntArray clone = (IntArray)super.clone();
        clone.mValues = (int[])this.mValues.clone();
        return clone;
    }

    public int get(int index) {
        ArrayUtils.checkBounds(this.mSize, index);
        return this.mValues[index];
    }

    public void set(int index, int value) {
        ArrayUtils.checkBounds(this.mSize, index);
        this.mValues[index] = value;
    }

    public int indexOf(int value) {
        int n = this.mSize;

        for(int i = 0; i < n; ++i) {
            if (this.mValues[i] == value) {
                return i;
            }
        }

        return -1;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void remove(int index) {
        ArrayUtils.checkBounds(this.mSize, index);
        System.arraycopy(this.mValues, index + 1, this.mValues, index, this.mSize - index - 1);
        --this.mSize;
    }

    public int size() {
        return this.mSize;
    }

    public int[] toArray() {
        return Arrays.copyOf(this.mValues, this.mSize);
    }
}
