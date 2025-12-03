package com.thesis.hotel_service.repository.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import com.thesis.hotel_service.model.Hotel;

public class HotelSpecification {

    public static Specification<Hotel> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null : cb.equal(root.get("city"), city);
    }
    
    public static Specification<Hotel> hasRating(Integer rating) {
        return (root, query, cb) ->
                rating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), rating);
    }

    public static Specification<Hotel> nameContains(String keyword) {
        return (root, query, cb) ->
                keyword == null ? null : cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Hotel> hasPriceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;

            //Join to room_types table
            Join<Object, Object> roomTypeJoin = root.join("roomTypes", JoinType.INNER);
            query.distinct(true);

            if (min == null)
                return cb.lessThanOrEqualTo(roomTypeJoin.get("price_per_night"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(roomTypeJoin.get("price_per_night"), min);
            return cb.between(roomTypeJoin.get("price_per_night"), min, max);
        };
    }
}

