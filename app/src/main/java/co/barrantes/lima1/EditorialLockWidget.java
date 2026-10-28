package co.barrantes.lima1;

public final class EditorialLockWidget extends BaseWidget {
    @Override public String style() { return "editorial"; }
    @Override public boolean lock() { return true; }
}
