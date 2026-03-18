package metty1337.cloudfilestorage.config.minio;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "minio")
@Validated
public record MinioProperties(
        @NotBlank String url,
        @Valid Access access,
        @Valid Bucket bucket
) {
    public record Access(@NotBlank String name, @NotBlank String secret) {
    }

    public record Bucket(@NotBlank String name) {
    }
}
