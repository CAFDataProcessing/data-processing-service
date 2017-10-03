/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cafdataprocessing.utilities.taskreceiver.taskmessage;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Decode methods tied to task data.
 */
public class TaskDataDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger( TaskDataDecoder.class );

    /**
     * Replaces a base64 encoded taskData node on provided node with the decoded version on the provided JsonNode object.
     * @param node Node containing task data node to decode.
     * @param mapper Mapper to use in decoding.
     */
    public static void decodeTaskData( JsonNode node, ObjectMapper mapper )
    {
        if ( node.has(TaskMessageConstants.TASK_DATA) )
        {
            // carry on and decode the item to a string, and put back onto the JSON node
            attemptToDecodeNode( node, mapper, TaskMessageConstants.TASK_DATA );
        }
        else{
            LOGGER.warn("JSON passed to TaskDataDecoder has no property "+TaskMessageConstants.TASK_DATA);
        }
    }

    /**
     * Attempts to decode a node with the provided name on the provided JsonNode from base64 to a string representation. Updating it on the provided JsonNode object.
     * @param node Node containing node that should be decoded.
     * @param mapper Mapper to use in decoding.
     * @param nodeName Name of the node that should be decoded.
     */
    public static void attemptToDecodeNode( JsonNode node, ObjectMapper mapper, String nodeName )
    {
        byte[] data;
        try
        {
            data = node.get( nodeName ).binaryValue();
        }
        catch ( IOException ex )
        {
            LOGGER.debug("attemptToDecodeNode caught error: ", ex );
            return;
        }

        if ( data != null && data.length > 0 )
        {
            try
            {
                // Perform a simple replacement of the binary blob which is UTF8 encoded with a STRING version.
                // In this way people will be able to read its content easily.
                ( (ObjectNode) node ).put( nodeName, mapper.readTree( new String( data, StandardCharsets.UTF_8 ) ) );
            }
            catch(JsonParseException e){
                LOGGER.debug("attemptToDecodeNode encountered a parse exception trying to replace binary data with ObjectNode representation. Attempting to replace with String value.", e);
                ( (ObjectNode) node ).put(nodeName, new String(data, StandardCharsets.UTF_8));
            }
            catch ( Exception ex )
            {
                LOGGER.debug("attemptToDecodeNode couldn't map requested node: " + nodeName + " to an ObjectNode representation.", ex );
            }
        }
    }
            
}
