package com.milica.customer;

import com.milica.clients.fraud.FraudCheckResponse;
import com.milica.clients.fraud.FraudClient;
import com.milica.clients.notification.NotificationClient;
import com.milica.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final NotificationClient notificationClient;
    private final FraudClient fraudClient;


    public void registerCustomer(CustomerRegistrationRequest request){
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        //todo check
        //save and flush so the Id won't be null, if it is just save then Id would be null
        customerRepository.saveAndFlush(customer);
        //todo check if fraudster

        //FRAUD is the name in eureka server
        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        if(fraudCheckResponse.isFraudster()){
            throw  new IllegalStateException("fraudster");
        }
        // todo: make it async. i.e add to queue
        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome...",
                                customer.getFirstName())
                )
        );
    }
}
