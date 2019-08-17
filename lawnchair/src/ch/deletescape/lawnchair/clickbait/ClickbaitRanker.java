/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.clickbait;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClickbaitRanker {

    public static String[] COMMON_VOCAB = new String[]{
            "Why", "Who", "What", "When",
            "He", "She", "Will", "The",
            "Were", "Was", "Sleep", "iPhone",
            "Apple", "Google", "Tracks", "You",
            "Latest", "New", "Cool", "Additional",
            "Tips", "100", "Get", "Started"
    };


    public static final int CAPS_PRIZE = 1;
    public static final int COMMON_VOCAB_PRIZE = 2;
    public static final int SYMBOL_PRIZE = -1;
    public static final int TITLE_MATCH_PRIZE = 5;

    public static class Token {

        public String text;
        public int confidence;
    }

    public static List<Token> rank(String string) {
        return rank(string.split(" "));
    }

    public static List<Token> rank(String[] strings) {
        return rank(Arrays.stream(strings).map(it -> {
            Token token = new Token();
            token.text = it;
            return token;
        }).collect(Collectors.toList()));
    }

    public static List<Token> rank(List<Token> tokens) {
        tokens.forEach(token -> {
            token.confidence = 0;
            if (tokens.indexOf(token) == 0) {
                if (token.text.length() >= 1 && Character.isUpperCase(token.text.charAt(0))) {
                    token.confidence += TITLE_MATCH_PRIZE + CAPS_PRIZE;
                }
                if (Arrays.stream(COMMON_VOCAB).anyMatch(it -> token.text.equalsIgnoreCase(it))) {
                    token.confidence += TITLE_MATCH_PRIZE + COMMON_VOCAB_PRIZE;
                }
            } else {
                if (Arrays.stream(COMMON_VOCAB).anyMatch(it -> token.text.equalsIgnoreCase(it))) {
                    token.confidence += COMMON_VOCAB_PRIZE;
                }
                if (token.text.length() >= 1 && Character.isUpperCase(token.text.charAt(0))) {
                    token.confidence += CAPS_PRIZE;
                }
            }
        });
        return tokens;
    }

    public static List<Token> assemble(List<Token> tokens) {
        int confidence = 0;
        for (Token token : tokens) {
            confidence += token.confidence;
        }
        int finalConfidence = confidence;
        return tokens.stream()
                .map(it -> tokens.indexOf(it) == 0 || finalConfidence < 15 ? it
                        : ((Function<Token, Token>) tok -> {
                            tok.text = tok.text.toLowerCase();
                            return tok;
                        }).apply(it)).collect(Collectors.toList());
    }

    public static String assembleToString(List<Token> tokens) {
        return assemble(tokens).stream().map(it -> it.text).collect(Collectors.joining(" "));
    }

    public static String completePipeline(String string) {
        return assembleToString(rank(string));
    }
}
