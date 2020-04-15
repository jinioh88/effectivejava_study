package item3;

public class Elvis {
    private static final Elvis INSTANCE = new Elvis();

    public Elvis() {
    }

    public static Elvis getInstance() {
        return INSTANCE;
    }

    public void leaveTheBuilding() {
        System.out.println("leave");
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
