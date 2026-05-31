package com.deliverx.user_service.service;

import com.deliverx.user_service.dto.UpdateUserProfileRequest;
import com.deliverx.user_service.dto.UserProfileResponse;
import com.deliverx.user_service.entity.UserProfile;
import com.deliverx.user_service.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfileResponse getProfile(String email) {
        UserProfile profile = userProfileRepository
                .findByEmail(email)
                .orElseGet(() -> userProfileRepository.save(new UserProfile(email)));

        return toResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateUserProfileRequest request) {
        UserProfile profile = userProfileRepository
                .findByEmail(email)
                .orElseGet(() -> new UserProfile(email));

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return toResponse(savedProfile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getAddress()
        );
    }
}
