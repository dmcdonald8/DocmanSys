package main.controllers;

import main.models.*;
import main.dao.*;
import main.models.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/* Handles document sharing requests between users */
@RestController
public class ShareController {

    private final DocumentDao documentDao;
    private final UserDao userDao;
    private final ShareDao shareDao;

    @Autowired
    ShareController(DocumentDao documentDao, ShareDao shareDao, UserDao userDao){
        this.documentDao = documentDao;
        this.userDao = userDao;
        this.shareDao = shareDao;
    }

    /** Creates share request and returns share if permissions etc are correct **/
    @RequestMapping(method = RequestMethod.GET, value = "/shareRequest/{docId}/{distribUsername}")
    public Object shareRequest(@PathVariable("docId") String docId,
                               @PathVariable("distribUsername") String distribUsername){
        long id, distrib;
        User loggedUser, distributee;
        Document document;
        Share share;

        try{
            /* Find document and distributee */
            id = Long.parseLong(docId);
            document = documentDao.findById(id);
            distributee = userDao.findByUsername(distribUsername);
        }
        catch(Exception e){
            e.printStackTrace();
            return new Error("Invalid document or distributee username entered", e.getMessage());
        }
        try{
            /* Get logged user */
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            loggedUser = userDao.findByUsername(userDetails.getUsername());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Error("Error loading loading user details", e.getMessage());
        }
        try {
            /* check author of doc */
            if (document.getAuthor().equals(loggedUser.getUsername())) {
                /* Check document is active */
                if (document.isActive()) {
                    /* Check distributee permissions */
                    if (distributee.getRole() == Role.ROLE_DISTRIBUTEE || distributee.getRole() == Role.ROLE_AUTHOR) {
                        share = new Share();
                        share.setDocId(document.getId());
                        share.setRevisionNo(document.getRevisionNo());
                        share.setAuthorId(loggedUser.getId());
                        share.setDistribId(distributee.getId());
                    } else {
                        return new Error("Wrong distributee permissions", "Distributee does not have role AUTHOR or DISTRIBUTEE");
                    }
                }
                else { return new Error("Document must be activated before it can be shared",
                        "Document must be activated before it can be shared"); }
            } else {
                return new Error("No author permissions", "Logged user not listed as author on doc object");
            }
            shareDao.save(share);
            distributee.setNewShare(true);
            userDao.save(distributee);
            return share;
        }
        catch(Exception e) {
            e.printStackTrace();
            return new Error("Unknown error", e.getMessage());
        }
    }

    /* Returns array of all documents shared by loggedUser */
    @RequestMapping(method = RequestMethod.GET, value = "/getSharing")
    public Iterable<Share> getShares(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User loggedUser = userDao.findByUsername(userDetails.getUsername());
        return shareDao.findAllByAuthorId(loggedUser.getId());
    }

    /* Returns array of all documents shared with distributee */
    @RequestMapping(method = RequestMethod.GET, value = "/getSharedWith")
    public Iterable<Share> getSharedWith() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User loggedUser = userDao.findByUsername(userDetails.getUsername());
        return shareDao.findAllByDistribId(loggedUser.getId());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/checkShare")
    public boolean checkShare(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User loggedUser = userDao.findByUsername(userDetails.getUsername());
        boolean newShare = loggedUser.isNewShare();
        if(newShare)
        {
            loggedUser.setNewShare(false);
            userDao.save(loggedUser);
        }
        return newShare;
    }
}
