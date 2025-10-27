package sgu.demo.sgubackend.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String message;
    private Object data;
    private Boolean success;
    private HttpStatus status;

    // Constructor por defecto para deserialización
    public ApiResponse() {
    }

    public ApiResponse(String message, Object data, Boolean success, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.success = success;
        this.status = status;
    }

    public ApiResponse(String message, Boolean success, HttpStatus status) {
        this.message = message;
        this.success = success;
        this.status = status;
    }

    // Constructores estáticos de conveniencia
    public static ApiResponse success(Object data, String message) {
        return new ApiResponse(message, data, true, HttpStatus.OK);
    }

    public static ApiResponse success(Object data, String message, HttpStatus status) {
        return new ApiResponse(message, data, true, status);
    }

    public static ApiResponse error(String message, HttpStatus status) {
        return new ApiResponse(message, null, false, status);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(message, null, false, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ApiResponse> toResponseEntity() {
        return ResponseEntity.status(this.status != null ? this.status : HttpStatus.OK).body(this);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
