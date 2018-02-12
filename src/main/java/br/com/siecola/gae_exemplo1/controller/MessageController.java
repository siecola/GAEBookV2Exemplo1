package br.com.siecola.gae_exemplo1.controller;

import br.com.siecola.gae_exemplo1.model.Product;
import br.com.siecola.gae_exemplo1.model.User;
import br.com.siecola.gae_exemplo1.repository.UserRepository;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/message")
public class MessageController {
    private static final Logger log = Logger.getLogger("MessageController");

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path = "/sendproduct")
    public ResponseEntity<String> sendProduct(
            @RequestParam("email") String email,
            @RequestParam("productCode") String productCode) {

        Optional<User> optUser = userRepository.getByEmail(email);
        if (optUser.isPresent()) {
            User user = optUser.get();

            Optional<Product> optProduct = findProduct(
                    Integer.parseInt(productCode));
            if (optProduct.isPresent()) {
                Product product = optProduct.get();

                Sender sender = new Sender("API_KEY");
                Gson gson = new Gson();
                Message message = new Message.Builder().addData("product", gson.toJson(product)).build();
                Result result;

                log.info("GCM RegistrationId: " + user.getGcmRegId());
                try {
                    result = sender.send(message, user.getGcmRegId(), 5);
                    if (result.getMessageId() != null) {
                        String canonicalRegId = result.getCanonicalRegistrationId();
                        if (canonicalRegId != null) {
                            log.severe("Usuário [" + user.getEmail() + "] com mais de um registro");
                        }
                    } else {
                        String error = result.getErrorCodeName();
                        log.severe("Usuário [" + user.getEmail() + "] não registrado");
                        log.severe(error);

                        return new ResponseEntity<String>("Usuário não registrado no GCM",
                                HttpStatus.NOT_FOUND);
                    }
                } catch (IOException e) {
                    log.severe("Falha ao enviar mensagem");
                    e.printStackTrace();
                    return new ResponseEntity<String>("Falha ao enviar a mensagem",
                            HttpStatus.PRECONDITION_FAILED);
                }
                log.severe("Mensagem enviada ao produto " + product.getName());
                return new ResponseEntity<String>("Mensagem enviada com o produto "
                        + product.getName(), HttpStatus.OK);
            } else {
                log.severe("Produto não encontrado");
                return new ResponseEntity<String>("Produto não encontrado",
                        HttpStatus.NOT_FOUND);
            }
        } else {
            log.severe("Usuário não encontrado");
            return new ResponseEntity<String>("Usuário não encontrado",
                    HttpStatus.NOT_FOUND);
        }
    }


    private Optional<Product> findProduct (int code) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Filter codeFilter = new FilterPredicate("Code",
                FilterOperator.EQUAL, code);

        Query query = new Query("Products").setFilter(codeFilter);
        Entity productEntity = datastore.prepare(query).asSingleEntity();

        if (productEntity != null) {
            return Optional.ofNullable(ProductController
                    .entityToProduct(productEntity));
        } else {
            return Optional.empty();
        }
    }
}