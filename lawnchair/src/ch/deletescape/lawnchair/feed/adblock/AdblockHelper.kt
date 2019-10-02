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

package ch.deletescape.lawnchair.feed.adblock

import android.annotation.SuppressLint
import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URL

@SuppressLint("StaticFieldLeak")
object AdblockHelper {
    lateinit var context: Context
    lateinit var filesDir: File
    lateinit var blocklist: File

    private val blockCache = mutableMapOf<String, Boolean>()
    private val BLOCKLIST = "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts"

    fun initialize() {
        filesDir = File(context.filesDir, "adblock")
        blocklist = File(filesDir, "blocklist")
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        if (!blocklist.exists()) {
            FileOutputStream(blocklist).apply {
                InputStreamReader(URL(BLOCKLIST).openStream()).forEachLine {
                    if (!it.startsWith("#")) {
                        write(it.toByteArray() + 10)
                    }
                }
            }
        }
    }

    fun shouldBlock(dom: String): Boolean {
        if (blocklist == null) {
            return false
        } else {
            if (blockCache.containsKey(dom)) {
                return blockCache[dom]!!
            }
            var block = false
            InputStreamReader(FileInputStream(blocklist)).forEachLine {
                if (it.split(" ")[1] == dom) {
                    block = true
                    return@forEachLine
                }
            }
            blockCache += dom to block
            return block
        }
    }
}