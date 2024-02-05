package cz.muni.jena.issue;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "IssueClass",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "name",
                        "project_label"
                },
                name = "IssuesClassUniqueConstraint")}
)
public class IssueClass
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "project_label")
    private String projectLabel;

    @Column(name = "complexity")
    private long complexity;

    @Column(name = "name")
    private String name;

    public IssueClass(String projectLabel, long complexity, String name)
    {
        this.projectLabel = projectLabel;
        this.complexity = complexity;
        this.name = name;
    }

    public IssueClass()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getProjectLabel()
    {
        return projectLabel;
    }

    public void setProjectLabel(String projectVersion)
    {
        this.projectLabel = projectVersion;
    }

    public long getComplexity()
    {
        return complexity;
    }

    public void setComplexity(long complexity)
    {
        this.complexity = complexity;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        IssueClass that = (IssueClass) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
