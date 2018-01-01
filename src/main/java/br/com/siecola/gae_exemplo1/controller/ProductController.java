package br.com.siecola.gae_exemplo1.controller;

import br.com.siecola.gae_exemplo1.model.Product;
import com.google.appengine.api.datastore.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api/products")
public class ProductController {

    @GetMapping("/{code}")
    public ResponseEntity<Product> getProduct(@PathVariable int code) {
        Product product = createProduct(code);
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = new ArrayList<>();
        for (int j = 1; j <= 5; j++) {
            products.add(createProduct(j));
        }

        return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        product.setProductID(Integer.toString(product.getCode()));

        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{code}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("code") int code) {
        Product product = createProduct(code);
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @PutMapping(path = "/{code}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("code") int code) {
        product.setProductID(Integer.toString(product.getCode()));
        product.setName("New name");
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    private Product createProduct (int code) {
        Product product = new Product();
        product.setProductID(Integer.toString(code));
        product.setCode(code);
        product.setModel("Model " + code);
        product.setName("Name " + code);
        product.setPrice(10 * code);
        return product;
    }
}
