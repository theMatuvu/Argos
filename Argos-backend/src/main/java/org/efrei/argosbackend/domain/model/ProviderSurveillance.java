package org.efrei.argosbackend.domain.model;

import org.efrei.argosbackend.domain.exception.ProviderUnderSurveillanceException;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public record ProviderSurveillance(OptionalDouble recentAverage) {

    public static final int WINDOW_SIZE = 5;
    public static final double THRESHOLD = 3.0;

    private static final Comparator<Incident> MOST_RECENTLY_EVALUATED_FIRST =
            Comparator.comparing(Incident::evaluatedAt).thenComparing(Incident::id).reversed();

    public static ProviderSurveillance none() {
        return new ProviderSurveillance(OptionalDouble.empty());
    }

    public static ProviderSurveillance of(List<Incident> incidents) {
        List<Rating> lastRatings = incidents.stream()
                .filter(Incident::isEvaluated)
                .sorted(MOST_RECENTLY_EVALUATED_FIRST)
                .limit(WINDOW_SIZE)
                .map(Incident::rating)
                .toList();
        if (lastRatings.size() < WINDOW_SIZE) {
            return none();
        }
        return new ProviderSurveillance(lastRatings.stream().mapToInt(Rating::value).average());
    }

    public boolean isUnderSurveillance() {
        return recentAverage.isPresent() && recentAverage.getAsDouble() < THRESHOLD;
    }

    public void ensureAssignable(Provider provider, boolean confirmed) {
        if (isUnderSurveillance() && !confirmed) {
            throw new ProviderUnderSurveillanceException(provider.name());
        }
    }
}
