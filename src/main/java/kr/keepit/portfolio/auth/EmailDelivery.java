package kr.keepit.portfolio.auth;
public interface EmailDelivery {
    void send(String recipient, String subject, String text);
}
