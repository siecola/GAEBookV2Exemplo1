package br.com.siecola.gae_exemplo1.controller;

import br.com.siecola.gae_exemplo1.model.Product;
import com.google.appengine.api.datastore.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path="/api/products")
public class ProductController {

    @GetMapping("/{code}")
    public ResponseEntity<Product> getProduct(@PathVariable int code) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate("Code",
                Query.FilterOperator.EQUAL, code);

        Query query = new Query("Products").setFilter(codeFilter);

        Entity productEntity = datastore.prepare(query).asSingleEntity();

        if (productEntity != null) {
            Product product = entityToProduct(productEntity);

            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query query;
        query = new Query("Products").addSort("Code",
                Query.SortDirection.ASCENDING);

        List<Entity> productsEntities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity productEntity : productsEntities) {
            Product product = entityToProduct(productEntity);

            products.add(product);
        }

        return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        if (!checkIfCodeExist (product)) {
            Key productKey = KeyFactory.createKey("Products", "productKey");
            Entity productEntity = new Entity("Products", productKey);

            productToEntity (product, productEntity);

            datastore.put(productEntity);

            product.setId(productEntity.getKey().getId());
        } else {
            return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{code}")
    public ResponseEntity<Product> deleteProduct(
            @PathVariable("code") int code) {

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate("Code",
                Query.FilterOperator.EQUAL, code);

        Query query = new Query("Products").setFilter(codeFilter);

        Entity productEntity = datastore.prepare(query).asSingleEntity();

        if (productEntity != null) {
            datastore.delete(productEntity.getKey());

            Product product = entityToProduct(productEntity);

            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{code}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product,
                                                 @PathVariable("code") int code) {
        if (product.getId() != 0) {
            if (!checkIfCodeExist (product)) {
                DatastoreService datastore = DatastoreServiceFactory
                        .getDatastoreService();

                Filter codeFilter = new FilterPredicate("Code",
                        FilterOperator.EQUAL, code);

                Query query = new Query("Products").setFilter(codeFilter);

                Entity productEntity = datastore.prepare(query).asSingleEntity();

                if (productEntity != null) {
                    productToEntity (product, productEntity);

                    datastore.put(productEntity);

                    product.setId(productEntity.getKey().getId());

                    return new ResponseEntity<Product>(product, HttpStatus.OK);
                } else {
                    return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean checkIfCodeExist (Product product) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Filter codeFilter = new FilterPredicate("Code", FilterOperator.EQUAL, product.getCode());

        Query query = new Query("Products").setFilter(codeFilter);
        Entity productEntity = datastore.prepare(query).asSingleEntity();

        if (productEntity == null) {
            return false;
        } else {
            if (productEntity.getKey().getId() == product.getId()) {
                //está alterando o mesmo produto
                return false;
            } else {
                return true;
            }
        }
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

    private void productToEntity (Product product, Entity productEntity) {
        productEntity.setProperty("ProductID", product.getProductID());
        productEntity.setProperty("Name", product.getName());
        productEntity.setProperty("Code", product.getCode());
        productEntity.setProperty("Model", product.getModel());
        productEntity.setProperty("Price", product.getPrice());
    }

    private Product entityToProduct (Entity productEntity) {
        Product product = new Product();
        product.setId(productEntity.getKey().getId());
        product.setProductID((String) productEntity.getProperty("ProductID"));
        product.setName((String) productEntity.getProperty("Name"));
        product.setCode(Integer.parseInt(productEntity.getProperty("Code")
                .toString()));
        product.setModel((String) productEntity.getProperty("Model"));
        product.setPrice(Float.parseFloat(productEntity.getProperty("Price")
                .toString()));
        return product;
    }
}
