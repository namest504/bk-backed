package k_paas.balloon.keeper.application.domain.balloon.report.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ReportImageTypeValidator {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    /**
     * 제공된 파일이 허용된 확장자 중 하나를 가진 이미지인지 확인
     *
     * @param file the MultipartFile to check
     * @return true if the file is a valid image; false otherwise
     */
    public boolean isImage(MultipartFile file) {
        if (file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            return false;
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 주어진 파일명에서 확장자 이름 추출
     *
     * @param filename the name of the file from which to extract the extension
     * @return the file extension if found; an empty string if the filename is null or no extension is found
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}
