package org.efrei.argosbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BAD_REQUEST = "BadRequest";
    public static final String FORBIDDEN = "Forbidden";
    public static final String NOT_FOUND = "NotFound";
    public static final String CONFLICT = "Conflict";

    private static final String SESSION_SCHEME = "session";
    private static final String SESSION_COOKIE = "JSESSIONID";
    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String PROBLEM_DETAIL_SCHEMA = "#/components/schemas/ProblemDetail";

    @Bean
    public OpenAPI argosOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Argos API")
                        .version("1.0")
                        .description("Supervision des équipements du réseau et suivi des incidents. "
                                + "Connectez-vous via POST /auth/login : la session est ensuite portée par le cookie "
                                + SESSION_COOKIE + "."))
                .components(new Components()
                        .addSecuritySchemes(SESSION_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name(SESSION_COOKIE))
                        .addSchemas("ProblemDetail", problemDetailSchema())
                        .addResponses(BAD_REQUEST, errorResponse("Données invalides ou incomplètes."))
                        .addResponses(FORBIDDEN, new ApiResponse()
                                .description("Action réservée au responsable d'exploitation."))
                        .addResponses(NOT_FOUND, errorResponse("Ressource inexistante."))
                        .addResponses(CONFLICT, errorResponse("Opération interdite par les règles de gestion.")))
                .addSecurityItem(new SecurityRequirement().addList(SESSION_SCHEME));
    }

    private ApiResponse errorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(PROBLEM_JSON,
                        new MediaType().schema(new Schema<>().$ref(PROBLEM_DETAIL_SCHEMA))));
    }

    private Schema<?> problemDetailSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("type", new Schema<>().type("string"))
                .addProperty("title", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer"))
                .addProperty("detail", new Schema<>().type("string"))
                .addProperty("instance", new Schema<>().type("string"));
    }
}
