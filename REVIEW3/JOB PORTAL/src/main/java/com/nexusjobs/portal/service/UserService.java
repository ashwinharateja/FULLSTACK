package com.nexusjobs.portal.service;

import com.nexusjobs.portal.model.User;
import com.nexusjobs.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Spring Security: load by username (= email) */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    @Transactional
    public User register(String firstName, String lastName, String email,
                         String rawPassword, User.Role role, String company) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }
        int idx = (int)(userRepository.count() % 4) + 1;
        User user = User.builder()
                .name(firstName + " " + lastName)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .company(company)
                .avatarClass("avatar-gradient-" + idx)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(Long userId, User updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (updates.getName() != null)       user.setName(updates.getName());
        if (updates.getTitle() != null)      user.setTitle(updates.getTitle());
        if (updates.getLocation() != null)   user.setLocation(updates.getLocation());
        if (updates.getSkills() != null)     user.setSkills(updates.getSkills());
        if (updates.getBio() != null)        user.setBio(updates.getBio());
        if (updates.getCompany() != null)    user.setCompany(updates.getCompany());
        if (updates.getAbout() != null)      user.setAbout(updates.getAbout());
        if (updates.getWebsite() != null)    user.setWebsite(updates.getWebsite());
        if (updates.getEducation() != null)  user.setEducation(updates.getEducation());
        if (updates.getSalary() != null)     user.setSalary(updates.getSalary());
        if (updates.getExperience() != null) user.setExperience(updates.getExperience());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long countAll()                   { return userRepository.count(); }
    public long countByRole(User.Role role)  { return userRepository.findByRole(role).size(); }
}
