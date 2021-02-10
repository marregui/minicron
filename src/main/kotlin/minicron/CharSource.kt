/*
 * Licensed to Miguel Arregui ("marregui") under one or more contributor
 * license agreements. See the LICENSE file distributed with this work
 * for additional information regarding copyright ownership. You may
 * obtain a copy at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * Copyright 2020, Miguel Arregui a.k.a. marregui
 */

package minicron

open class CharSource(line: String) {

    private val buffer: CharArray = line.toCharArray()
    private var offset: Int = 0

    val length get() = buffer.size


    fun read(): Char {
        if (offset >= buffer.size) {
            throw EOFException
        }
        return buffer[offset++]
    }

    fun offset() = offset - 1

    fun back(n: Int = 1) {
        offset = if (offset - n >= 0) offset - n else 0
    }

    fun text(start: Int = offset, end: Int = buffer.size) =
        if (start >= 0 && start < buffer.size && end > start && end <= buffer.size) {
            String(buffer, start, end - start)
        } else {
            ""
        }
}