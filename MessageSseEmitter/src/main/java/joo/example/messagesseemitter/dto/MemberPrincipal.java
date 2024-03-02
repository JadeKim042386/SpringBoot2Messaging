package joo.example.messagesseemitter.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public record MemberPrincipal(
        Long id,
        String username,
        String password,
        String nickname,
        Collection<? extends GrantedAuthority> authorities)
        implements UserDetails {

    public static MemberPrincipal of(Long id, String username, String password, String nickname) {
        return new MemberPrincipal(
                id,
                username,
                password,
                nickname,
                Set.of(new SimpleGrantedAuthority("USER")));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }
}
