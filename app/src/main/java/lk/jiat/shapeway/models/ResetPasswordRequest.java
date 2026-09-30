package lk.jiat.shapeway.models;

public class ResetPasswordRequest {
    private String oobCode;
    private String newPassword;

    public ResetPasswordRequest(String oobCode) {
        this.oobCode = oobCode;
    }

    public ResetPasswordRequest(String oobCode, String newPassword) {
        this.oobCode = oobCode;
        this.newPassword = newPassword;
    }
}