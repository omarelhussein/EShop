package shop.persistence;

public interface CSVSerializable {

    String toCSVString();

    void fromCSVString(String csv);

}
