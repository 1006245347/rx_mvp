package jason.com.rxremvplib.bean;

/**
 * 共用响应体，只需继承使用
 * Created by jason on 18/9/8.
 */

public class BaseHttpResponse {
    private int code;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
