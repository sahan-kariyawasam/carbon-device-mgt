/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.jaxrs.api;

import io.swagger.annotations.*;
import org.wso2.carbon.apimgt.annotations.api.*;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * DeviceNotification management REST-API implementation.
 * All end points support JSON, XMl with content negotiation.
 */
@API(name = "Device Notification", version = "1.0.0", context = "/devicemgt_admin/notifications", tags = {"devicemgt_admin"})

// Below Api is for swagger annotations
@Api(value = "DeviceNotification", description = "Device notification related operations can be found here.")
@SuppressWarnings("NonJaxWsWebServices")
@Path("/notifications")
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public interface DeviceNotification {

    @GET
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            produces = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            httpMethod = "GET",
            value = "Getting all Device Notification Details",
            notes = "Get the details of all notifications that were pushed to the device in WSO2 EMM using "
                    + "this REST API",
            response = Notification.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Notifications", response = Notification.class,
                    responseContainer = "List"),
            @ApiResponse(code = 500, message = "Error occurred while retrieving the notification list")
    })
    @Permission(scope = "device-notification-view", permissions = {
            "/permission/admin/device-mgt/admin/notifications/view",
            "/permission/admin/device-mgt/user/notifications/view"})
    Response getNotifications();

    @GET
    @Path("{status}")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            produces = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            httpMethod = "GET",
            value = "Getting the Device Notifications Filtered by the Status",
            notes = "Get the details of all the unread notifications or the details of all the read "
                    + "notifications using this REST API",
            response = Notification.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Notifications", response = Notification.class,
                    responseContainer = "List"),
            @ApiResponse(code = 500, message = "Error occurred while retrieving the notification list")
    })
    @Permission(scope = "device-notification-view", permissions = {
            "/permission/admin/device-mgt/admin/notifications/view",
            "/permission/admin/device-mgt/user/notifications/view"})
    Response getNotificationsByStatus(@ApiParam(name = "status", value = "Provide the notification status as"
            + " the value for {status}", required = true)
                                      @PathParam("status") Notification.Status status);

    @PUT
    @Path("{id}/{status}")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            produces = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            httpMethod = "PUT",
            value = "Updating the Device Notification Status",
            notes = "When a user has read the the device notification the device notification status must "
                    + "change from NEW to CHECKED. Update the device notification status using this REST API")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Notification status updated successfully"),
            @ApiResponse(code = 500, message = "Error occurred while updating notification status")
    })
    @Permission(scope = "device-notification-modify",
            permissions = {"/permission/admin/device-mgt/admin/notifications/modify"})
    Response updateNotificationStatus(@ApiParam(name = "id", value = "Provide the ID of the notification"
            + " you wish you update", required = true) @PathParam("id") int id,
                                      @ApiParam(name = "status", value = "Provide the notification status as"
                                              + " the value", required = true) @PathParam("status")
                                      Notification.Status status);

    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            produces = MediaType.APPLICATION_JSON + ", " + MediaType.APPLICATION_XML,
            httpMethod = "POST",
            value = "Sending a Device Notification",
            notes = "Notify users on device operation failures and other information using this REST API")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Notification has been added successfully"),
            @ApiResponse(code = 500, message = "Error occurred while updating notification status")
    })
    @Permission(scope = "device-notification-modify",
            permissions = {"/permission/admin/device-mgt/admin/notifications/modify"})
    Response addNotification(Notification notification);

}
