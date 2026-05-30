package smartmart.model;

public interface Exportable {
    String toCSVRow();
    String getCSVHeader();
}
