package com.odeyalo.kyrie.core.oauth2.oidc;

import io.jsonwebtoken.lang.Assert;
import lombok.*;

/**
 * Data class to represent the standard fields in user info by OpenID specification
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#Claims">OpedID Standard Claims</a>
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class OidcUserInfo {
    // Represents the subject claim. User’s identifier (sub)
    private final String subject;
    // Represents the issuer claim. Authorization server’s identifier (iss)
    private final String issuer;
    // Represent the audience claim. Client’s identifier (aud)
    private final String aud;
    // Time at which the ID token was issued
    private final Long issuedAt;
    // Expiration time of the ID token (exp)
    private final Long expiresTime;
    // 	End-User's full name in displayable form including all name parts, possibly including titles and suffixes, ordered according to the End-User's locale and preferences.
    private String name;
    // Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names; all can be present, with the names being separated by space characters.
    private String givenName;
    //Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or no family name; all can be present, with the names being separated by space characters.
    private String familyName;
    // 	Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; all can be present, with the names being separated by space characters. Also note that in some cultures, middle names are not used.
    private String middleName;
    // Casual name of the End-User that may or may not be the same as the given_name. For instance, a nickname value of Mike might be returned alongside a given_name value of Michael
    private String nickname;
    // Shorthand name by which the End-User wishes to be referred to at the RP, such as janedoe or j.doe. This value MAY be any valid JSON string including special characters such as @, /, or whitespace.
    private String preferredUsername;
    // URL of the End-User's profile page. The contents of this Web page SHOULD be about the End-User.
    private String profile;
    // URL of the End-User's profile picture. This URL MUST refer to an image file (for example, a PNG, JPEG, or GIF image file), rather than to a Web page containing an image. Note that this URL SHOULD specifically reference a profile photo of the End-User suitable for displaying when describing the End-User, rather than an arbitrary photo taken by the End-User.
    private String picture;
    // End-User's preferred e-mail address
    private String email;
    // True if the End-User's e-mail address has been verified; otherwise false
    private boolean emailVerified;
    // End-User's gender. Values defined by this specification are female and male
    private String gender;
    // End-User's birthday, represented as an ISO 8601:2004 [ISO8601‑2004] YYYY-MM-DD format. The year MAY be 0000, indicating that it is omitted. To represent only the year, YYYY format is allowed.
    private String birthday;
    // String from zoneinfo [zoneinfo] time zone database representing the End-User's time zone. For example, Europe/Paris or America/Los_Angeles.
    private String zoneInfo;
    //End-User's locale, represented as a BCP47 [RFC5646] language tag. This is typically an ISO 639-1 Alpha-2 [ISO639‑1] language code in lowercase and
    // an ISO 3166-1 Alpha-2 [ISO3166‑1] country code in uppercase, separated by a dash.
    // For example, en-US or fr-CA. As a compatibility note, some implementations have used an underscore as the separator rather than a dash,
    // for example, en_US; Relying Parties MAY choose to accept this locale syntax as well.
    private String locale;
    // End-User's preferred telephone number
    private String phoneNumber;
    // True if the End-User's phone number has been verified; otherwise false.
    private String phoneNumberVerified;
    private Address address;
    // Time the End-User's information was last updated. Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time.
    private Long updatedAt;

    private static OidcUserInfoBuilder builder() {
        throw new UnsupportedOperationException("The OidcIdTokenBuilder does not support empty builder");
    }

    /**
     * @return OidcTokenBuilder with required fields
     */
    public static OidcUserInfoBuilder builder(String issuer, String subject, String aud, Long issuedAt, Long expiresTime) {
        Assert.notNull(issuer, "The issuer cannot be empty");
        Assert.notNull(subject, "The subject cannot be empty");
        Assert.notNull(aud, "The aud cannot be empty");
        Assert.notNull(issuedAt, "The issuedAt cannot be empty");
        Assert.notNull(expiresTime, "The expiresTime cannot be empty");
        return new OidcUserInfoBuilder().issuer(issuer).subject(subject).aud(aud).issuedAt(issuedAt).expiresTime(expiresTime);
    }

    @Data
    @Builder
    public static class Address {
        // Country name component.
        private String country;
        // Zip code or postal code component.
        private String postalCode;
        // State, province, prefecture, or region component.
        private String region;
        // City or locality component.
        private String locality;
        // Full street address component, which MAY include house number, street name, Post Office Box, and multi-line extended street address information. This field MAY contain multiple lines, separated by newlines.
        // Newlines can be represented either as a carriage return/line feed pair ("\r\n") or as a single line feed character ("\n").
        private String streetAddress;
    }
}
