package com.single.project.controller;

import com.single.project.domain.member.MemberEntity;
import com.single.project.model.Auth;
import com.single.project.security.TokenProvider;
import com.single.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        MemberEntity result = memberService.register(request);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        MemberEntity memberEntity = memberService.authenticate(request);
        String token = this.tokenProvider.generateToken(memberEntity.getUsername(), memberEntity.getRoles());

        return ResponseEntity.ok(token);
    }
}
