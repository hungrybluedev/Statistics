package in.hungrybluedev.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A Utility class for easy construction of Samples. Observations
 * can be added one by one, or all at once. Once a sufficient number
 * of observations are entered (see {@linkplain Sample#getThreshold()}
 * a sample can be generated.
 * <p>
 * Generating a sample does not invalidate the previous observations.
 * More entries can be added and the consequent samples will contain
 * all the observations (new as well as old ones). In such a scenario,
 * it is recommended to use the {@linkplain #setName(String)} method
 * to create Sample with different names (for the sake of easy organization).
 *
 * @author Subhomoy Haldar (@HungryBlueDev)
 */
public class SampleBuilder {
    private final List<Double> observationList;
    private String name;
    private String unit;

    /**
     * Default constructor that creates an internal storage list of
     * default size and the name of the sample is set to {@code null}.
     * If you want to change the name of the Sample, use the
     * {@linkplain #setName(String)} method.
     *
     * @see Sample#DEFAULT_THRESHOLD
     * @see Sample#getThreshold()
     */
    public SampleBuilder() {
        observationList = new ArrayList<>(Sample.getThreshold());
        name = null;
        unit = null;
    }

    /**
     * Creates a SampleBuilder and sets the (current) name of the
     * Sample to the proposed value. The internal list is of the default size.
     *
     * @param name The desired name of the Sample.
     * @see Sample#DEFAULT_THRESHOLD
     * @see Sample#getThreshold()
     */
    public SampleBuilder(final String name) {
        observationList = new ArrayList<>(Sample.getThreshold());
        this.name = name;
    }

    /**
     * Creates a SampleBuilder and sets the (current) name of the
     * Sample to the proposed value. The internal list is of the default size.
     *
     * @param name The desired name of the Sample.
     * @param unit The unit of measurement for all the observations in the Sample.
     * @see Sample#DEFAULT_THRESHOLD
     * @see Sample#getThreshold()
     */
    public SampleBuilder(final String name, final String unit) {
        observationList = new ArrayList<>(Sample.getThreshold());
        this.name = name;
        this.unit = unit;
    }

    /**
     * Constructor that takes the desired name of the Sample as well
     * as the tentative size of the Sample. This ensures that the value
     * of count is at least equal to the threshold value.
     *
     * @param name  The desired name of the Sample.
     * @param unit  The unit of measurement for all the observations in the Sample.
     * @param count The proposed size of the Sample.
     * @throws IllegalArgumentException If the count is lower than the threshold.
     * @see Sample#DEFAULT_THRESHOLD
     * @see Sample#getThreshold()
     */
    public SampleBuilder(final String name, final String unit, final int count) {
        if (count < Sample.getThreshold()) {
            throw new IllegalArgumentException("The count is lower than the threshold.");
        }
        observationList = new ArrayList<>(count);
        this.name = name;
        this.unit = unit;
    }

    /**
     * Sets the name of the Sample being built to the proposed value.
     *
     * @param name The name to be given to the next Sample that is generated.
     * @return This SampleBuilder to facilitate chaining of method calls.
     */
    public SampleBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the unit of measurement for the observations in the Sample.
     *
     * @param unit The unit of measurement for all the observations in the Sample.
     * @return This SampleBuilder to facilitate chaining of method calls.
     */
    public SampleBuilder setUnit(final String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * Adds an observation value to the list. The requirement is that the
     * value must be finite (not infinite or NaN).
     *
     * @param observation The observation to add to the current Sample.
     * @return This SampleBuilder to facilitate chaining of method calls.
     * @throws IllegalArgumentException If the observation is not finite.
     * @see Double#isFinite(double)
     */
    public SampleBuilder addObservation(final double observation)
            throws IllegalArgumentException {

        if (!Double.isFinite(observation)) {
            throw new IllegalArgumentException("Observations must be finite.");
        }

        observationList.add(observation);
        return this;
    }

    /**
     * Adds a chunk of observations to the current Sample. The criterion for individual
     * observations holds: only finite values (no infinite or NaN).
     *
     * @param observationChunk The chunk of observations to add to the current Sample.
     * @return This SampleBuilder to facilitate chaining of method calls.
     * @throws IllegalArgumentException If any of the observations are not finite.
     */
    public SampleBuilder addObservations(final double[] observationChunk)
            throws IllegalArgumentException {
        for (double observation : observationChunk) {
            addObservation(observation);
        }
        return this;
    }

    /**
     * Adds a {@link Collection} of observations to the current Sample. The criteria
     * for valid observations include:
     * <ol>
     *     <li>Must be non-null</li>
     *     <li>Must be finite (not infinite or NaN)</li>
     * </ol>
     *
     * @param observationsCollection The collections of elements to be added.
     * @return This SampleBuilder to facilitate chaining of method calls.
     * @throws IllegalArgumentException If any of the items is null, infinite or NaN.
     */
    public SampleBuilder addObservations(final Collection<Double> observationsCollection)
            throws IllegalArgumentException {
        for (Double observation : observationsCollection) {
            Objects.requireNonNull(observation);
            addObservation(observation);
        }
        return this;
    }

    /**
     * @return The current count of observations in the internal list.
     */
    public int getCount() {
        return observationList.size();
    }

    /**
     * @return A Sample instance from the observations collected thus far.
     * @throws IllegalStateException If the sample size does not exceed the minimum required threshold.
     * @see Sample#DEFAULT_THRESHOLD
     * @see Sample#getThreshold()
     */
    public Sample buildSample()
            throws IllegalStateException {

        final int count = getCount();
        if (count < Sample.getThreshold()) {
            throw new IllegalStateException("The sample does not contain enough observations.");
        }

        Double[] rawOutput = observationList.toArray(new Double[count]);
        double[] observations = new double[count];

        for (int i = 0; i < count; i++) {
            observations[i] = rawOutput[i];
        }

        return new Sample(name, unit, observations);
    }
}
