package com.has.backend.domain.auth.enums;

public enum EmailVerifyPurpose {
    REGISTER("[HAS] 회원가입 인증 코드입니다.", "register"),
    FIND_ID("[HAS] 아이디 찾기 인증 코드입니다.", "find-id"),
    PASSWORD_RESET("[HAS] 비밀번호 재설정 인증 코드입니다.", "password-reset");

    private final String subject;
    private final String prefix;

    EmailVerifyPurpose(String subject, String prefix) {
        this.subject = subject;
        this.prefix = prefix;
    }

    public String getSubject() {
        return subject;
    }

    public String getPrefix() {
        return prefix;
    }
}
