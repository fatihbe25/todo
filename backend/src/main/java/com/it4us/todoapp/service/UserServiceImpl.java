package com.it4us.todoapp.service;

import com.it4us.todoapp.dto.UserCreateDto;
import com.it4us.todoapp.dto.UserSignInDto;
import com.it4us.todoapp.dto.UserSignInResponse;
import com.it4us.todoapp.dto.UserViewDto;
import com.it4us.todoapp.entity.User;
import com.it4us.todoapp.exception.UserExistException;
import com.it4us.todoapp.repository.UserRepository;
import com.it4us.todoapp.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService{


    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserViewDto create(UserCreateDto userCreateDto) {
        String uuid = UUID.randomUUID().toString();
        User user = new User();

        if(isEmailExist(userCreateDto.getEmail()))
            throw new UserExistException("user already exist");
        else if(userCreateDto.getUsername() == null)
            userCreateDto.setUsername(createUsernameIfNoPresent(userCreateDto));


        user.setUsername(userCreateDto.getUsername());
        user.setEmail(userCreateDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userCreateDto.getPassword()));
        user.setId(uuid);

        return UserViewDto.of(userRepository.save(user));
    }

    @Override
    public Boolean isEmailExist(String email) {

        Optional<?> user = userRepository.findByEmail(email);

        return user.isPresent();
    }

    @Override
    public String createUsernameIfNoPresent(UserCreateDto userCreateDto) {
        String[] temp = userCreateDto.getEmail().split("@");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(temp[0]);

        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            stringBuilder.append(random.nextInt(9));
        }
        return stringBuilder.toString();
    }

    @Override
    public UserSignInResponse login(UserSignInDto userSignInDto) {

        User user = userRepository.findByEmail(userSignInDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        UserSignInResponse userSignInResponse = UserSignInResponse.of(user);

        userSignInResponse.setToken(JwtUtils.generateToken(user));

        return userSignInResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User is Not Found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new ArrayList<>());
    }
}
