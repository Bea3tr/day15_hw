package ssf.day15_hw.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ssf.day15_hw.models.Cart;
import ssf.day15_hw.repositories.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    public String generateId(String user) {
        String id = user + "_" + UUID.randomUUID().toString().substring(0, 3);
        return id;
    }

    public List<Cart> getCartByUser(String user) {
        return cartRepo.getCartByUser(user);
    }

    public Optional<Cart> getCart(String cartId) {
        return cartRepo.getCart(cartId);
    }
    
    public void insertCart(String cartId) {
        cartRepo.insertCart(cartId);
    }

    public void updateCart(String cartId, String item, int qty) {
        cartRepo.updateCart(cartId, item, qty);
    }

}
