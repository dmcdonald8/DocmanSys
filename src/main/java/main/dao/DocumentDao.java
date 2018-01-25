package main.dao;

import main.models.Document;
import org.springframework.data.repository.CrudRepository;

/* Defines CRUD interface for the corresponding model */
public interface DocumentDao extends CrudRepository<Document, Long> {

    public Document findById(long id);
    public Document findByTitle(String title);
    public Iterable<Document> findAllByAuthor(String author);
}
