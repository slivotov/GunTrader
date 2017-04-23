package ru.forceofshit.gmail;

import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.forceofshit.gmail.GmailService.getGmailService;

public class PasswordGetter {

    private static String user = "me";

    public String getPassword() throws IOException {
        Gmail service = getGmailService();
        Message message = getLastEmail(service);
        String encodedData = getEncodedData(message);
        String decodedData = getDecodedData(encodedData);
        return getPasswordFromParsedData(decodedData);
    }

    private String getDecodedData(String encodedData) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(encodedData));
    }

    private String getEncodedData(Message message) {
        return message.getPayload().getParts().get(0).getBody().getData();
    }

    private Message getLastEmail(Gmail service) throws IOException {
        //read here about qParameter https://support.google.com/mail/answer/7190?hl=en
        String qParameter = "is:unread from:noreply@steampowered.com";
        return service.users().messages().list(user).setQ(qParameter).execute().getMessages().get(0);
    }

    String getPasswordFromParsedData(String decodedData) {
        Pattern MY_PATTERN = Pattern.compile("[A-Z1-9]{5}");
        Matcher m = MY_PATTERN.matcher(decodedData);
        if(!m.find()){
            throw new RuntimeException("I can't find password in email");
        }
        return m.group(0);
    }
}
