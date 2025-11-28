package com.dimitrijevic175.user_service.configuration;

import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.domain.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) ->
                firstName == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) ->
                lastName == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(RoleName roleName) {
        return (root, query, criteriaBuilder) ->
                roleName == null ? null : criteriaBuilder.equal(root.get("role").get("name"), roleName);
    }

    public static Specification<User> isActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
                active == null ? null : criteriaBuilder.equal(root.get("active"), active);
    }
}
