package farm.inventory.product.data;

import java.util.Random;

public class RandomQuality {

    private Random rand;
    private static Long seed = null;

    public RandomQuality() {
        rand = new Random();
        if (seed != null) {
            setRandomWithSeed(seed);
        }
    }

    public static void setSeed(Long seed) {
        RandomQuality.seed = seed;
    }

    public void setRandomWithSeed(long seed) {
        rand.setSeed(seed);
    }

    public Quality getRandomQuality() {
        int value = rand.nextInt(10);
        return switch (value) {
            case 0, 1, 2, 3 -> Quality.REGULAR;
            case 4, 5, 6 -> Quality.SILVER;
            case 7, 8 -> Quality.GOLD;
            case 9 -> Quality.IRIDIUM;
            default -> Quality.REGULAR;
        };
    }
}
