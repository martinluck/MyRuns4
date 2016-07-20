package edu.dartmouth.cs.reshmi.myruns1;

/**
 * Helper class for creating a data structure to handle the title and description of entries
 * in the database, displayed in the custom ListView for the History Fragment, as one object.
 *
 * @author Reshmi Suresh
 */
public class HistoryFragmentItems
{
    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HistoryFragmentItems(String title, String description)
    {
        super();
        this.title = title;
        this.description = description;
    }
}
