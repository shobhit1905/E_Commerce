package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressDTO;
import com.ecommerce.project.exceptions.APIExceptions;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    public AddressDTO addNewAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO , Address.class) ;

        // need to update both sides of the relationship
        List<Address> addressList = user.getAddresses() ;
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user) ;

        Address savedAddress = addressRepository.save(address);
        AddressDTO savedAddressDTO = modelMapper.map(savedAddress , AddressDTO.class) ;

        return savedAddressDTO ;
    }

    public List<AddressDTO> getAllAddresses() {

        List<Address> addresses = addressRepository.findAll() ;

        if(addresses == null) {
            throw new APIExceptions("No addresses found") ;
        }
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(address -> {
                    AddressDTO addressDTO = modelMapper.map(address , AddressDTO.class) ;
                    return addressDTO ;
                }).toList() ;

        return addressDTOs ;
    }

    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address" , "addressId", addressId)
        ) ;

        AddressDTO addressDTO = modelMapper.map(address , AddressDTO.class) ;
        return addressDTO ;
    }

    public List<AddressDTO> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.findAddressByUserId(userId) ;
        if(addresses == null) {
            throw new APIExceptions("No addresses found") ;
        }
        List<AddressDTO> addressDTOs = addresses.stream().map(
                address -> {
                    AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class) ;
                    return addressDTO ;
                }
        ).toList() ;

        return addressDTOs ;
    }

    public AddressDTO updateAddress(@Valid AddressDTO addressDTO, Long addressId) {
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address" , "addressId", addressId)
        ) ;

        Address updatedAddress = modelMapper.map(addressDTO , Address.class) ;

        existingAddress.setCity(updatedAddress.getCity());
        existingAddress.setCountry(updatedAddress.getCountry());
        existingAddress.setState(updatedAddress.getState());
        existingAddress.setZipCode(updatedAddress.getZipCode());
        existingAddress.setApartmentNumber(updatedAddress.getApartmentNumber());
        existingAddress.setStreet(updatedAddress.getStreet());

        Address savedAddress = addressRepository.save(existingAddress);

        return modelMapper.map(savedAddress , AddressDTO.class) ;
    }

    public String deleteAddress(Long addressId) {
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address" , "addressId", addressId)
        ) ;

        User user = existingAddress.getUser() ;
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(existingAddress);
        return "Address details for addressId : " + addressId + " deleted successfully" ;
    }
}
