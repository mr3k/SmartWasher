package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by xxottosl on 2015-04-09.
 */
public class Status {

    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public boolean success(){
        return "ok".equals(status);
    }
}
