package database;

public enum CarTypes {
    ECONOMY("ECONOMY"), STANDARD("STANDARD"), SUV("SUV");
    private final String name;

    CarTypes(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
