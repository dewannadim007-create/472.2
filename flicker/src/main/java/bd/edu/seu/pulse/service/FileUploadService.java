package bd.edu.seu.pulse.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        // Only initialize Cloudinary if credentials are provided
        if (cloudName != null && !cloudName.isEmpty() && 
            apiKey != null && !apiKey.isEmpty() && 
            apiSecret != null && !apiSecret.isEmpty()) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret));
            logger.info("Cloudinary configured successfully");
        } else {
            logger.warn("Cloudinary credentials not configured. Image upload features will not work.");
        }
    }

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public String uploadImage(MultipartFile file) throws IOException {
        if (cloudinary == null) {
            throw new IllegalStateException("Cloudinary is not configured. Please set CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET environment variables.");
        }
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Upload to Cloudinary
        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        return (String) uploadResult.get("secure_url");
    }

    public boolean deleteImage(String imageUrl) {
        if (cloudinary == null) {
            logger.warn("Cloudinary is not configured. Cannot delete image.");
            return false;
        }
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }

        try {
            // Extract public_id from URL
            // URL format:
            // https://res.cloudinary.com/demo/image/upload/v1570979139/sample.jpg
            // public_id is "sample" (without extension)

            String publicId = extractPublicId(imageUrl);

            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                return true;
            }
        } catch (Exception e) {
            logger.error("Error deleting image from Cloudinary", e);
            return false;
        }
        return false;
    }

    private String extractPublicId(String imageUrl) {
        try {
            // Handle Cloudinary URLs
            if (imageUrl.contains("cloudinary.com")) {
                int lastSlashIndex = imageUrl.lastIndexOf("/");
                int lastDotIndex = imageUrl.lastIndexOf(".");

                if (lastSlashIndex != -1 && lastDotIndex != -1 && lastDotIndex > lastSlashIndex) {
                    return imageUrl.substring(lastSlashIndex + 1, lastDotIndex);
                }
            } else if (imageUrl.startsWith("/uploads/")) {
                // Backward compatibility or handle old local files (though they can't be
                // deleted from cloud)
                return null;
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}