package cz.muni.jena.issue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueDao extends JpaRepository<Issue, Long>
{
}
