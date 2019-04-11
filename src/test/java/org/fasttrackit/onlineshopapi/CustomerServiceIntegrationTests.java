package org.fasttrackit.onlineshopapi;

import org.fasttrackit.onlineshopapi.domain.Customer;
import org.fasttrackit.onlineshopapi.exception.ResourceNotFoundException;
import org.fasttrackit.onlineshopapi.service.CustomerService;
import org.fasttrackit.onlineshopapi.transfer.customer.CreateCustomerRequest;
import org.fasttrackit.onlineshopapi.transfer.customer.UpdateCustomerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest

public class CustomerServiceIntegrationTests {

    @Autowired
    //IoC
    private CustomerService customerService;

    @Test
    public void testCreateCustomer_whenValidRequest_thenReturnCustomerWithId (){
        Customer customer = createCustomer();

        assertThat(customer, notNullValue());
        assertThat(customer.getId(), greaterThan(0L));
    }

    private Customer createCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setFirstName("Popescu");
        request.setLastName("Elena");
        return customerService.createCustomer(request);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetCustomer_whenCustomerNotFound_thenThrowResourceNotFoundException() throws ResourceNotFoundException {
        customerService.getCustomer(0);
    }

    @Test
    public void testGetCustomer_whenExistingId_thenReturnMatchingCustomer() throws ResourceNotFoundException {
        Customer customer = createCustomer();

        Customer retrievedCustomer = customerService.getCustomer(customer.getId());

        assertThat(retrievedCustomer.getId(), is(customer.getId()));
        assertThat(retrievedCustomer.getFirstName(), is(customer.getFirstName()));
    }
    @Test
    public void testUpdateCustomer_whenValidRequestWithAllFields_thenReturnUpdatedCustomer() throws ResourceNotFoundException {
        Customer createdCustomer = createCustomer();

        UpdateCustomerRequest request = new UpdateCustomerRequest();

        request.setFirstName(createdCustomer.getFirstName() + "_Edited");
        request.setLastName(createdCustomer.getFirstName() + "_Edited");

        Customer updatedCustomer = customerService.updateCustomer(createdCustomer.getId(), request);

        assertThat(updatedCustomer.getFirstName(), is(request.getFirstName()));
        assertThat(updatedCustomer.getLastName(), is(request.getLastName()));
        assertThat(updatedCustomer.getFirstName(), not(is(createdCustomer.getFirstName())));
        assertThat(updatedCustomer.getLastName(), not(is(createdCustomer.getLastName())));


        assertThat(updatedCustomer.getId(), is(createdCustomer.getId()));

    }
    //todo: Implement negative tests for update and test for update with some fields only

    @Test (expected = ResourceNotFoundException.class)
    public void testDeleteCustomer_whenExistingId_theCustomerIsDeleted() throws ResourceNotFoundException {
        Customer createdCustomer = createCustomer();
        customerService.deleteCustomer(createdCustomer.getId());

        customerService.getCustomer(createdCustomer.getId());

    }
}
