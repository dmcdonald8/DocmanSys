package main.dao;

import main.models.Share;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/* Defines CRUD interface for the corresponding model */
@Repository
public interface ShareDao extends CrudRepository<Share, Long> {

    public Share findById(long Id);

    public Share findByDocId(long docId);

    public Share findByAuthorId(long authorId);

    public Share findByDistribId(long distribId);

    public Iterable<Share> findAllByDistribId(long distribId);

    public Iterable<Share> findAllByAuthorId(long authorId);

}
