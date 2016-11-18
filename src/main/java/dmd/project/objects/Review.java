package dmd.project.objects;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Review implements Serializable {
    protected int id;
    protected String date;
    protected int grade;
    protected String overall;
    protected String pros;
    protected String cons;
    protected String sourceUrl;
    protected Source source;
    
    protected User author;
    protected int src_id;


    public Review() {
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        sb.append(getDate() + "\n");
        sb.append(getAuthor() + "\n");
        sb.append("Grade: " + getGrade() + "\n");
        sb.append("Pros: " + getPros() + "\n");
        sb.append("Cons: " + getCons() + "\n");
        sb.append(getOverall());
        
        sb.append("}");
        return sb.toString();
    }
    
    // Getters
    public int getReviewId() {
        return id;
    }
    
    public String getDate() {
        return date;
    }
    
    public int getGrade() {
        return grade;
    }
    
    public String getOverall() {
        return overall;
    }
    
    public String getPros() {
        return pros;
    }
    
    public String getCons() {
        return cons;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public Source getSource() {
        return source;
    }
    
    // Setters
    public void setReviewId(int rId) {
        this.id = rId;
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
    
    public void setOverall(String overall) {
        this.overall = overall;
    }
    
    public void setPros(String pros) {
        this.pros = pros;
    }
    
    public void setCons(String cons) {
        this.cons = cons;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public void setSourceUrl(String url) {
        this.sourceUrl = url;
    }
    
    public void setSource(Source source) {
        this.source = source;
    }
}
