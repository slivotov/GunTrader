package ru.forceofshit.gmail;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PasswordGetterTest {
    @Test
    public void getPassword() throws Exception {
        String encodedData = "\n" +
                "Dear forceofsh,\n" +
                "\n" +
                "Here is the Steam Guard code you need to login to account forceofsh:\n" +
                "\n" +
                "H99FN\n" +
                "\n" +
                "\n" +
                "\n" +
                "This email was generated because of a login attempt from a web or mobile device located at 138.128.246.125 (US).  The login attempt included your correct account name and password.\n" +
                "\n" +
                "https://steamcommunity.com/actions/ReportSuspiciousLogin?stoken=64f0dd026c8b06b347aec8596e77934ab397227f5caea82cdd3181b43e4334230a101add55abdfdefebc0638a73bf36c\n" +
                "\n" +
                "The Steam Guard code is required to complete the login.  No one can access your account without also accessing this email.\n" +
                "\n" +
                "If you are not attempting to login then please change your Steam password, and consider changing your email password as well to ensure your account security.\n" +
                "\n" +
                "If you are unable to access your account then use this account specific recovery link for assistance recovering or self-locking your account.\n" +
                "\n" +
                "https://help.steampowered.com/en/wizard/HelpUnauthorizedLogin?stoken=aK57%2FVFPaOgXVEOIFZt%2B%2BQJnAsL6GNootgaGroZ%2BYzEkpWDHI6E3dgZ%2FIG9whcf6df41ASRoN%2BHkcuiLgnESPQ%3D%3D\n" +
                "\n" +
                "\n" +
                "The Steam Team\n" +
                "https://help.steampowered.com\n" +
                "\n" +
                "\n" +
                "\n" +
                "==============\n" +
                "This notification has been sent to the email address associated with your Steam account.\n" +
                "For information on Valve's privacy policy, visit http://www.valvesoftware.com/privacy.htm.\n" +
                "This email message was auto-generated. Please do not respond.\n" +
                "\n" +
                "© Valve Corporation. All rights reserved. All trademarks are property of their respective owners in the US and other countries.\n" +
                "\n";
        PasswordGetter passwordGetter = new PasswordGetter();
        assertEquals("H99FN",passwordGetter.getPasswordFromParsedData(encodedData));
    }

}