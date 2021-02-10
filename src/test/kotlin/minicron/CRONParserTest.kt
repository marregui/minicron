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
import java.util.*

class CRONParserTest {

    fun spit(field: CRONParser.Field, str: String) {
        println("$field [${field.start}..${field.end}]: $str")
        val parser = CRONParser()
        for (i in parser.parseField(field, str)) {
            print(" $i")
        }
        println("")
    }

    @Test
    fun test_spec_minutes() {
        val numbers = intArrayOf(1, 17, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58)
        val str = "17, 19/3, 1"
        testParsing(CRONParser.Field.MINUTES, str, numbers)
    }

//    @Test
//    @Throws(Exception::class)
//    fun specHours() {
//        val str = "0-4,5-10/2,13,14,16-20/3"
//        val numbers = intArrayOf(0, 1, 2, 3, 4, 5, 7, 9, 13, 14, 16, 19)
//        testParsing(str, CRONParser.Field.HOURS, numbers)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun specDayOfMonth() {
//        val numbers = intArrayOf(1, 17, 19, 22, 25, 28, 31)
//        val str = "17, 19/3, 1"
//        testParsing(str, CRONParser.Field.DAY_OF_MONTH, numbers)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun specYear() {
//        val numbers =
//            intArrayOf(2010, 2012, 2013, 2014, 2015, 2018, 2021, 2024, 2027, 2030, 2033, 2036, 2039, 2042, 2045)
//        val str = "2010, 2012-2015, 2018-2045/3"
//        testParsing(str, CRONParser.Field.YEAR, numbers)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun specDayOfWeek() {
//        val numbers = intArrayOf(0, 1, 2, 3, 4, 5, 6)
//        val str = "SUN, MON, TUE-THU/1, THU-SAT"
//        testParsing(str, CRONParser.Field.DAY_OF_WEEK, numbers)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun specMonth() {
//        val numbers = intArrayOf(1, 3, 5, 6, 7, 8, 9, 11, 12)
//        val str = "1-6/2, 6-8, SEP, NOV-DEC"
//        testParsing(str, CRONParser.Field.MONTH, numbers)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testNormalDayOfWeek() {
//        val numbers = intArrayOf(0, 1, 2, 3, 4, 5, 6)
//        var str = "0-6"
//        testParsing(str, CRONParser.Field.DAY_OF_WEEK, numbers)
//        val numbers1 = intArrayOf(0)
//        str = "0"
//        testParsing(str, CRONParser.Field.DAY_OF_WEEK, numbers1)
//        val numbers2 = intArrayOf(6)
//        str = "6"
//        testParsing(str, CRONParser.Field.DAY_OF_WEEK, numbers2)
//    }
//
//    @Test //(expected = SyntaxError.class)
//    @Throws(Exception::class)
//    fun testNegativeNormalDayOfWeek() {
//        val numbers = intArrayOf(7)
//        val str = "7"
//        testParsing(str, CRONParser.Field.DAY_OF_WEEK, numbers)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testShiftedDayOfWeek() {
//        val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7)
//        var str = "1-7"
//        testParsingWithShiftedDayOfWeek(str, CRONParser.Field.DAY_OF_WEEK, numbers)
//        val numbers1 = intArrayOf(1)
//        str = "1"
//        testParsingWithShiftedDayOfWeek(str, CRONParser.Field.DAY_OF_WEEK, numbers1)
//        val numbers2 = intArrayOf(7)
//        str = "7"
//        testParsingWithShiftedDayOfWeek(str, CRONParser.Field.DAY_OF_WEEK, numbers2)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testNegativeShiftedDayOfWeek() {
//        val numbers = intArrayOf(0)
//        val str = "0"
//        Assertions.assertThrows(SyntaxError::class.java) {
//            testParsingWithShiftedDayOfWeek(
//                str,
//                CRONParser.Field.DAY_OF_WEEK,
//                numbers
//            )
//        }
//    }

    @Test
    fun wrong() {
        val strs = arrayOf("", "*/", "*/*", "/", "*/0", "-12", "25", "100-200/2", "/3", "12,JU")
        val parser = CRONParser()
        for (str in strs) {
            parser.parseField( CRONParser.Field.MONTH, str)
        }
    }

    @Test
    fun test_wildcard_followed_by_step() {
        val parser = CRONParser()
        for (step in 1..10) {
            val str = "*/$step"
            for (field in CRONParser.Field.values()) {
                val result: List<Int> = ArrayList(parser.parseField(field, str))
                println("str:$str, field:$field, result:$result")
                var i = field.start
                var j = 0
                while (i <= field.end) {
                    assertThat(result[j], `is`(i))
                    i += step
                    j++
                }
            }
        }
    }

    private fun testParsingWithShiftedDayOfWeek(str: String, field: CRONParser.Field, expected: IntArray) {
        testParsing(field, str, expected, CRONParser(true))
    }

    private fun testParsing(field: CRONParser.Field, str: String, numbers: IntArray) {
        testParsing(field, str, numbers, CRONParser())
    }

    private fun testParsing(field: CRONParser.Field, str: String, expected: IntArray, parser: CRONParser) {
        assertThat(ArrayList(parser.parseField(field, str)), `is`(expected))
    }
}
