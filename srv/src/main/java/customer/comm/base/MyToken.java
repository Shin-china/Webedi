package customer.comm.base;

import java.time.Instant;

import org.apache.http.client.HttpClient;

public class MyToken {

    private String access_token; //存储的token
    private long expires_in = 60 * 10; //有效期  默认10分钟
    private long getTokenTime = 0;
    HttpClient client;

    public boolean isExpire() { //token是否过期

        long pre = 10 * 60;//提前10分钟获取新的
        if (getTokenTime == 0 || expires_in == 0) {
            return true;
        }
        long diff = (Instant.now().toEpochMilli() - getTokenTime) / 1000;
        if (diff < (expires_in + pre)) {
            return true;
        }

        return false;
    }

    public void createToken(HttpClient _client, String _token, long _expires) { //token是否过期
        client = _client;
        access_token = _token;
        getTokenTime = Instant.now().toEpochMilli();
        if (_expires > 0)
            expires_in = _expires;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public long getGetTokenTime() {
        return getTokenTime;
    }

    public void setGetTokenTime(long getTokenTime) {
        this.getTokenTime = getTokenTime;
    }

    /**
     * @return the client
     */
    public HttpClient getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(HttpClient client) {
        this.client = client;
    }

}
