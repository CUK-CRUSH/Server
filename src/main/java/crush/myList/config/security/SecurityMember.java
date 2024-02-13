package crush.myList.config.security;

import crush.myList.domain.member.enums.RoleName;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityMember implements UserDetails {
    private Long id;
    private String oauth2id;
    private String username;
    private String name;
    private RoleName role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(role.getValue());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return null;
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
