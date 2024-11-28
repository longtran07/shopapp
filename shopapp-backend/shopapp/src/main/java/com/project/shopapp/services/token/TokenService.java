package com.project.shopapp.services.token;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private static final int MAX_TOKENS = 3;
    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;


    @Transactional
    @Override
    public Token addToken(User user, String token, boolean isMobileDevice) {
        List<Token> userTokens=tokenRepository.findByUser(user);
        int tokenCount=userTokens.size();
        if(tokenCount>=MAX_TOKENS) {
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::isMobile);
            Token tokenToDelete;
            if(hasNonMobileToken) {
                tokenToDelete=userTokens.stream()
                        .filter(userToken -> !userToken.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            }
            else {
                tokenToDelete=userTokens.get(0);
            }
            tokenRepository.delete(tokenToDelete);
        }
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime= LocalDateTime.now().plusSeconds(expirationInSeconds);
        Token newToken=Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .isMobile(isMobileDevice)
                .build();

        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);
        return newToken;
    }

    @Transactional
    @Override
    public Token refreshToken(String refreshToken, User user) throws Exception {
        Token exitingToken=tokenRepository.findByRefreshToken(refreshToken);
        if(exitingToken==null) {
            throw new DataNotFoundException("Refresh token does not exist");
        }
        if(exitingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(exitingToken);
            throw new DataNotFoundException("Refresh token expired");
        }
        String token = jwtTokenUtils.generateToken(user);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
        exitingToken.setExpirationDate(expirationDateTime);
        exitingToken.setToken(token);
        exitingToken.setRefreshToken(UUID.randomUUID().toString());
        exitingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        return exitingToken;
    }
}
