package com.solution.kachey.user_manager.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "permissions")
public class Permission {
    @Id
    private String ID;
    @NotEmpty(message = "Permission name is required")
    @Indexed(unique = true) // Creates a unique index on this field in MongoDB
    private String permissionName;
    @NotEmpty(message = "API URL is required")
    @Indexed(unique = true) // Creates a unique index on this field in MongoDB
    private String apiUrl;
    @NotEmpty(message = "API method is required")
    private String apiMethod;
    private boolean isMenu = false;
    private String parentReference;
}
