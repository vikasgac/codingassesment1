package com.reliaquest.api.Entity;

public class EmployeeApiResponse<T> {

    private T data;        /// The actual response payload can be anything generic(e.g., Employee, List<Employee>, etc.)
    private String status; // The status message

    // Constructors
    public EmployeeApiResponse() {
    }

    public EmployeeApiResponse(T data, String status) {
        this.data = data;
        this.status = status;
    }

    // Getters and setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EmployeeApiResponse{" +
                "data=" + data +
                ", status='" + status + '\'' +
                '}';
    }
}
