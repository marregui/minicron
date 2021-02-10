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

/**
 * Supported constructs:
 *
 *  * *
 *  * *\/2
 *  * 0-4,5-10/2,13,14,16-20/3
 *  * SUN, MON, TUE-THU/1, THU-SAT
 *  * 1-6/2, 6-8, SEP, NOV-DEC
 *
 */
class CRONParser constructor(private val shiftedDaysOfWeek: Boolean = false) {

    enum class Field(val start: Int, val end: Int, vararg chars: Char) {
        MINUTES(0, 59),
        HOURS(0, 23),
        DAY_OF_MONTH(1, 31),
        MONTH(1, 12),
        DAY_OF_WEEK(0, 6), YEAR(1970, 2099)
    }

    private fun getFieldStart(field: Field) =
        if (field == Field.DAY_OF_WEEK && shiftedDaysOfWeek)
            field.start + 1
        else field.start

    private fun getFieldEnd(field: Field) =
        if (field === Field.DAY_OF_WEEK && shiftedDaysOfWeek)
            field.end + 1
        else field.end

    fun parseField(field: Field, str: String): Set<Int> {
        val values = sortedSetOf<Int>()
        val lexer = Lexer(str)
        var token = lexer.token
        when (token.type) {
            TokenType.WILDCARD -> when (lexer.lookAhead().type) {
                TokenType.SLASH -> {
                    lexer.token // consume slash
                    if (lexer.lookAhead().type != TokenType.INTEGER) {
                        throw SyntaxError(lexer.offset(), "missing step after '/'")
                    }
                    val step = lexer.token.text.toInt()
                    if (step <= 0) {
                        throw SyntaxError(lexer.offset(), "step must be > 0")
                    }
                    var i = getFieldStart(field)
                    while (i <= getFieldEnd(field)) {
                        values.add(i)
                        i += step
                    }
                    if (lexer.lookAhead().type != TokenType.EOF) {
                        throw SyntaxError(
                            lexer.offset(),
                            "wildcard must be followed by either end of string or a '/' followed by the step"
                        )
                    }
                    lexer.token // consume EOF
                }
                TokenType.EOF -> {
                    var i = getFieldStart(field)
                    while (i <= getFieldEnd(field)) {
                        values.add(Integer.valueOf(i))
                        i++
                    }
                    lexer.token // consume EOF
                }
                else -> throw SyntaxError(
                    lexer.offset(),
                    "wildcard must be followed by either end of string or a '/' followed by the step"
                )
            }
            TokenType.INTEGER -> specLoop@ while (true) {
                when (lexer.lookAhead().type) {
                    TokenType.RANGE -> {
                        lexer.token // consume range
                        if (lexer.lookAhead().type != TokenType.INTEGER) {
                            throw SyntaxError(lexer.offset(), "missing upper bound in range")
                        }
                        val start = token.text.toInt()
                        val end = lexer.token.text.toInt()
                        var step = 1
                        var lookAhead = lexer.lookAhead()
                        if (lookAhead.type == TokenType.SLASH) {
                            lexer.token // consume the slash
                            if (lexer.lookAhead().type != TokenType.INTEGER) {
                                throw SyntaxError(lexer.offset(), "missing step after slash")
                            }
                            step = lexer.token.text.toInt()
                            if (step <= 0) {
                                throw SyntaxError(lexer.offset(), "step must be > 0")
                            }
                            lookAhead = lexer.lookAhead()
                        }
                        if (start < getFieldStart(field) || start > getFieldEnd(field)) {
                            throw SyntaxError(
                                lexer.offset(),
                                    "start value out of range: $start not in [${getFieldStart(field)}..${getFieldEnd(field)}]")
                        }
                        if (end < getFieldStart(field) || end > getFieldEnd(field)) {
                            throw SyntaxError(
                                lexer.offset(),
                                    "end value out of range: $end not in [${getFieldStart(field)}..${getFieldEnd(field)}]")
                        }
                        var i = start
                        while (i <= end) {
                            values.add(Integer.valueOf(i))
                            i += step
                        }
                        token = when (lookAhead.type) {
                            TokenType.COMMA -> {
                                lexer.token // consume comma
                                if (lexer.lookAhead().type != TokenType.INTEGER) {
                                    throw SyntaxError(lexer.offset(), "only numbers can follow a comma")
                                }
                                lexer.token // prepare next number
                            }
                            TokenType.EOF -> {
                                lexer.token // consume EOF
                                break@specLoop
                            }
                            else -> throw SyntaxError(
                                lexer.offset(),
                                    "unexpected token '${lexer.token.text}' while looking for the end of the string or a ','")
                        }
                    }
                    TokenType.COMMA -> {
                        val value = token.text.toInt()
                        if (value < getFieldStart(field) || value > getFieldEnd(field)) {
                            throw SyntaxError(
                                lexer.offset(),
                                    "value out of range: $value not in [${getFieldStart(field)}..${getFieldEnd(field)}]")
                        }
                        values.add(value)
                        lexer.token // consume comma
                        token = when (lexer.lookAhead().type) {
                            TokenType.INTEGER -> lexer.token // prepare next number
                            TokenType.EOF -> {
                                lexer.token // consume EOF
                                break@specLoop
                            }
                            else -> throw SyntaxError(lexer.offset(), "only numbers can follow a comma")
                        }
                    }
                    TokenType.SLASH -> {
                        lexer.token // consume slash
                        if (lexer.lookAhead().type != TokenType.INTEGER) {
                            throw SyntaxError(lexer.offset(), "Missing step after '/'")
                        }
                        val step = lexer.token.text.toInt()
                        if (step <= 0) {
                            throw SyntaxError(lexer.offset(), "step must be > 0")
                        }
                        val start = token.text.toInt()
                        if (start < getFieldStart(field) || start > getFieldEnd(field)) {
                            throw SyntaxError(
                                lexer.offset(),
                                    "start value out of range: $start not in [${getFieldStart(field)}..${getFieldEnd(field)}]")
                        }
                        var i: Int = start
                        while (i <= getFieldEnd(field)) {
                            values.add(i)
                            i += step
                        }
                        token = when (lexer.lookAhead().type) {
                            TokenType.COMMA -> {
                                lexer.token // consume comma
                                if (lexer.lookAhead().type != TokenType.INTEGER) {
                                    throw SyntaxError(lexer.offset(), "only numbers can follow a comma")
                                }
                                lexer.token // prepare next number
                            }
                            TokenType.EOF -> {
                                lexer.token // consume EOF
                                break@specLoop
                            }
                            else -> throw SyntaxError(
                                lexer.offset(),
                                    "unexpected token '${lexer.token.text}' while looking for the end of the string or a ','")
                        }
                    }
                    TokenType.EOF -> {
                        val value = token.text.toInt()
                        if (value < getFieldStart(field) || value > getFieldEnd(field)) {
                            throw SyntaxError(
                                lexer.offset(),
                                    "value out of range: $value not in [${getFieldStart(field)}..${getFieldEnd(field)}]")
                        }
                        values.add(value)
                        lexer.token // consume EOF
                        break@specLoop
                    }
                    else -> throw SyntaxError(
                        lexer.offset(),
                        "numbers must be followed by either '/' and step, or '-' and " +
                                "upper bound, or ',' and more numbers, or the end of string"
                    )
                }
            }
            TokenType.EOF -> throw SyntaxError(lexer.offset(), "unexpected end of string")
            else -> throw SyntaxError(lexer.offset(), "unexpected token '${token.text}'")
        }
        return values
    }
}
