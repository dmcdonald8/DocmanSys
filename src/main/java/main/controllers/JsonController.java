package main.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.dao.DocumentDao;
import main.dao.UserDao;
import main.models.Document;
import main.models.User;
import main.models.Error;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;


/* Rest controller for servicing AJAX requests for information from the front-end */
@RestController
@RequestMapping("/rest")
public class JsonController {

    private final DocumentDao documentDao;

    private final UserDao userDao;

    @Autowired
    JsonController(DocumentDao documentDao, UserDao userDao){
        this.documentDao = documentDao;
        this.userDao = userDao;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/byId/{userId}")
    Object getUserById(@PathVariable long userId) {
        try{
        User user = this.userDao.findById(userId);
        return user;
        }
        catch (Exception e){
            e.printStackTrace();
            return new Error("Invalid user ID", e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/byUsername/{username}")
    Object getUserByUsername(@PathVariable String userName) {
        try {
            User user = this.userDao.findByUsername(userName);
            return user;
        }
        catch (Exception e){
            e.printStackTrace();
            return new Error("Invalid Username", e.getMessage());
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/user/getAll")
    Iterable<User> getAllUsers() {
        try {
            return this.userDao.findAll();
        }
        catch (Exception e){
            return null;
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/document/byId/{docId}")
    Object getDocumentById(@PathVariable long docId) {
        try {
            Document document = this.documentDao.findById(docId);
            return document;
        }
        catch (Exception e){
            e.printStackTrace();
            return new Error("Invalid document ID", e.getMessage());
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/document/byTitle/{docTitle}")
    Object getDocumentByTitle(@PathVariable String title) {
        try {
            Document document = this.documentDao.findByTitle(title);
            return document;
        }
        catch (Exception e){
            e.printStackTrace();
            return new Error("Invalid document title", e.getMessage());
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/document/getAll")
    Iterable<Document> getAllDocuments() {
        try {
            return this.documentDao.findAll();
        }
        catch (Exception e) {
            return null;
         }
        }

    @RequestMapping (method = RequestMethod.GET, value = "/document/byAuthor/{username}")
    public Iterable<Document> getAllDocumentsByAuthor(@PathVariable("username") String username) {
       try {
           System.out.println(username);
           Iterable<Document> docs = documentDao.findAllByAuthor(username);
           return docs;
       }
       catch(Exception e) {
           e.printStackTrace();
           return null;
       }
    }

    @RequestMapping (value ="/getLoggedUser")
    Object getLoggedUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userDao.findByUsername(userDetails.getUsername());
            return user;
        }
        catch (Exception e) {
            return new Error("Error getting logged user", e.getMessage());
        }
    }

    /***
     * Toggle activation of documents
     * @param docId
     * @return String
     */
    @RequestMapping(value = "toggleActivation/{docId}", method = RequestMethod.GET)
    public Object toggleActivation(@PathVariable("docId") String docId) {
        Document document;
        try {
            document = documentDao.findById(Long.parseLong(docId));
        } catch (Exception e) {
            System.out.println("Error toggling activation, invalid docId" + e.getMessage());
            return new Error("Error toggling activation, invalid docId", e.getMessage());
        }

        if(document.isActive()){ document.setActive(false);}
        else {document.setActive(true);}
        return documentDao.save(document);
    }

    /***
     * Take a stringified user object from the site as http request and maps to object
     * @param userId
     * @param jsonString
     * @return

    @RequestMapping(method = RequestMethod.POST, value ="editUser/{userId}/{jsonString}")
    public Object editUser(@PathVariable("userId") String userId,
                           @PathVariable("jsonString") String jsonString){
        long id;
        User newUser;
        User loadedUser;

        try{ // Checks user exists and id is valid number
            id = Long.parseLong(userId);
            loadedUser = userDao.findById(id);
        }
        catch(Exception e) { return new Error("Invalid user id", e.getMessage());}

        try{ // Checks jsonString can be mapped as valid User object
            ObjectMapper mapper = new ObjectMapper();
            newUser = mapper.readValue(jsonString, User.class);
        }
        catch(Exception e){ return new Error("Invalid user string", e.getMessage()); }

        userDao.delete(id); // Deletes previous entry
        return userDao.save(newUser); // Saves and returns new entry
    }
     */

}

