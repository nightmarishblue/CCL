package org.example.helper;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static <A, B> List<Pair<A, B>> zip(List<A> as, List<B> bs) {
        int size = Math.min(as.size(), bs.size());
        List<Pair<A, B>> output = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            output.add(Pair.of(as.get(i), bs.get(i)));
        }
        return output;
    }
}
