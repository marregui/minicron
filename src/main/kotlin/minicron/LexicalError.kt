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

import java.lang.Exception


class LexicalError : Exception {
    val foundChr: Char

    constructor(offset: Int, foundChr: Char) :
            super("unexpected char '$foundChr' at offset $offset") {
        this.foundChr = foundChr
    }

    constructor(offset: Int, foundChr: Char, type: TokenType?) :
            super("unexpected char '$foundChr' at offset $offset while parsing $type") {
        this.foundChr = foundChr
    }
}