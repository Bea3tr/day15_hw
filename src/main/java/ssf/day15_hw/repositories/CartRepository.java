package ssf.day15_hw.repositories;

import java.util.*;
import java.util.logging.Logger;

import ssf.day15_hw.models.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CartRepository {

    private static final Logger logger = Logger.getLogger(CartRepository.class.getName());
    @Autowired
    @Qualifier("redis-0")
    private RedisTemplate<String, Object> template;

    public List<Cart> getCartByUser(String user) {
        List<Cart> cartList = new LinkedList<>();
        for (String id : template.keys(user+"*")) {
            Optional<Cart> opt = getCart(id);
            if(!opt.isEmpty()) {
                cartList.add(opt.get());
            }
        }
        return cartList;
    }

    public Optional<Cart> getCart(String cartId) {
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        if (!template.hasKey(cartId)) {
            return Optional.empty();
        }
        Cart cart = new Cart();
        Map<String, Integer> cartMap = new HashMap<>();
        // Get cart with id
        Map<String, Object> items = hashOps.entries(cartId);
        for(String item : items.keySet()) {
            if(!item.equals(" ")) {
                int qty = Integer.parseInt(items.get(item).toString());
                cartMap.put(item, qty);
            }
        }

        cart.setId(cartId);
        cart.setCart(cartMap);
        cart.setCount(getCount(cartMap));

        return Optional.of(cart);
    }

    public void insertCart(String cartId) {
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        hashOps.put(cartId, " ", " ");
    }

    public void updateCart(String cartId, String item, int qty) {
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        logger.info("[Repo] Updating cart: added %d %s".formatted(qty, item));
        if(hashOps.hasKey(cartId, item)) {
            @SuppressWarnings("null")
            int prevQty = Integer.parseInt(hashOps.get(cartId, item).toString());
            qty += prevQty;
            hashOps.put(cartId, item, Integer.toString(qty));
        }
        hashOps.put(cartId, item, Integer.toString(qty));
    }

    public int getCount(Map<String, Integer> cart) {
        int count = 0;
        for (String item : cart.keySet()) {
            count += cart.get(item);
        }
        return count;
    }
}