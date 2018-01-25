package main.models;

/* Custom Error model for returning server errors to the front-end for debugging purposes */
public class Error {

    private String type;
    private String subtype;
    private String message;

    public Error(String subtype,String message) {
        this.type ="error";
        this.setSubtype(subtype);
        this.setMessage(message);
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
