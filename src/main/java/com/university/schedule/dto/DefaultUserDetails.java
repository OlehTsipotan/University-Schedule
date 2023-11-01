package com.university.schedule.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class DefaultUserDetails implements UserDetails {

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private Collection<? extends GrantedAuthority> grantedAuthorities;

    @NonNull
    private Boolean isEnable;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return isEnable;
    }

    public boolean equals(Object obj) {
        return obj instanceof DefaultUserDetails && this.getUsername().equals(((DefaultUserDetails) obj).getUsername());
    }
}
