package com.cos.photogramstart.service;

import com.cos.photogramstart.domain.subscribe.SubscribeRepository;
import com.cos.photogramstart.domain.user.User;
import com.cos.photogramstart.domain.user.UserRepository;
import com.cos.photogramstart.handler.ex.CustomApiException;
import com.cos.photogramstart.handler.ex.CustomException;
import com.cos.photogramstart.handler.ex.CustomValidationApiException;
import com.cos.photogramstart.web.dto.user.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${file.path}")
    private String uploadFolder;

    @Transactional(readOnly = true)
    public UserProfileDto 회원프로필(int pageUserId, int principalId) {
        UserProfileDto userProfileDto = new UserProfileDto();

        User userEntity = userRepository.findById(pageUserId).orElseThrow(() -> {
            return new CustomException("해당 프로필 페이지는 없는 페이지입니다.");
        });

        userProfileDto.setUser(userEntity);
        userProfileDto.setPageOwnerState(pageUserId == principalId); // 1은 페이지 주인, -1은 주인이 아님
        userProfileDto.setImageCount(userEntity.getImages().size());

        int subscribeState = subscribeRepository.mSubscribeState(principalId, pageUserId);
        int subscribeCount = subscribeRepository.mSubscribeCount(pageUserId);

        userProfileDto.setSubscribeState(subscribeState == 1);
        userProfileDto.setSubscribeCount(subscribeCount);

        // 좋아요 카운트 추가하기
        userEntity.getImages().forEach(image -> {
            image.setLikeCount(image.getLikes().size());
        });

        return userProfileDto;
    }

    @Transactional
    public User 회원수정(int id, User user) {
        // 1. 영속화
        User userEntity = userRepository.findById(id).orElseThrow(() -> {
            return new CustomValidationApiException("찾을 수 없는 id입니다.");
        });

        // 2. 영속화된 오브젝트를 수정 - 더티체킹(업데이트 완료)
        userEntity.setName(user.getName());

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        userEntity.setPassword(encPassword);
        userEntity.setBio(user.getBio());
        userEntity.setWebsite(user.getWebsite());
        userEntity.setPhone(user.getPhone());
        userEntity.setGender(user.getGender());
        return userEntity;
    }

    @Transactional
    public User 회원프로필사진변경(int principalId, MultipartFile profileImageFile) {
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + profileImageFile.getOriginalFilename();
        Path imageFilePath = Paths.get(uploadFolder + imageFileName);
        try {
            Files.write(imageFilePath, profileImageFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        User userEntity = userRepository.findById(principalId).orElseThrow(() -> {
            return new CustomApiException("유저를 찾을 수 없습니다.");
        });
        userEntity.setProfileImageUrl(imageFileName);

        return userEntity;
    }
}
