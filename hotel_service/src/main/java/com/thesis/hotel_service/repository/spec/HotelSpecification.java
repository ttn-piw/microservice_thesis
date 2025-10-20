package com.thesis.hotel_service.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import com.thesis.hotel_service.model.Hotel;

public class HotelSpecification {

    public static Specification<Hotel> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.equal(root.get("city"), city);
    }

    public static Specification<Hotel> hasPriceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }

    public static Specification<Hotel> hasRating(Integer rating) {
        return (root, query, cb) ->
                rating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), rating);
    }

    public static Specification<Hotel> nameContains(String keyword) {
        return (root, query, cb) ->
                keyword == null ? null : cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }
}

