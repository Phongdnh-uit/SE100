package uit.se100.enums.employee;

public enum EmployeePosition {

    PILOT(12000),
    COPILOT(8000),
    ATTENDANT(0),
    OPERATOR(0),
    TICKETING(0),
    OTHER(0);

    private final int maxFlightHours;

    EmployeePosition(int maxFlightHours) {
        this.maxFlightHours = maxFlightHours;
    }

    public int getMaxFlightHours() {
        return maxFlightHours;
    }

    public boolean isFlyingPosition() {
        return maxFlightHours > 0;
    }
}
