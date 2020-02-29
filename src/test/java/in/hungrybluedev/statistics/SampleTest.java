package in.hungrybluedev.statistics;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.Arrays;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class SampleTest {

    private static final int TEST_COUNT = 100;
    private static final int RANDOM_MAX = 1000;
    private static final double EPSILON = 1e-10;

    private static Random random = new Random();

    private static int getRandomSampleSize() {
        return random.nextInt(RANDOM_MAX) + Sample.getThreshold();
    }

    /**
     * Implementation of the classic Fischer-Yates shuffle algorithm.
     *
     * @param observation The array of observations to be shuffled.
     */
    private static void shuffle(final double[] observation) {
        for (int i = observation.length - 1; i >= 1; i--) {
            int j = random.nextInt(i);
            swap(observation, i, j);
        }
    }

    private static void swap(double[] observation, int i, int j) {
        double temp = observation[i];
        observation[i] = observation[j];
        observation[j] = temp;
    }

    @BeforeMethod
    public void setUp() {
        Sample.resetStates();
    }

    @AfterMethod
    public void tearDown() {
        Sample.resetStates();
    }

    @org.testng.annotations.Test
    public void testThreshold() {
        int[] testThresholds = {30, 20, 10, 40, 100, 2};

        for (int threshold : testThresholds) {

            // The point is to ensure that the sample size is never below
            // the minimum threshold. If it is lower, then an exception
            // should be thrown. Otherwise everything should work.
            if (threshold < Sample.MINIMUM_THRESHOLD) {

                assertThrows(IllegalArgumentException.class, () -> Sample.setThreshold(threshold));

            } else {

                Sample.setThreshold(threshold);
                assertEquals(Sample.getThreshold(), threshold);

            }
        }

        Sample.setThreshold(Sample.DEFAULT_THRESHOLD);
    }

    @org.testng.annotations.Test
    public void testGetSummary() {
        String expected = "Summary Statistics for Sample: Test sample\n" +
                "\n" +
                "Count   :          50\n" +
                "Sum     : 1225.000 km\n" +
                "Mean    :   24.500 km\n" +
                "Variance:  208.250 km\n" +
                "Std Dev :   14.431 km\n";

        SampleBuilder builder = new SampleBuilder("Test sample", "km");

        for (int i = 0; i < 50; i++) {
            builder.addObservation(i);
        }

        Sample sample = builder.buildSample();

        assertEquals(sample.getSummary().toString(), expected);
    }

    @org.testng.annotations.Test
    public void testGetCount() {
        for (int i = 1; i <= TEST_COUNT; i++) {
            final int n = getRandomSampleSize();
            final double[] observations = new double[n];

            Sample sample = new Sample("Sample number " + i, null, observations);
            assertEquals(sample.getCount(), n);
        }
    }

    @org.testng.annotations.Test
    public void testGetSum() {
        for (int i = 1; i <= TEST_COUNT; i++) {
            final int n = getRandomSampleSize();
            final double[] observations = new double[n];

            for (int j = 0; j < n; j++) {
                observations[j] = (j + 1);
            }

            shuffle(observations);

            Sample sample = new Sample("Sample number " + i, "cm", observations);
            assertEquals(sample.getSum(), n * (n + 1.0) / 2, EPSILON);
        }
    }

    @org.testng.annotations.Test
    public void testGetMean() {
        for (int i = 1; i <= TEST_COUNT; i++) {
            final int n = getRandomSampleSize();
            final double[] observations = new double[n];

            for (int j = 0; j < n; j++) {
                observations[j] = (j + 1);
            }

            shuffle(observations);

            Sample sample = new Sample("Sample number " + i, "A", observations);
            assertEquals(sample.getMean(), (n + 1.0) / 2, EPSILON);
        }
    }

    @org.testng.annotations.Test
    public void testZeroVariance() {
        for (int i = 1; i <= TEST_COUNT; i++) {
            final int n = getRandomSampleSize();
            final double[] observations = new double[n];
            final double constant = random.nextInt(RANDOM_MAX);

            Arrays.fill(observations, constant);

            Sample sample = new Sample("Sample number " + i, "J", observations);
            assertEquals(sample.getVariance(), 0, EPSILON);
            assertEquals(sample.getStdDev(), 0, EPSILON);
        }
    }

    @org.testng.annotations.Test
    public void testStdDev() {
        for (int i = 1; i <= TEST_COUNT; i++) {
            final int n = getRandomSampleSize() * 2;
            final double[] observations = new double[n];

            double mean = random.nextDouble();
            double error = random.nextDouble();

            int factor = -1;

            for (int j = 0; j < n; j++) {
                observations[j] = mean + error * factor;
                factor *= -1;
            }

            Sample sample = new Sample("Gaussian Sample #" + i, null, observations);
            assertEquals(sample.getMean(), mean, EPSILON);
            assertEquals(sample.getStdDev(), error, EPSILON);
        }
    }

    @org.testng.annotations.Test
    public void testTestSampleToString() {
        String expectedResult = "0.0 km\n" +
                "1.0 km\n" +
                "2.0 km\n" +
                "3.0 km\n" +
                "4.0 km\n" +
                "5.0 km\n" +
                "6.0 km\n" +
                "7.0 km\n" +
                "8.0 km\n" +
                "9.0 km\n" +
                "10.0 km\n" +
                "11.0 km\n" +
                "12.0 km\n" +
                "13.0 km\n" +
                "14.0 km\n" +
                "15.0 km\n" +
                "16.0 km\n" +
                "17.0 km\n" +
                "18.0 km\n" +
                "19.0 km\n" +
                "20.0 km\n" +
                "21.0 km\n" +
                "22.0 km\n" +
                "23.0 km\n" +
                "24.0 km\n" +
                "25.0 km\n" +
                "26.0 km\n" +
                "27.0 km\n" +
                "28.0 km\n" +
                "29.0 km\n" +
                "30.0 km\n" +
                "31.0 km\n" +
                "32.0 km\n" +
                "33.0 km\n" +
                "34.0 km\n" +
                "35.0 km\n" +
                "36.0 km\n" +
                "37.0 km\n" +
                "38.0 km\n" +
                "39.0 km\n" +
                "40.0 km\n" +
                "41.0 km\n" +
                "42.0 km\n" +
                "43.0 km\n" +
                "44.0 km\n" +
                "45.0 km\n" +
                "46.0 km\n" +
                "47.0 km\n" +
                "48.0 km\n" +
                "49.0 km\n" +
                "\n" +
                "Summary Statistics for Sample: Test sample\n" +
                "\n" +
                "Count   :          50\n" +
                "Sum     : 1225.000 km\n" +
                "Mean    :   24.500 km\n" +
                "Variance:  208.250 km\n" +
                "Std Dev :   14.431 km\n";

        SampleBuilder builder = new SampleBuilder("Test sample", "km");

        for (int i = 0; i < 50; i++) {
            builder.addObservation(i);
        }

        Sample sample = builder.buildSample();

        assertEquals(sample.toString(), expectedResult);
    }
}