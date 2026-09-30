package lk.jiat.shapeway.models;

public class ErrorResponse {
    private FirebaseError error;

    public FirebaseError getError() {
        return error;
    }

    public static class FirebaseError {
        private String message;

        public String getMessage() {
            return message;
        }
    }
}