package auth2.auth2.Platforms;

public class PlatformResultDto {

    private boolean success;
    private String errorMessage;
    private String externalPostId;

    public PlatformResultDto(boolean success, String errorMessage, String externalPostId) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.externalPostId = externalPostId;
    }

    public PlatformResultDto() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExternalPostId() {
        return externalPostId;
    }

    public void setExternalPostId(String externalPostId) {
        this.externalPostId = externalPostId;
    }
}
