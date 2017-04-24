package ru.forceofshit.gmail;

import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.forceofshit.gmail.GmailService.getGmailService;

public class PasswordGetter {

    private static String user = "me";
    private static Logger log = Logger.getLogger(PasswordGetter.class.getName());

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
        List<Message> messageList = Lists.newArrayList();
        while(messageList.isEmpty()) {
            log.info("Trying to get new message from Steam with Password");
            messageList = service.users().messages().list(user).setQ(qParameter).execute().getMessages();
        }
        Message lastMessage = messageList.get(0);
        String lastMessageId = lastMessage.getId();
        ModifyMessageRequest requestContent = new ModifyMessageRequest().setRemoveLabelIds(Lists.newArrayList("UNREAD"));
        service.users().messages().modify(user, lastMessageId, requestContent).execute();
        return service.users().messages().get(user, lastMessageId).execute();
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
