package com.university.schedule.converter;

import com.university.schedule.dto.DefaultUserDetails;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.*;
import com.university.schedule.service.AuthorityService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class UserDetailsMapper {

    private final ModelMapper modelMapper;

    public UserDetailsMapper(@Autowired AuthorityService authorityService) {
        this.modelMapper = new ModelMapper();
        Converter<Role, List<SimpleGrantedAuthority>> converter = role -> {
            List<Authority> authorities = authorityService.findByRole(role.getSource());
            List<SimpleGrantedAuthority> simpleGrantedAuthorities =
                    new ArrayList<>(authorities.stream().map(
                            authority -> new SimpleGrantedAuthority(authority.getName())).toList());
            simpleGrantedAuthorities.add(
                    new SimpleGrantedAuthority(String.format("ROLE_%s", role.getSource().getName().toUpperCase())));
            return simpleGrantedAuthorities;
        };

        modelMapper.typeMap(User.class, DefaultUserDetails.class).addMappings(
                modelMapper -> {
                    modelMapper.using(converter)
                            .map(User::getRole, DefaultUserDetails::setGrantedAuthorities);
                    modelMapper.map(User::isEnable, DefaultUserDetails::setIsEnable);
                });

    }

    public UserDetails convertToUserDetails(User user) {
        return modelMapper.map(user, DefaultUserDetails.class);
    }
}
