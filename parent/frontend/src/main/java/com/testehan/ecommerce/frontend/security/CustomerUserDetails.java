package com.testehan.ecommerce.frontend.security;

import com.testehan.ecommerce.common.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// A Customer as defined by us, but with extra information that is integrated into the spring security.
// This is the "principal" object used in the html files
public class CustomerUserDetails implements UserDetails {

    private Customer customer;

    public CustomerUserDetails(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;    // because for customers we don't have authorities
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    public String getFullName(){
        return customer.getFullName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return customer.isEnabled();
    }

    public void setFirstName(String firstName){
        this.customer.setFirstName(firstName);
    }

    public void setLastName(String lastName){
        this.customer.setLastName(lastName);
    }

}
