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

enum class TokenType(val numeric: Int = 0) {
    UNKNOWN,
    WHITE,
    INTEGER,
    WILDCARD,  // '*'
    COMMA,
    RANGE, // '-'
    SLASH, // '/'
    SYMBOL,
    SUN(0), MON(1), TUE(2), WED(3), THU(4), FRI(5), SAT(6),
    JAN(1), FEB(2), MAR(3),
    APR(4), MAY(5), JUN(6),
    JUL(7), AUG(8), SEP(9),
    OCT(10), NOV(11), DEC(12),
    EOF;

    companion object {
        fun match(term: String) =
            try {
                valueOf(term)
            } catch (unknown: IllegalArgumentException) {
                UNKNOWN
            }

        fun typeFor(c: Char) = when {
            Character.isWhitespace(c) -> WHITE
            Character.isDigit(c) -> INTEGER
            c == '*' -> WILDCARD
            c == ',' -> COMMA
            c == '-' -> RANGE
            c == '/' -> SLASH
            else -> SYMBOL
        }
    }
}

data class Token(val type: TokenType, val text: String = "")

object EOFException : Exception()
