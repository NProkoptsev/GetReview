package dmd.project.objects;

import java.io.Serializable;

public class User implements Serializable {
    protected int user_id;
    protected String nickname;
    
    public User() {
        
    }
    
    public User(String nickname) {
        this();
        setNickname(nickname);
    }
    
    @Override
    public String toString() {
        return getNickname();
    }
    
    // Getters
    public int getUserId() {
        return user_id;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    // Setters
    public void setUserId(int userId) {
        this.user_id = userId;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
