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

package org.wso2.carbon.device.mgt.jaxrs.api.impl;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.operation.mgt.Activity;
import org.wso2.carbon.device.mgt.jaxrs.api.common.MDMAPIException;
import org.wso2.carbon.device.mgt.jaxrs.api.context.DeviceOperationContext;
import org.wso2.carbon.device.mgt.jaxrs.api.util.MDMIOSOperationUtil;
import org.wso2.carbon.device.mgt.jaxrs.beans.ApplicationWrapper;
import org.wso2.carbon.device.mgt.jaxrs.beans.MobileApp;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.PaginationRequest;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.device.mgt.common.Platform;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManager;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.app.mgt.ApplicationManagementProviderService;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.jaxrs.api.util.DeviceMgtAPIUtils;
import org.wso2.carbon.device.mgt.jaxrs.api.util.MDMAndroidOperationUtil;
import org.wso2.carbon.device.mgt.jaxrs.api.util.ResponsePayload;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Operation related REST-API implementation.
 */
@SuppressWarnings("NonJaxWsWebServices")
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class OperationImpl implements org.wso2.carbon.device.mgt.jaxrs.api.Operation {

    private static Log log = LogFactory.getLog(OperationImpl.class);

    /* @deprecated */
    @Override
    @GET
    public Response getAllOperations() {
        List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations;
        DeviceManagementProviderService dmService;
        try {
            dmService = DeviceMgtAPIUtils.getDeviceManagementService();
            operations = dmService.getOperations(null);
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the operations for the device.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        return Response.status(Response.Status.OK).entity(operations).build();
    }

    @Override
    @GET
    @Path("paginate/{type}/{id}")
    public Response getDeviceOperations(@PathParam("type") String type, @PathParam("id") String id,
                                        @QueryParam("start") int startIdx, @QueryParam("length") int length,
                                        @QueryParam("search") String search) {
        PaginationResult operations;
        DeviceManagementProviderService dmService;
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        PaginationRequest paginationRequest = new PaginationRequest(startIdx, length);
        try {
            deviceIdentifier.setType(type);
            deviceIdentifier.setId(id);
            dmService = DeviceMgtAPIUtils.getDeviceManagementService();
            operations = dmService.getOperations(deviceIdentifier, paginationRequest);
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the operations for the device.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        return Response.status(Response.Status.OK).entity(operations).build();
    }

    @Override
    @GET
    @Path("{type}/{id}")
    public Response getAllDeviceOperations(@PathParam("type") String type, @PathParam("id") String id) {
        List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations;
        DeviceManagementProviderService dmService;
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        try {
            deviceIdentifier.setType(type);
            deviceIdentifier.setId(id);
            dmService = DeviceMgtAPIUtils.getDeviceManagementService();
            operations = dmService.getOperations(deviceIdentifier);
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the operations for the device.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        return Response.status(Response.Status.OK).entity(operations).build();
    }

    /* @deprecated */
    @Override
    @POST
    public Response addOperation(DeviceOperationContext operationContext) {
        DeviceManagementProviderService dmService;
        ResponsePayload responseMsg = new ResponsePayload();
        try {
            dmService = DeviceMgtAPIUtils.getDeviceManagementService();

            //TODO: Fix this properly later adding device type to be passed in when the task manage executes "addOperations()"
            String type = null;
            List<DeviceIdentifier> deviceIdentifiers = operationContext.getDevices();
            if (deviceIdentifiers.size() > 0) {
                type = deviceIdentifiers.get(0).getType();
            }
            Activity activity = dmService.addOperation(type, operationContext.getOperation(), operationContext.getDevices());
            if (activity != null) {
                responseMsg.setStatusCode(HttpStatus.SC_CREATED);
                responseMsg.setMessageFromServer("Operation has added successfully.");
            }
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (OperationManagementException e) {
            String msg = "Error occurred while saving the operation";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @Override
    @GET
    @Path("{type}/{id}/apps")
    public Response getInstalledApps(@PathParam("type") String type, @PathParam("id") String id) {
        List<Application> applications;
        ApplicationManagementProviderService appManagerConnector;
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        try {
            deviceIdentifier.setType(type);
            deviceIdentifier.setId(id);
            appManagerConnector = DeviceMgtAPIUtils.getAppManagementService();
            applications = appManagerConnector.getApplicationListForDevice(deviceIdentifier);
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while fetching the apps of the device.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        return Response.status(Response.Status.CREATED).entity(applications).build();
    }

    @Override
    @POST
    @Path("installApp/{tenantDomain}")
    public Response installApplication(ApplicationWrapper applicationWrapper,
                                       @PathParam("tenantDomain") String tenantDomain) {
        ResponsePayload responseMsg = new ResponsePayload();
        ApplicationManager appManagerConnector;
        org.wso2.carbon.device.mgt.common.operation.mgt.Operation operation = null;
        Activity activity = null;
        try {
            appManagerConnector = DeviceMgtAPIUtils.getAppManagementService();
            MobileApp mobileApp = applicationWrapper.getApplication();

            if (applicationWrapper.getDeviceIdentifiers() != null) {
                for (DeviceIdentifier deviceIdentifier : applicationWrapper.getDeviceIdentifiers()) {
                    if (deviceIdentifier.getType().equals(Platform.android.toString())) {
                        operation = MDMAndroidOperationUtil.createInstallAppOperation(mobileApp);
                    } else if (deviceIdentifier.getType().equals(Platform.ios.toString())) {
                        operation = MDMIOSOperationUtil.createInstallAppOperation(mobileApp);
                    }
                }
                activity = appManagerConnector.installApplicationForDevices(operation, applicationWrapper.getDeviceIdentifiers());
            }
            responseMsg.setStatusCode(HttpStatus.SC_CREATED);
            responseMsg.setMessageFromServer("Authentication installation request has been sent to the device.");
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (ApplicationManagementException | MDMAPIException e) {
            String msg = "Error occurred while saving the operation";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @Override
    @POST
    @Path("uninstallApp/{tenantDomain}")
    public Response uninstallApplication(ApplicationWrapper applicationWrapper,
                                         @PathParam("tenantDomain") String tenantDomain) {
        ResponsePayload responseMsg = new ResponsePayload();
        ApplicationManager appManagerConnector;
        org.wso2.carbon.device.mgt.common.operation.mgt.Operation operation = null;
        Activity activity = null;
        try {
            appManagerConnector = DeviceMgtAPIUtils.getAppManagementService();
            MobileApp mobileApp = applicationWrapper.getApplication();

            if (applicationWrapper.getDeviceIdentifiers() != null) {
                for (DeviceIdentifier deviceIdentifier : applicationWrapper.getDeviceIdentifiers()) {
                    if (deviceIdentifier.getType().equals(Platform.android.toString())) {
                        operation = MDMAndroidOperationUtil.createAppUninstallOperation(mobileApp);
                    } else if (deviceIdentifier.getType().equals(Platform.ios.toString())) {
                        operation = MDMIOSOperationUtil.createAppUninstallOperation(mobileApp);
                    }
                }
                activity = appManagerConnector.installApplicationForDevices(operation, applicationWrapper.getDeviceIdentifiers());
            }
            responseMsg.setStatusCode(HttpStatus.SC_CREATED);
            responseMsg.setMessageFromServer("Authentication removal request has been sent to the device.");
            return Response.status(Response.Status.CREATED).entity(activity).build();
        } catch (ApplicationManagementException | MDMAPIException e) {
            String msg = "Error occurred while saving the operation";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @Override
    @GET
    @Path("activity/{id}")
    public Response getActivity( @PathParam("id") String id)
            throws MDMAPIException {
        Activity activity;
        DeviceManagementProviderService dmService;
        try {
            dmService = DeviceMgtAPIUtils.getDeviceManagementService();
            activity = dmService.getOperationByActivityId(id);
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the activity for the supplied id.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
         return Response.status(Response.Status.OK).entity(activity).build();
    }


    @Override
    @GET
    @Path("activity/after/{timestamp}")
    public Response getActivityUpdatedAfter(@PathParam("timestamp") String timestamp)
            throws MDMAPIException {
        List<Activity> activities;
        DeviceManagementProviderService dmService;
        try {
            dmService = DeviceMgtAPIUtils.getDeviceManagementService();
            activities = dmService.getActivitiesUpdatedAfter(Long.parseLong(timestamp));
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the activities updated after given time stamp.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(Response.Status.OK).entity(activities).build();
    }

}