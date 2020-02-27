package in.hungrybluedev.statistics;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Sample is an immutable collection of observations. There are statistics
 * associated with every Sample, such as mean, variance and standard
 * deviation. This class implements various methods to access these
 * desired statistics and also presents them in an organised manner
 * through the Summary subclass.
 * <p>
 * Note that the minimum number of elements in a Sample must be {@value #DEFAULT_THRESHOLD}
 * (or whatever the threshold is set to). This is because the formulas
 * implemented do not adjust for the decrease in degree of freedom.
 *
 * @author Subhomoy Haldar (@HungryBlueDev)
 * @noinspection WeakerAccess, unused
 */
public class Sample {
    /* Statically accessible parameters */

    /**
     * The threshold is the minimum number of elements that a Sample
     * must have to ensure accurate statistical analysis. The implementation
     * allows for some flexibility by providing methods to change the
     * threshold. However, some minimum restrictions apply.
     */
    public static final int DEFAULT_THRESHOLD = 40;

    /**
     * This is the minimum threshold value allowed for the Sample size. Any
     * lower, and we cannot guarantee the accuracy of the Summary Statistics
     * generated.
     */
    public static final int MINIMUM_THRESHOLD = 30;

    private static int threshold = DEFAULT_THRESHOLD;

    /**
     * Update the minimum sample size to the new minimum value. Note that
     * the new minimum must be at least {@value MINIMUM_THRESHOLD}.
     *
     * @param newThreshold The new proposed threshold value.
     * @throws IllegalArgumentException If the value is lesser than the minimum acceptable value.
     */
    public static void setThreshold(final int newThreshold)
            throws IllegalArgumentException {
        if (newThreshold < MINIMUM_THRESHOLD) {
            throw new IllegalArgumentException("Value of threshold is too low.");
        }
        threshold = newThreshold;
    }

    /**
     * @return The current threshold value.
     */
    public static int getThreshold() {
        return threshold;
    }

    /**
     * The precision is the number of significant digits that are accurately portrayed
     * in the results. The default precision is chosen arbitrarily. Users are
     * recommended to set the precision (within the capacity of double) to change
     * the way the summary is generated.
     */
    public static final int DEFAULT_PRECISION = 3;

    private static int precision = DEFAULT_PRECISION;

    /**
     * Update the precision value to the new proposed value, provided that the
     * String format method and the size of double support it.
     *
     * @param newPrecision The proposed value to the set the precision to.
     * @throws IllegalArgumentException If the new precision is not valid.
     */
    public static void setPrecision(final int newPrecision)
            throws IllegalArgumentException {
        if (newPrecision < 1 || newPrecision > 15) {
            throw new IllegalArgumentException("Invalid precision value");
        }
        precision = newPrecision;
    }

    /**
     * @return The current precision.
     */
    public static int getPrecision() {
        return precision;
    }

    /*
     * These are helper methods that make use of the precision value to
     * determine the output of the statistics generated.
     */

    private static String format(final double value) {
        return String.format("%." + precision + "f", value);
    }

    private static String format(final int value) {
        return String.valueOf(value);
    }

    private static boolean stringIsEmpty(final String text) {
        return text == null || text.isEmpty();
    }

    // Immutable fields
    private final String name;
    private final String unit;
    private final double[] values;

    // Lazily initialized fields:
    private Summary summary;
    private Double sum;
    private Double squaredSum;

    /**
     * This is the constructor for Sample and requires a name and the array
     * of observations for the sample. Unit of measurement is optional.
     * It is recommended to use a SampleBuilder to generate a Sample.
     * If the name is not provided, the default result of super.toString() is used.
     * <p>
     * Note: It is important to ensure that the observations are equal to or
     * greater than the threshold value. See {@linkplain #getThreshold()} and
     * {@link #DEFAULT_THRESHOLD}
     *
     * @param name         The name of the sample.
     * @param unit         The unit of measurement for all
     * @param observations The array of observations in the sample.
     * @throws IllegalArgumentException If any of the parameters are empty, or invalid.
     */
    Sample(final String name, final String unit, double[] observations)
            throws IllegalArgumentException {

        if (observations == null) {
            throw new IllegalArgumentException("Empty value array");
        }

        if (observations.length < threshold) {
            throw new IllegalArgumentException(
                    "The sample size is the less than the threshold: "
                            + observations.length + " < " + threshold
            );
        }

        this.name = stringIsEmpty(name) ? super.toString() : name;
        this.unit = stringIsEmpty(unit) ? "" : unit;
        this.values = observations;

        summary = null;
        sum = null;
        squaredSum = null;
    }

    /**
     * @return The name of the Series (if non-empty).
     */
    public String getName() {
        return name;
    }

    /**
     * @return The unit of measurement for the Observations (if non-empty).
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Generates the summary statistics for the Sample. For a regular Sample, the
     * guaranteed statistics calculated are:
     * <ol>
     *     <li>Count</li>
     *     <li>Sum</li>
     *     <li>Mean</li>
     *     <li>Variance</li>
     *     <li>Standard Deviation</li>
     * </ol>
     * The Summary is generated lazily. Therefore, the getSummary() function can be
     * used more than once without any degradation of performance. In fact, the
     * {@linkplain #toString()} method relies on this.
     *
     * @return A Sample.Summary object with at least the guaranteed statistics mentioned.
     */
    public Summary getSummary() {
        if (summary != null) {
            return summary;
        }
        // Add the basic summary statistics that are generally required.
        // These do not require the Sample observations to be sorted.
        summary = new Summary(this);
        summary.addStatistic("Count", format(getCount()));
        summary.addStatistic("Sum", format(getSum()));
        summary.addStatistic("Mean", format(getMean()));
        summary.addStatistic("Variance", format(getVariance()));
        summary.addStatistic("Std Dev", format(getStdDev()));

        return summary;
    }

    /**
     * @return The size of this Sample, or the number of Observations in this Sample.
     */
    public int getCount() {
        return values.length;
    }

    /**
     * @return The sum of all the observations in the Sample.
     */
    public double getSum() {
        if (sum != null) {
            return sum;
        }
        double value = 0;
        for (double item : values) {
            value += item;
        }
        sum = value;
        return value;
    }

    /**
     * @return The arithmetic mean (average) of all the observations in the Sample.
     */
    public double getMean() {
        return getSum() / getCount();
    }

    /**
     * The variance in the sample. It is calculated in a manner like (but not exactly):
     *
     * <pre>
     *     <code>
     *          double mean = // mean of observations
     *          double result = 0;
     *          for (double obs: observations) {
     *              result += Math.pow(obs - mean, 2);
     *          }
     *          return result / count;
     *     </code>
     * </pre>
     * <p>
     * Mathematically, {@code var X = sum((x - avg)^2)} where X is the sample, x is an individual
     * observation, avg is the arithmetic mean.
     * <p>
     * NOTE: There is no adjustment made for the decrease in degree of freedom. The effect
     * should be negligible because the Sample size is appropriate (at least {@value #MINIMUM_THRESHOLD}.
     *
     * @return Returns the variance of the all the observations in the Sample.
     */
    public double getVariance() {
        if (squaredSum != null) {
            return squaredSum / getCount();
        }

        double mu = getMean();
        double accumulator = 0;

        for (double item : values) {
            accumulator += Math.pow(item - mu, 2);
        }

        squaredSum = accumulator;
        return accumulator / getCount();
    }

    /**
     * The Standard Deviation in the Sample. It can be usually calculated in the following manner:
     *
     * <pre>
     *     <code>
     *         double mean = // mean of observations
     *         double result = 0;
     *         for (double obs: observations) {
     *             result += Math.pow(obs - mean, 2);
     *         }
     *         return Math.sqrt(result / count);
     *     </code>
     * </pre>
     * <p>
     * Mathematically, it is the square root of the variation. It is advantageous (and often
     * referred to as the Standard Error) because it has the same units as the observations.
     * <p>
     * NOTE: The formula is not adjusted for the decrease in degree of freedoms. However,
     * it should not make a significant difference because the Sample size is guaranteed
     * to be at least {@value MINIMUM_THRESHOLD}.
     *
     * @return The standard deviation of all the observations in the Sample.
     */
    public double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        String unitString = stringIsEmpty(unit) ? "" : " " + unit;

        for (double observation : values) {
            builder.append(observation)
                    .append(unitString)
                    .append("\n");

        }

        return builder.toString() + "\n" + getSummary().toString();
    }

    /**
     * A Summary is a collection of Statistics and their corresponding values. Internally
     * it is a {@link Map} that stores the name of the Statistics as keys and the value
     * of that Statistic as the value in the Key-Value pair.
     */
    static class Summary {
        private final Sample owner;
        private final Map<String, String> map;

        private int maxKeyLength;
        private int maxValueLength;

        /**
         * Create a new Summary. Statistics and their values (as Strings) can be
         * added using the {@link #addStatistic(String, String)} method.
         *
         * @param owner The Sample for which this Summary contains Statistics.
         */
        Summary(final Sample owner) {
            this.owner = owner;
            map = new LinkedHashMap<>();
            maxKeyLength = 0;
            maxValueLength = 0;
        }

        /**
         * Adds a statistics and its corresponding value to this Summary.
         *
         * @param statistic The statistic (like mean, median, etc).
         * @param value     The value corresponding to the statistic.
         */
        void addStatistic(final String statistic, final String value) {
            String unit = owner.getUnit();
            String updatedValue = unit.isEmpty() ? value : value + " " + unit;
            map.putIfAbsent(statistic, updatedValue);
            maxKeyLength = Math.max(maxKeyLength, statistic.length());
            maxValueLength = Math.max(maxValueLength, updatedValue.length());
        }

        /**
         * @param statistic The statistic whose value is sought.
         * @return The value of the statistic if it is saved in this Summary, or "Unknown".
         */
        String getStatistic(final String statistic) {
            return map.getOrDefault(statistic, "Unknown");
        }


        private static String fitString(final String text, final int width, final boolean left) {
            final String prefix = left ? "%-" : "%";
            return String.format(prefix + width + "s", text);
        }

        /**
         * @return A String form of the Statistics added to this summary and their values.
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("Summary Statistics for Sample: ")
                    .append(owner.name)
                    .append("\n\n");

            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.append(fitString(entry.getKey(), maxKeyLength, true))
                        .append(": ")
                        .append(fitString(entry.getValue(), maxValueLength, false))
                        .append("\n");
            }

            return builder.toString();
        }
    }
}
