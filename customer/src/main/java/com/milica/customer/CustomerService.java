package com.milica.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

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
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()

        );

        if(fraudCheckResponse.isFraudster()){
            throw  new IllegalStateException("fraudster");
        }
        //send notification
    }
}
