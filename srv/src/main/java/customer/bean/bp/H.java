package customer.bean.bp;

import java.util.List;

public class H {

    private Integer __count;
    private List<ResultsH> results;

    public void setResults(List<ResultsH> results) {
        this.results = results;
    }

    public List<ResultsH> getResults() {
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