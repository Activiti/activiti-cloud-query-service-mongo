/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.services.audit.mongo.events;


import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.events.TaskRuntimeEvent;

import com.querydsl.core.annotations.QueryEntity;

@QueryEntity
public class TaskCreatedEventDocument extends TaskAuditEventDocument {

    protected static final String TASK_CREATED_EVENT = "TaskCreatedEvent";

    public TaskCreatedEventDocument() {
    }

    public TaskCreatedEventDocument(String eventId,
                                    Long timestamp) {
        super(eventId,
              timestamp,
              TaskRuntimeEvent.TaskEvents.TASK_CREATED.name());
    }

    public TaskCreatedEventDocument(String eventId,
                                    Long timestamp,
                                    String appName,
                                    String appVersion,
                                    String serviceName,
                                    String serviceFullName,
                                    String serviceType,
                                    String serviceVersion,
                                    Task task) {
        super(eventId,
              timestamp,
              TaskRuntimeEvent.TaskEvents.TASK_CREATED.name(),
              appName,
              appVersion,
              serviceName,
              serviceFullName,
              serviceType,
              serviceVersion,
              task);
    }
}