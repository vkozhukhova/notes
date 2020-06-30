package home.model;

public class ImportDetails {
    private ImportType importType;
    private String importString;

    public ImportDetails(ImportType importType, String importString) {
        this.importType = importType;
        this.importString = importString;
    }

    public ImportDetails() {
    }

    public ImportType getImportType() {
        return importType;
    }

    public void setImportType(ImportType importType) {
        this.importType = importType;
    }

    public String getImportString() {
        return importString;
    }

    public void setImportString(String importString) {
        this.importString = importString;
    }
}
