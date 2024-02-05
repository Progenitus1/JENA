package cz.muni.jena.issue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueMethodDao extends JpaRepository<IssueMethod, Long>
{
}
