package com.account.controller;

import com.account.dto.request.TransactionRequest;

import com.account.dto.response.AccountResponse;
import com.account.dto.response.InternalResponse;
import com.account.service.AccountService;
import com.account.dto.request.RequestAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.transaction.Transaction;
import javax.ws.rs.Path;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    Logger logger = LoggerFactory.getLogger(AccountController.class);

    @PostMapping ("/create")
    public ResponseEntity<Object> create(@RequestParam Long clientId){
        return accountService.create(clientId);
    }

    @GetMapping ("/getByClient/{clientId}")
    public ResponseEntity<Object>  getByClient(@PathVariable Long clientId){
        return accountService.getByClient(clientId);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id){
        return accountService.getById(id);
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAll(){
        return accountService.getAll();
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Object> cancel(@PathVariable Long id){
        return accountService.cancel(id);
    }

    @PutMapping("/setBalance/{id}")
    public ResponseEntity<Object> setBalance(@PathVariable Long id, @RequestParam BigDecimal amount){
        return accountService.setBalance(id, amount);
    }

    @GetMapping("/is-active/{accountNumber}")
    public InternalResponse isActive(@PathVariable String accountNumber){
        logger.info("Se verifica la cuenta: "+accountNumber );
        return accountService.isActive(accountNumber);
    }

    @PutMapping("/validate")
    public InternalResponse validate(@RequestBody TransactionRequest validateRequest){
        try {
            InternalResponse response=accountService.validateCommon(validateRequest);
            logger.info(String.valueOf(response));
            return response;
        } catch (Exception e) {
            return new InternalResponse(e.getMessage(), 505);
        }

    }

    @PutMapping("/executeTransaction")
    public ResponseEntity<Object> execute(@RequestBody TransactionRequest transactionRequest ){
       try {
           return accountService.execute(transactionRequest);
       } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PutMapping("/validate-card")
    public  InternalResponse validateCard(@RequestBody TransactionRequest transactionRequest){

            return accountService.validateCard(transactionRequest);



    }

    @PutMapping("/add-card/{id}")
    ResponseEntity<Object> addCard(@PathVariable Long id){
        return accountService.addCard(id);
    }

}
