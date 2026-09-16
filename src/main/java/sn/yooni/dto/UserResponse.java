package sn.yooni.dto;

import sn.yooni.model.User;
import sn.yooni.model.UserRole;

public record UserResponse(
        Long id,
        String email,
        String telephone,
        UserRole role,
        boolean actif) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getTelephone(),
                user.getRole(),
                user.getActif());
    }
}