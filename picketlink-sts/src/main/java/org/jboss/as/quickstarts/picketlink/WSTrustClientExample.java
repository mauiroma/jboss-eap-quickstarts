/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.picketlink;

import com.sun.security.auth.UserPrincipal;
import org.picketlink.common.util.DocumentUtil;
import org.picketlink.identity.federation.api.wstrust.WSTrustClient;
import org.picketlink.identity.federation.api.wstrust.WSTrustClient.SecurityInfo;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAMLUtil;
import org.w3c.dom.Element;

/**
 * This class demonstrates how to request SAML 2.0 security token from PicketLink STS.
 *
 * @author Peter Skopek (pskopek ( at redhat dot com))
 *
 */
public class WSTrustClientExample {
    String userName ="";
    String password = "";

    public WSTrustClientExample(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    private Element getSAMLV2(){
        Element assertionElement = null;
        try {
            WSTrustClient client = new WSTrustClient("PicketLinkSTS", "PicketLinkSTSPort", "http://localhost:8080/picketlink-sts/PicketLinkSTS",
                    new SecurityInfo(userName, password));
            System.out.println("Invoking token service to get SAML assertion for user:" + userName + " with password:" + password);
            // Step 2: Get a SAML2 Assertion Token from the PicketLink STS
            assertionElement = client.issueToken(SAMLUtil.SAML2_TOKEN_TYPE);
            System.out.println("SAML assertion for user:" + userName + " successfully obtained!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return assertionElement;
    }

    private Element getSAMLV1(){
        Element assertionElement = null;
        try {
            WSTrustClient client = new WSTrustClient("PicketLinkSTS", "PicketLinkSTSPort", "http://localhost:8080/picketlink-sts/PicketLinkSTS",
                    new SecurityInfo(userName, password));
            System.out.println("Invoking token service to get SAML assertion for user:" + userName + " with password:" + password);
            // Step 2: Get a SAML2 Assertion Token from the PicketLink STS
            assertionElement = client.issueToken(SAMLUtil.SAML11_TOKEN_TYPE);
            System.out.println("SAML assertion for user:" + userName + " successfully obtained!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return assertionElement;
    }

    private Element getSAMLV1Behalf(){
        Element assertionElement = null;
        try {
            WSTrustClient client = new WSTrustClient("PicketLinkSTS", "PicketLinkSTSPort", "http://localhost:8080/picketlink-sts/PicketLinkSTS",
                    new SecurityInfo(userName, password));
            System.out.println("Invoking token service to get SAML assertion for user:" + userName + " with password:" + password);
            // Step 2: Get a SAML2 Assertion Token from the PicketLink STS
            UserPrincipal principal = new UserPrincipal("maui");
            assertionElement = client.issueTokenOnBehalfOf("http://localhost:8080/picketlink-sts/PicketLinkSTS", SAMLUtil.SAML11_BEARER_URI, principal);
            System.out.println("SAML assertion for user:" + userName + " successfully obtained!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return assertionElement;
    }


    public static void main(String[] args) throws Exception {
        String userName = (args.length > 0 ? args[0] : "jbadmin");
        System.out.println(userName);
        String password = (args.length > 1 ? args[1] : "password");
        System.out.println(password);
        WSTrustClientExample clientExample =  new WSTrustClientExample(userName,password);
        System.out.println("SAML V2");
        System.out.println(DocumentUtil.getDOMElementAsString(clientExample.getSAMLV2()));
        System.out.println("SAML V1");
        System.out.println(DocumentUtil.getDOMElementAsString(clientExample.getSAMLV1()));
        System.out.println("SAML V1 Behalf");
        System.out.println(DocumentUtil.getDOMElementAsString(clientExample.getSAMLV1Behalf()));
    }
}
