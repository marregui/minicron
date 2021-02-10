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
import org.junit.jupiter.api.Assertions.assertThrows
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import java.util.*


class LexerTest {

    @Test
    fun test_empty_line() {
        val lexer = Lexer("")
        assertThat(lexer.token, `is`(Token(TokenType.EOF)))
        assertThat(lexer.token, `is`(Token(TokenType.EOF)))
    }

    @Test
    fun test_random_text() {
        val lexer = Lexer("random")
        assertThrows(LexicalError::class.java,  { lexer.token }, "unexpected char 'r' at offset 0 while parsing SYMBOL")
    }

    @Test
    fun test_multi_char_symbol() {
        val line = "MON, TUE, WED, THU, FRI, SAT, SUN, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC"
        val types = arrayOf(
            TokenType.MON, TokenType.TUE, TokenType.WED,
            TokenType.THU, TokenType.FRI, TokenType.SAT, TokenType.SUN,
            TokenType.JAN, TokenType.FEB, TokenType.MAR,
            TokenType.APR, TokenType.MAY, TokenType.JUN,
            TokenType.JUL, TokenType.AUG, TokenType.SEP,
            TokenType.OCT, TokenType.NOV, TokenType.DEC
        )
        val comma = Token(TokenType.COMMA, ",")
        val tokens = arrayOfNulls<Token>(types.size * 2)
        for (i in types.indices) {
            val j = i * 2
            tokens[j] = Token(TokenType.INTEGER, types[i].numeric.toString())
            tokens[j + 1] = comma
        }
        tokens[tokens.size - 1] = Token(TokenType.EOF)
        checkLexing(line, tokens)
    }

    @Test
    fun test_parse_integers() {
        checkLexing(
            "1F2o3o4T",
            arrayOf(
                Token(TokenType.INTEGER, "1"),
                Token(TokenType.INTEGER, "2"),
                Token(TokenType.INTEGER, "3"),
                Token(TokenType.INTEGER, "4"),
                Token(TokenType.EOF, "")
            ),
            charArrayOf('F', 'o', 'o', 'T')
        )
    }

    @Test
    fun test_parse_whites() {
        val wildcard = Token(TokenType.WILDCARD, "*")
        checkLexing(
            "\n\n\n * \t\t\t * * \r\r\r",
            arrayOf(wildcard, wildcard, wildcard, Token(TokenType.EOF))
        )
    }

    @Test
    fun test_single_char_symbol() {
        val types = hashMapOf(
            TokenType.WILDCARD to '*',
            TokenType.COMMA to ',',
            TokenType.RANGE to '-',
            TokenType.SLASH to '/'
        )
        val unexpexted = charArrayOf('A')
        for ((type, chr) in types) {
            val line = "${chr}A$chr"
            val token = Token(type, chr.toString())
            val tokens = arrayOfNulls<Token>(3)
            Arrays.fill(tokens, 0, tokens.size - 1, token)
            tokens[tokens.size - 1] = Token(TokenType.EOF)
            checkLexing(line, tokens, unexpexted)
        }
    }

    private fun checkLexing(line: String, expectedTokens: Array<Token?>, unexpectedChars: CharArray = charArrayOf()) {
        val lexer = Lexer(line)
        var token: Token
        var tokenIdx = 0
        var unexpectedIdx = 0
        while (true) {
            try {
                token = lexer.token!!
                val expected = expectedTokens[tokenIdx++]!!
                assertThat(token.type, `is`(expected.type))
                assertThat(token.text, `is`(expected.text))
                if (token.type === TokenType.EOF) {
                    break
                }
            } catch (lerr: LexicalError) {
                assertThat(lerr.foundChr, `is`(unexpectedChars[unexpectedIdx++]))
            }
        }
        check(! (token.type !== TokenType.EOF || tokenIdx < expectedTokens.size || unexpectedIdx < unexpectedChars.size))
    }
}
