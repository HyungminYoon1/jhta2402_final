package com.user.IntArea.service;

import com.user.IntArea.common.utils.ImageUtil;
import com.user.IntArea.dto.image.ImageDto;
import com.user.IntArea.entity.Image;
import com.user.IntArea.entity.QuotationRequest;
import com.user.IntArea.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageUtil imageUtil;
    private final ImageRepository imageRepository;

    private void saveSingleImage(MultipartFile image, UUID refId, int index) {
        ImageDto imageDto = imageUtil.uploadS3(image, refId, index)
                .orElseThrow(() -> new NoSuchElementException("S3 오류"));
        imageRepository.save(imageDto.toImage());
    }

    public void saveImage(MultipartFile image, UUID refId) {
        saveSingleImage(image, refId, 0);
    }

    @Transactional
    public void saveMultiImages(List<MultipartFile> images, UUID refId) {
        for (int i=0; i<images.size(); i++) {
            saveSingleImage(images.get(i), refId, i);
        }
    }

    // 특정한 객체와 연관된 이미지 리스트 불러오기
    public <T> List<Image> getImagesFrom(T entity) {
        UUID refId;
        if (entity instanceof QuotationRequest) {
            refId = ((QuotationRequest) entity).getId();
        } else {
            throw new NoSuchElementException("이미지가 참조하는 클래스가 아닙니다.");
        }
        return imageRepository.findAllByRefId(refId);
    }

    public void deleteImageByImageId(UUID id) {
        imageRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllImagesByRefId(UUID refId) {
        imageRepository.deleteByRefId(refId);
    }
}
