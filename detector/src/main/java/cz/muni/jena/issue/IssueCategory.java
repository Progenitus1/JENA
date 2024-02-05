package cz.muni.jena.issue;

public enum IssueCategory
{
    DI("DI"),
    SECURITY("SEC"),
    PERSISTENCE("PER"),
    MOCKING("MOC"),
    SERVICE_LAYER("SER");

    private final String shortCut;

    IssueCategory(String shortCut)
    {
        this.shortCut = shortCut;
    }

    @Override
    public String toString()
    {
        return shortCut;
    }
}
