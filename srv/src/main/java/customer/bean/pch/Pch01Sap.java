package customer.bean.pch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Pch01Sap {

    Map<String, String> supplierCreatMap = new HashMap<>();
    Map<String, String> supplierUpdateMap = new HashMap<>();
    Map<String, String> supplierDeleteMap = new HashMap<>();

    HashSet<String> PoDelSet = new HashSet<>();

    /**
     * @return the supplierCreatMap
     */
    public Map<String, String> getSupplierCreatMap() {
        return supplierCreatMap;
    }

    /**
     * @param supplierCreatMap the supplierCreatMap to set
     */
    public void setSupplierCreatMap(Map<String, String> supplierCreatMap) {
        this.supplierCreatMap = supplierCreatMap;
    }

    /**
     * @return the supplierUpdateMap
     */
    public Map<String, String> getSupplierUpdateMap() {
        return supplierUpdateMap;
    }

    /**
     * @param supplierUpdateMap the supplierUpdateMap to set
     */
    public void setSupplierUpdateMap(Map<String, String> supplierUpdateMap) {
        this.supplierUpdateMap = supplierUpdateMap;
    }

    /**
     * @return the supplierDeleteMap
     */
    public Map<String, String> getSupplierDeleteMap() {
        return supplierDeleteMap;
    }

    /**
     * @param supplierDeleteMap the supplierDeleteMap to set
     */
    public void setSupplierDeleteMap(Map<String, String> supplierDeleteMap) {
        this.supplierDeleteMap = supplierDeleteMap;
    }

    /**
     * @return the poDelSet
     */
    public HashSet<String> getPoDelSet() {
        return PoDelSet;
    }

    /**
     * @param poDelSet the poDelSet to set
     */
    public void setPoDelSet(HashSet<String> poDelSet) {
        PoDelSet = poDelSet;
    }

}
