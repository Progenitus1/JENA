package cz.muni.jena.issue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueClassDao extends JpaRepository<IssueClass, Long>
{
}
