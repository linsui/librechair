/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package kg.net.bazi.gsb4j.properties;

import com.google.inject.Singleton;

import kg.net.bazi.gsb4j.data.ClientInfo;

/**
 * Information about Google Safe Browsing API client implementation. Client info should uniquely identify a client
 * implementation, not an individual user. This class provides client info using project group id and version values.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
@Singleton
public class Gsb4jClientInfoProvider {

    private final String clientId;
    private final String clientVersion;

    Gsb4jClientInfoProvider() {
        clientId = "1.0";
        clientVersion = "1.0";
    }

    /**
     * Gets the client ID that uniquely identifies the client implementation of the Safe Browsing API.
     *
     * @return client id value
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Gets the version of the client implementation.
     *
     * @return client version
     */
    public String getClientVersion() {
        return clientVersion;
    }

    /**
     * Makes a client info for this implementation.
     *
     * @return client info instance
     */
    public ClientInfo make() {
        ClientInfo ci = new ClientInfo();
        ci.setClientId(clientId);
        ci.setClientVersion(clientVersion);
        return ci;
    }

}
