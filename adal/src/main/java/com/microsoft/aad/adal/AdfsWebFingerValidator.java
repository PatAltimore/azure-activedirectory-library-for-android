package com.microsoft.aad.adal;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates trusts between authorities and ADFS instances using DRS metadata and WebFinger.
 */
final class AdfsWebFingerValidator {



    private AdfsWebFingerValidator() {
        // utility class
    }

    /**
     * Used for logging.
     */
    private static final String TAG = "AdfsWebFingerValidator";

    /**
     * Constant identifying trust between two realms.
     */
    private static final String TRUSTED_REALM_REL = "http://schemas.microsoft.com/rel/trusted-realm";

    /**
     * Verify that trust is established between IDP and the SP.
     *
     * @param authority the endpoint used
     * @param metadata  the {@link WebFingerMetadata} to consult
     * @return True, if trust exists: otherwise false.
     */
    static boolean realmIsTrusted(URL authority, WebFingerMetadata metadata) {
        Logger.v(TAG, "Verifying trust: " + authority.toString() + metadata.toString());
        String href, rel, host;
        for (Link link : metadata.getLinks()) {
            href = link.getHref();
            rel = link.getRel();
            host = authority.getProtocol() + "://" + authority.getHost();
            if (href.equalsIgnoreCase(host) && rel.equalsIgnoreCase(TRUSTED_REALM_REL)) {
                return true;
            }
        }
        return false;
    }

}
