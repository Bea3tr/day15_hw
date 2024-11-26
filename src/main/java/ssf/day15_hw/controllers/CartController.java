package ssf.day15_hw.controllers;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ssf.day15_hw.models.Cart;
import ssf.day15_hw.services.CartService;

@Controller
@RequestMapping
public class CartController {

    private final Logger logger = Logger.getLogger(CartController.class.getName());

    @Autowired
    private CartService cartSvc;

    @PostMapping("/carts")
    public String postCarts(Model model,
            @RequestParam String name) {

        model.addAttribute("cartList", cartSvc.getCartByUser(name.toLowerCase()));
        model.addAttribute("user", name.toUpperCase());

        return "carts";
    }

    @GetMapping("/carts/{user}")
    public String getCarts(Model model,
        @PathVariable String user) {

        model.addAttribute("cartList", cartSvc.getCartByUser(user.toLowerCase()));
        model.addAttribute("user", user.toUpperCase());

        return "carts";
    }

    @GetMapping("/carts/{user}/{id}")
    public ModelAndView getCart(
        @PathVariable String user,
        @PathVariable String id) {

        ModelAndView mav = new ModelAndView();
        Optional<Cart> opt = cartSvc.getCart(id);
        if (opt.isEmpty()) {
            mav.setViewName("not-found");
            mav.setStatus(HttpStatusCode.valueOf(404));
            mav.addObject("user", user.toUpperCase());
            mav.addObject("id", id);
            return mav;
        }

        Cart cart = opt.get();

        mav.setViewName("cart");
        mav.setStatus(HttpStatusCode.valueOf(200));
        mav.addObject("cart", cart);
        mav.addObject("user", user.toUpperCase());

        return mav;
    }

    @GetMapping("/carts/new")
    public String getNewCart(Model model, @RequestParam String user) {
        
        // Prepare new cart id
        String newId = cartSvc.generateId(user.toLowerCase());
        cartSvc.insertCart(newId);

        Cart cart = new Cart();
        cart.setCart(new HashMap<>());
        cart.setId(newId);

        model.addAttribute("cart", cart);
        model.addAttribute("id", newId);
        model.addAttribute("user", user.toUpperCase());

        return "cart";
    }

    @PostMapping("/carts/{user}/{id}")
    public String postCart(Model model, 
        @RequestParam MultiValueMap<String, String> form,
        @PathVariable String id,
        @PathVariable String user) {
        
        @SuppressWarnings("null")
        String item = form.getFirst("item").toLowerCase();
        int count = Integer.parseInt(form.getFirst("count"));
        logger.info("[Controller] " + form.getFirst("item") + ": " + form.getFirst("count"));
        
        // Update cart
        logger.info("[Controller] Total quantity in cart: %d".formatted(cartSvc.getCart(id).get().getCount()));
        cartSvc.updateCart(id, item, count);
        logger.info("[Controller] Added %d %s to cart [%s]".formatted(count, item, id));
        Optional<Cart> opt = cartSvc.getCart(id);
        Cart cart = opt.get();
        logger.info("[Controller] Updated quantity in cart: %d".formatted(cartSvc.getCart(id).get().getCount()));
        
        model.addAttribute("cart", cart);

        return "cart";
    }
}
