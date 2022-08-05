package io.github.drmanganese.endercrop;

import java.util.Random;

public class MathExt {
    public static final Random Rnd = new Random();
    public static float RandomBetween(float min, float max) {
        return min + Rnd.nextFloat() * (max - min);
    }
}
