package co.barrantes.lima1;

public final class Palette {
    public static final String LIMA = "lima";
    public static final String ORO = "oro";
    public static final String AMBAR = "ambar";
    public static final String[] ALL = {LIMA, ORO, AMBAR};

    private Palette() {}

    public static int color(String id) {
        if (ORO.equals(id)) return 0xFFE8C48A;
        if (AMBAR.equals(id)) return 0xFFFF7A3C;
        return 0xFFC6FF3A;
    }

    public static String label(String id) {
        if (ORO.equals(id)) return "Oro";
        if (AMBAR.equals(id)) return "Ámbar";
        return "Lima";
    }
}
