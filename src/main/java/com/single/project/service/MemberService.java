package com.single.project.service;

import com.single.project.domain.member.MemberEntity;
import com.single.project.domain.member.MemberRepository;
import com.single.project.model.Auth;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다. username : " + username));
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean exists = memberRepository.existsByUsername(member.getUsername());
        if(exists) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        MemberEntity result = memberRepository.save(member.toEntity());

        return result;
    }

    public MemberEntity authenticate(Auth.SignIn member) {

        MemberEntity existsUser = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 ID 입니다."));

        if(!this.passwordEncoder.matches(member.getPassword(), existsUser.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return existsUser;
    }
}
