package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AddressDTO;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService ;


    @Autowired
    private AuthUtil authUtil;

    // adding a new address
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addNewAddress(@RequestBody @Valid AddressDTO addressDTO) {
        // using a helper class AuthUtil , which provides us the information of the logged in user
        User user = authUtil.loggedInUser() ;

        // we also need to pass the logged in user , because for that user the address is going to be saved
        AddressDTO savedAddressDTO = addressService.addNewAddress(addressDTO , user) ;
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    // getting all the addresses saved in the db (useful for admin purposes)
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = addressService.getAllAddresses() ;
        return new ResponseEntity<>(addressDTOS, HttpStatus.FOUND);
    }

    // getting address using addressId
    @GetMapping("/{addressId}/address")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable("addressId") Long addressId) {
        AddressDTO savedAddress = addressService.getAddressById(addressId) ;
        return new ResponseEntity<>(savedAddress, HttpStatus.FOUND);
    }

    // getting all the saved addresses against a user
    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(){
        User user = authUtil.loggedInUser() ;
        Long userId = user.getUserId() ;
        List<AddressDTO> addresses = addressService.getUserAddresses(userId) ;
        return new ResponseEntity<>(addresses, HttpStatus.FOUND);
    }

    // update existing address
    @PutMapping("/{addressId}/address")
    public ResponseEntity<AddressDTO> updateAddress(@RequestBody @Valid AddressDTO addressDTO,
                                                    @PathVariable("addressId") Long addressId) {
        AddressDTO updatedAddress = addressService.updateAddress(addressDTO , addressId) ;
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    // delete existing address , performed by the logged in user in his/her saved addresses
    @DeleteMapping("/user/{addressId}/address")
    public ResponseEntity<String> deleteAddress(@PathVariable("addressId") Long addressId){
        String status = addressService.deleteAddress(addressId) ;
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
