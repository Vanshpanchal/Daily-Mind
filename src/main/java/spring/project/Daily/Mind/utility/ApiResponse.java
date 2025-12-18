package spring.project.Daily.Mind.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiResponse", description = "Standard API response wrapper")
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private int status;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message, int status, T data) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Success response
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, 200, data);
    }

    // Success with custom status
    public static <T> ApiResponse<T> success(String message, int status, T data) {
        return new ApiResponse<>(true, message, status, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Error response
    public static <T> ApiResponse<T> error(String message, int status) {
        return new ApiResponse<>(false, message, status, null);
    }
}
