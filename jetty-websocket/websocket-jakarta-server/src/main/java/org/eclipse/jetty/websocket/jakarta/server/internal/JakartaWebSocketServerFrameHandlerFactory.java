//
// ========================================================================
// Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under
// the terms of the Eclipse Public License 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0
//
// This Source Code may also be made available under the following
// Secondary Licenses when the conditions for such availability set
// forth in the Eclipse Public License, v. 2.0 are satisfied:
// the Apache License v2.0 which is available at
// https://www.apache.org/licenses/LICENSE-2.0
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.websocket.jakarta.server.internal;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.jetty.http.pathmap.UriTemplatePathSpec;
import org.eclipse.jetty.websocket.core.FrameHandler;
import org.eclipse.jetty.websocket.core.server.FrameHandlerFactory;
import org.eclipse.jetty.websocket.core.server.ServerUpgradeRequest;
import org.eclipse.jetty.websocket.core.server.ServerUpgradeResponse;
import org.eclipse.jetty.websocket.jakarta.client.JakartaWebSocketClientFrameHandlerFactory;
import org.eclipse.jetty.websocket.jakarta.common.JakartaWebSocketContainer;
import org.eclipse.jetty.websocket.jakarta.common.JakartaWebSocketFrameHandlerMetadata;

public class JakartaWebSocketServerFrameHandlerFactory extends JakartaWebSocketClientFrameHandlerFactory implements FrameHandlerFactory
{
    public JakartaWebSocketServerFrameHandlerFactory(JakartaWebSocketContainer container)
    {
        super(container, new PathParamIdentifier());
    }

    @Override
    public JakartaWebSocketFrameHandlerMetadata getMetadata(Class<?> endpointClass, EndpointConfig endpointConfig)
    {
        if (jakarta.websocket.Endpoint.class.isAssignableFrom(endpointClass))
            return createEndpointMetadata(endpointConfig);

        ServerEndpoint anno = endpointClass.getAnnotation(ServerEndpoint.class);
        if (anno == null)
            return super.getMetadata(endpointClass, endpointConfig);

        UriTemplatePathSpec templatePathSpec = new UriTemplatePathSpec(anno.value());
        JakartaWebSocketFrameHandlerMetadata metadata = new JakartaWebSocketFrameHandlerMetadata(endpointConfig);
        metadata.setUriTemplatePathSpec(templatePathSpec);
        return discoverJakartaFrameHandlerMetadata(endpointClass, metadata);
    }

    @Override
    public FrameHandler newFrameHandler(Object websocketPojo, ServerUpgradeRequest upgradeRequest, ServerUpgradeResponse upgradeResponse)
    {
        return newJakartaWebSocketFrameHandler(websocketPojo, new JakartaServerUpgradeRequest(upgradeRequest));
    }
}