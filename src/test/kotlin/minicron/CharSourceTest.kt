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

import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`

class CharSourceTest {

    private val line = "tres tristes tigres triscaban trigo"

    @Test
    fun test_read_thought() {
        val charSource = CharSource(line)
        var offset = 0
        try {
            while (true) {
                assertThat(charSource.read(), `is`(line[offset]))
                assertThat(charSource.offset(), `is`(offset))
                offset++
            }
        } catch (e: EOFException) {
            assertThat(charSource.offset(), `is`(charSource.length - 1))
        }
    }

    @Test
    fun test_text() {
        val charSource = CharSource(line)
        assertThat(charSource.text(), `is`(line))
        val middle = line.length / 2
        for (i in 0 until middle) {
            charSource.read()
        }
        assertThat(charSource.offset(), `is`(middle - 1))
        assertThat(charSource.text(charSource.offset() + 1), `is`(line.substring(middle)))
    }

    @Test
    fun test_move_forwards_and_backwards() {
        val charSource = CharSource(line)
        val idx1 = 6
        for (i in 0 until idx1) {
            charSource.read()
        }
        assertThat(charSource.offset(), `is`(idx1 - 1))
        val idx2 = 7
        for (i in 0 until idx2) {
            charSource.read()
        }
        assertThat(charSource.offset(), `is`(idx1 + idx2 - 1))
        charSource.back(idx2)
        assertThat(charSource.offset(), `is`(idx1 - 1))
        for (i in 0 until idx2) {
            assertThat(charSource.read(), `is`(line[idx1 + i]))
            assertThat(charSource.offset(), `is`(idx1 + i))
        }
        charSource.back(idx1 + idx2)
        for (i in line.indices) {
            assertThat(charSource.read(), `is`(line[i]))
            assertThat(charSource.offset(), `is`(i))
        }
    }
}