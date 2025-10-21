package com.thesis.hotel_service.repository.spec;

import com.thesis.hotel_service.model.Hotel;
import org.springframework.data.jpa.domain.Specification;

public class RoomTypeSpecification {
    public static Specification<Hotel> hasPriceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }
}
