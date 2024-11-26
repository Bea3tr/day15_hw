package ssf.day15_hw.models;

import java.util.Map;
import java.util.HashMap;

public class Cart {

    private String id;
    private Map<String, Integer> cart = new HashMap<>();
    private int count;

    public int getCount() {return count;}
    public void setCount(int count) {this.count = count;}
    
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public Map<String, Integer> getCart() {return cart;}
    public void setCart(Map<String, Integer> cart) {this.cart = cart;}
    
    @Override
    public String toString() {
        return "Cart [id=" + id + ", cart=" + cart + ", count=" + count + "]";
    }
    
}
