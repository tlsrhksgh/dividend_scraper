package com.single.project.model;

import com.single.project.domain.member.MemberEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

public class Auth {

    @Getter
    @Setter
    public static class SignIn {
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }
}
