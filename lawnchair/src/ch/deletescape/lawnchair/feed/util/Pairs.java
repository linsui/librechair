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

package ch.deletescape.lawnchair.feed.util;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "WeakerAccess"})
public final class Pairs {
    private Pairs() {
        throw new RuntimeException("Instantiation ugh");
    }

    public static <Ta, Tb> Pair<Ta, Tb> cons(Ta a, Tb b) {
        return function -> function.apply(a, b);
    }

    public static <Ta, Tb> Ta car(Pair<Ta, Tb> pair) {
        return (Ta) pair.apply((a, b) -> a);
    }

    public static <Ta, Tb> Tb cdr(Pair<Ta, Tb> pair) {
        return (Tb) pair.apply((a, b) -> b);
    }

    public interface Pair<Ta, Tb> extends Function<BiFunction<Ta, Tb, Object>, Object> {
        default Ta car() {
            return Pairs.car(this);
        }

        default Tb cdr() {
            return Pairs.cdr(this);
        }
    }
}