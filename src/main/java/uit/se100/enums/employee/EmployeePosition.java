package uit.se100.enums.employee;

public enum EmployeePosition {

    PILOT(100),
    COPILOT(100),
    ATTENDANT(80),
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
