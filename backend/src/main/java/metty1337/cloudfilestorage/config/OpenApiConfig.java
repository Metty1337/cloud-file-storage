package metty1337.cloudfilestorage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import metty1337.cloudfilestorage.dto.response.ErrorResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        var errorSchema = new Schema<ErrorResponse>()
                .$ref("#/components/schemas/ErrorResponse");

        Content errorContent = new Content()
                .addMediaType("application/json", new MediaType().schema(errorSchema));

        return new OpenAPI()
                .info(new Info()
                        .title("Cloud File Storage API")
                        .description("REST API for cloud file storage service with user authentication, file upload/download, and directory management")
                        .version("1.0.0"))
                .path("/api/auth/sign-out", new PathItem()
                        .post(new io.swagger.v3.oas.models.Operation()
                                .addTagsItem("Authentication")
                                .summary("Sign out")
                                .description("Invalidates the current session and clears the JSESSIONID cookie")
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse()
                                                .description("Signed out successfully"))
                                        .addApiResponse("401", new ApiResponse()
                                                .description("Not authenticated")
                                                .content(errorContent))
                                        .addApiResponse("500", new ApiResponse()
                                                .description("Internal server error")
                                                .content(errorContent)))));
    }
}