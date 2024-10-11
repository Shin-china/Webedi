package customer.bean.bp;

import java.util.List;

public class D {

    private Integer __count;
    private List<Results> results;

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public List<Results> getResults() {
        return results;
    }

    /**
     * @return the __count
     */
    public Integer get__count() {
        return __count;
    }

    /**
     * @param __count the __count to set
     */
    public void set__count(Integer __count) {
        this.__count = __count;
    }

}