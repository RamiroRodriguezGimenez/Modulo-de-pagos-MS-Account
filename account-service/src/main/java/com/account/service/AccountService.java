package com.account.service;

import com.account.dto.request.CardTransactionRequest;
import com.account.dto.request.TransactionRequest;

import com.account.dto.response.AccountResponse;
import com.account.dto.response.InternalResponse;
import com.account.repository.AccountRepository;
import com.account.dto.request.RequestAccount;
import com.account.entity.Account;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    Logger logger = LoggerFactory.getLogger(AccountService.class);

    public ResponseEntity<Object> create(Long clientId){


        Account account= new Account(newNumber(clientId), clientId);
        accountRepository.save(account);

        account.setNumber(newNumber(clientId));
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getByClient(Long clientId){
        List<AccountResponse> accounts= accountRepository.findByClientId(clientId).stream().map(AccountResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    private String newNumber(Long id){
        String number="";
        for (int i = 0; i < 4; i++) {

            if (id>0){
                number =  Long.toString((id%10)) + number;
            } else {
                number="0"+number;
            }
            id=(id-(id%3))/10;
        }

                number = number + String.valueOf((int) ((Math.random()*(99999999))+10000000));

        return number;
    }

    public ResponseEntity<Object> getById(Long id) {
        Account response =accountRepository.findById(id).orElse(null);
        if (response==null){
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Object> getAll() {
        List<Account> accountResponses = accountRepository.findAll();
        return new ResponseEntity<>(accountResponses, HttpStatus.OK);
    }

    public ResponseEntity<Object> cancel(Long id) {
        Account account= accountRepository.findById(id).orElse(null);
        if (account==null){
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }
        account.setActive(!account.isActive());
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Object> setBalance(Long id, BigDecimal amount) {
        Account account= accountRepository.findById(id).orElse(null);
        if (account==null){
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public InternalResponse isActive(String number) {
        Account account= accountRepository.findByNumber(number);
        logger.info("se encuentra la cuenta: "+account);
        if (account.isActive()) {return new InternalResponse(200);}
        return new InternalResponse("Account not found", 404);
    }
    @Transactional
    public InternalResponse validateCommon(TransactionRequest validateRequest) throws Exception {
        Account accountOrigin;
        Account accountDestination;
        try {
            accountOrigin=accountRepository.findByNumber(validateRequest.getAccountOrigin());
            accountDestination=accountRepository.findByNumber(validateRequest.getAccountDestination());

        } catch (Exception e) {
            return new InternalResponse("Account not found", 404);
        }

        if (!accountOrigin.isActive() || !accountDestination.isActive()) {return new InternalResponse("Account is not active", 400);}
        BigDecimal availableMoney= accountOrigin.getBalance().subtract(accountOrigin.getReservedMoney());
        logger.info("comparacion = "+validateRequest.getAmount().compareTo(availableMoney));
        if (validateRequest.getAmount().compareTo(availableMoney)==1){ return new InternalResponse("Insufficient founds", 400);}

        try{
            accountOrigin.setReservedMoney(accountOrigin.getReservedMoney().add(validateRequest.getAmount()));
            accountRepository.save(accountOrigin);
            logger.info("se guardo despues de verificar: "+accountOrigin);
        } catch (Exception e){
            throw new Exception("Persist error");
        }
        return new InternalResponse(200);
    }

    @Transactional
    public ResponseEntity<Object> execute(TransactionRequest transactionRequest) throws Exception {
        Account accountOrigin;
        Account accountDestination;
        logger.info("se ejecutara la siguiente transaccion: "+transactionRequest);
        try {
            accountOrigin=accountRepository.findByNumber(transactionRequest.getAccountOrigin());
            accountDestination=accountRepository.findByNumber(transactionRequest.getAccountDestination());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }
        try{
            accountOrigin.setReservedMoney(accountOrigin.getReservedMoney().subtract(transactionRequest.getAmount()));
            accountOrigin.setBalance(accountOrigin.getBalance().subtract(transactionRequest.getAmount()));
            accountDestination.setBalance(accountDestination.getBalance().add(transactionRequest.getAmount()));
            accountRepository.save(accountOrigin);
            accountRepository.save(accountDestination);
            logger.info("se guardo despues de ejecutar :  origin "+ accountOrigin + "destination: "+accountDestination);
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new Exception("Error  with data base, please try again later");
        }
    }

    public InternalResponse validateCard(TransactionRequest request){
        Account accountOrigin;
        try{
            accountOrigin=accountRepository.findByCardNumber(request.getCardNumber());
            logger.info("Cuenta encontrada: "+accountOrigin);
            request.setAccountOrigin(accountOrigin.getNumber());
            InternalResponse response= validateCommon(request);
            if (accountOrigin==null){
                return new InternalResponse("Account not found", 404);
            }
            if (response.getStatusCode()==202) {

                return new InternalResponse(accountOrigin.getNumber(), 200);
            }
            return response;
        } catch (Exception e){
            return new InternalResponse("Account not found", 404);
        }
    }

    public ResponseEntity<Object> addCard(Long id){
        Account account;
        try {
            Optional<Account> response = accountRepository.findById(id);
            if (response.isEmpty()){ return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);}
            account= response.get();
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        try{
            account.setCardNumber(newCardNumber(id));
            accountRepository.save(account);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Error with the data base, please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String newCardNumber(Long id){
        String number="";
        for (int i = 0; i < 4; i++) {

            if (id>0){
                number =  Long.toString((id%10)) + number;
            } else {
                number="0"+number;
            }
            id=(id-(id%3))/10;
        }
        for (int i = 0; i < 3; i++) {
            number=number+" ";
            number = number + String.valueOf((int) ((Math.random()*(9999))+1000));
        }


        return number;
    }

    public void executeSendMoney(TransactionRequest transactionRequest){
        Account account= accountRepository.findByNumber(transactionRequest.getAccountDestination());
        account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        accountRepository.save(account);
    }
}
