package com.thesis.auth_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.thesis.auth_service.document.Auth;
import com.thesis.auth_service.dto.request.IntrospectRequest;
import com.thesis.auth_service.dto.request.LoginRequest;
import com.thesis.auth_service.dto.request.RegisterRequest;
import com.thesis.auth_service.dto.request.UserRequest;
import com.thesis.auth_service.dto.response.ApiResponse;
import com.thesis.auth_service.dto.response.IntrospectResponse;
import com.thesis.auth_service.dto.response.TokenResponse;
import com.thesis.auth_service.repository.AuthRepository;
import com.thesis.auth_service.repository.httpClient.UserClient;
import lombok.experimental.NonFinal;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
    @Autowired
    AuthRepository authRepository;

    @Autowired
    UserClient userClient;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Value("${jwt.expiration}")
    private long expiration;

//    @NonFinal
//    @Value("${spring.jwt.valid-duration}")
//    protected long VALID_DURATION;
//
//    @NonFinal
//    @Value("${spring.jwt.refreshable-duration}")
//    protected long REFRESHABLE_DURATION;


    public List<Auth> getAll() {
        return authRepository.findAll();
    }

    Logger log = LoggerFactory.getLogger(AuthService.class);

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public IntrospectResponse introspect(@RequestBody IntrospectRequest request)
            throws JOSEException, ParseException {

        var token = request.getToken();

        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(jwsVerifier);

        return IntrospectResponse.builder()
                .valid(verified && expireTime.after(new Date()))
                .build();
    }

//    public InstrospectResponse instrospect(InstrospectRequest request){
//        var token = request.getToken();
//        boolean isValid = true;
//
//        try{
//            verifyToken(token,false);
//        } catch(Exception e) {
//            isValid = false;
//        }
//
//        return InstrospectResponse.builder().valid(isValid).build();
//    }



    public ApiResponse register (RegisterRequest request){
        if (authRepository.existsByEmail(request.getEmail()))
            return ApiResponse.builder().code(400).message("Email existed").data(null).build();

        if (!request.getPassword().equals(request.getRePassword()))
            return ApiResponse.builder().code(400).message("Password is not matched").data(null).build();

        //Random user_id for microservice
        String userId = UUID.randomUUID().toString();

        Auth registerUser = new Auth();
        registerUser.setUser_id(userId);
        registerUser.setEmail(request.getEmail());
        registerUser.setUsername(request.getUsername());
        registerUser.setPassword(passwordEncoder.encode(request.getPassword()));
        registerUser.setRoles(Collections.singleton("USER"));
        registerUser.setStatus("ACTIVE");

        //Get current timestamp
        Instant getCurrentTime = Instant.now();
        registerUser.setCreated_at(getCurrentTime.toString());
        registerUser.setUpdated_at(getCurrentTime.toString());

        Auth savedNewUser = authRepository.save(registerUser);

        try {
            //Send data to user_service
            UserRequest data = new UserRequest();
            data.setUser_id(userId);
            data.setName(request.getName());
            data.setGender(request.getGender());
            data.setPhone(request.getPhone());
            data.setAvatar(request.getAvatar());
            data.setBirthday(request.getBirthday());

            //Calling feign
            userClient.createUserFeign(data);
        } catch (Exception e) {
            log.info("ERROR: {}",e.getMessage());
            authRepository.deleteById(savedNewUser.getId());

            return ApiResponse.builder()
                    .code(500)
                    .message(String.format("An error occurred during registration. ERROR: %s",e.getMessage()))
                    .data(null)
                    .build();
        }

        return ApiResponse.builder()
                .code(200)
                .message("Register new account successfully!")
                .data(registerUser)
                .build();
    }

    public ApiResponse login(LoginRequest request){

        Auth getUserByEmail = authRepository.getAuthByEmail(request.getEmail());

        if (getUserByEmail == null)
            return ApiResponse.builder().code(400).message("Wrong email. Please try again!").data(null).build();

        if (!passwordEncoder.matches(request.getPassword(), getUserByEmail.getPassword()))
            return ApiResponse.builder().code(400).message("Wrong password. Please try again!").data(null).build();

        var token = generateToken(getUserByEmail);
        TokenResponse tokenResponse = TokenResponse
                .builder()
                .token(token)
                .token_type("Bear")
                .build();

        return ApiResponse.builder()
                .code(200)
                .message("Login successfully!")
                .data(tokenResponse)
                .build();
    }

    private String generateToken(Auth authInfo){
        //Build header with HS512 Algorithm
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        //Define Claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(authInfo.getEmail())
                .issuer("trungnguyen_keraunos.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expiration))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(authInfo))
                .build();
        //Payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        //JWT = header + payload
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

//    private SignedJWT verifyToken(String token, boolean isRefresh) throws
//            JOSEException, ParseException{
//        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
//
//        SignedJWT signedJWT = SignedJWT.parse(token);
//
//        Date expiryTime = (isRefresh)
//                ? new Date(signedJWT
//                    .getJWTClaimsSet()
//                    .getIssueTime()
//                    .toInstant()
//                    .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
//                    .toEpochMilli())
//                : signedJWT.getJWTClaimsSet().getExpirationTime();
//
//        var verified = signedJWT.verify(verifier);
//
//        if (!(verified && expiryTime.after(new Date()))) throw new RuntimeException("UNAUTHENTICATED");
//
//        return signedJWT;
//    }

    private String buildScope(Auth auth) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(auth.getRoles()))
            auth.getRoles().forEach(stringJoiner::add);

        return stringJoiner.toString();
    }

}
