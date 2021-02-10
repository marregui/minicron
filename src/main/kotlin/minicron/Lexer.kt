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

class Lexer(line: String) : CharSource(line) {

    private var l1: Token? = null

    val token: Token get() {
        var token: Token
        if (l1 != null) {
            token = l1!!
            l1 = null
        } else {
            do {
                token = nextToken()
            } while (token.type === TokenType.WHITE)
        }
        return token
    }

    fun lookAhead(): Token = token.also { l1 = it }

    private fun nextToken(): Token {
        var token: Token
        var type: TokenType? = null
        var start = -1
        var chr = 0.toChar()
        try {
            mainLoop@ while (true) {
                chr = read()
                start = offset()
                type = TokenType.typeFor(chr)
                when (type) {
                    TokenType.WHITE -> {
                        while (Character.isWhitespace(read())) {
                            /* consume */
                        }
                        back()
                        token = Token(type, text(start, offset() + 1))
                        break@mainLoop
                    }
                    TokenType.INTEGER -> {
                        while (Character.isDigit(read())) {
                            /* consume */
                        }
                        back()
                        token = Token(type, text(start, offset() + 1))
                        break@mainLoop
                    }
                    TokenType.WILDCARD,
                    TokenType.COMMA,
                    TokenType.RANGE,
                    TokenType.SLASH ->  {
                        token = Token(type, chr.toString())
                        break@mainLoop
                    }
                    TokenType.SYMBOL -> {
                        val sym = "$chr${read()}${read()}"
                        val symType = TokenType.match(sym)
                        if (symType != TokenType.UNKNOWN) {
                            token = Token(TokenType.INTEGER, symType.numeric.toString())
                            break@mainLoop
                        }
                        back(2)
                        throw LexicalError(offset(), chr, type)
                    }
                    else -> throw LexicalError(offset(), chr)
                }
            }
        } catch (eof: EOFException) {
            val text = text(start)
            token = if (text.isNotEmpty()) {
                when (type) {
                    TokenType.WHITE, TokenType.INTEGER -> Token(type, text)
                    TokenType.SYMBOL -> {
                        back(text.length - 1)
                        throw LexicalError(offset(), chr, TokenType.SYMBOL)
                    }
                    else -> throw LexicalError(offset(), chr)
                }
            } else {
                Token(TokenType.EOF)
            }
        }
        return token
    }
}
