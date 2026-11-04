package org.efrei.argosbackend.domain.model;

import java.util.List;
import java.util.OptionalDouble;

public record ProviderRating(int evaluationCount, OptionalDouble average) {

    public static ProviderRating of(List<Rating> ratings) {
        return new ProviderRating(ratings.size(), ratings.stream().mapToInt(Rating::value).average());
    }
}
