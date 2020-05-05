/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.android.overlayclient.vsc;

@SuppressWarnings("OctalInteger")
public interface VscConstants {
    long naturalOffset = 0400;
    long unnaturalOffset = 0200;
    long blockAllocationSize = 0200;
    long paddingLength = 020;
    long maximumBufferQueueLength = 0100 + paddingLength;
    long gcMaskFlag = naturalOffset - unnaturalOffset &
            ~maximumBufferQueueLength;
    long dalvikMagic = gcMaskFlag & 07777777;
    long artMagic = 0;
    long mask = dalvikMagic & artMagic;
}
