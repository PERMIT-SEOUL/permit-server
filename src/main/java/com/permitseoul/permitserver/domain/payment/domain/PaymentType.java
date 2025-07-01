package com.permitseoul.permitserver.domain.payment.domain;

public enum PaymentType {
    CARD, //카드
    EASY_PAY, //간편결제
    가상계좌, //VIRTUAL_ACCOUNT
    MOBILE_PHONE, //휴대폰
    TRANSFER, //계좌이체
    CULTURE_GIFT_CERTIFICATE, //문화상품권
    BOOK_GIFT_CERTIFICATE, //도서문화상품권
    GAME_GIFT_CERTIFICATE, //게임문화상품권
}
