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

package org.activiti.cloud.services.audit.mongo;

import com.querydsl.core.types.Predicate;
import org.activiti.cloud.alfresco.data.domain.AlfrescoPagedResourcesAssembler;
import org.activiti.cloud.services.audit.mongo.assembler.EventResourceAssembler;
import org.activiti.cloud.services.audit.mongo.events.ProcessEngineEventDocument;
import org.activiti.cloud.services.audit.mongo.repository.EventsRepository;
import org.activiti.cloud.services.audit.mongo.resources.EventResource;
import org.activiti.cloud.services.security.SecurityPoliciesApplicationService;
import org.activiti.cloud.services.security.SecurityPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/" + EventsRelProvider.COLLECTION_RESOURCE_REL, produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class ProcessEngineEventsController {

    private final EventsRepository eventsRepository;

    private EventResourceAssembler eventResourceAssembler;

    private AlfrescoPagedResourcesAssembler<ProcessEngineEventDocument> pagedResourcesAssembler;

    private SecurityPoliciesApplicationService securityPoliciesApplicationService;

    @Autowired
    public ProcessEngineEventsController(EventsRepository eventsRepository,
                                         EventResourceAssembler eventResourceAssembler,
                                         AlfrescoPagedResourcesAssembler<ProcessEngineEventDocument> pagedResourcesAssembler,
                                         SecurityPoliciesApplicationService securityPoliciesApplicationService) {
        this.eventsRepository = eventsRepository;
        this.eventResourceAssembler = eventResourceAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.securityPoliciesApplicationService = securityPoliciesApplicationService;
    }

    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    public EventResource findById(@PathVariable String eventId) {
        Optional<ProcessEngineEventDocument> findResult = eventsRepository.findById(eventId);
        if (!findResult.isPresent()) {
            throw new RuntimeException("Unable to find event for the given id:'" + eventId + "'");
        }
        ProcessEngineEventDocument processEngineEventEntity = findResult.get();
        if (!securityPoliciesApplicationService.canRead(processEngineEventEntity.getProcessDefinitionId(),processEngineEventEntity.getServiceName())){
            throw new RuntimeException("Operation not permitted for " + processEngineEventEntity.getProcessDefinitionId());
        }
        return eventResourceAssembler.toResource(processEngineEventEntity);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<EventResource> findAll(@QuerydslPredicate(root = ProcessEngineEventDocument.class) Predicate predicate,
                                                 Pageable pageable) {

        predicate = securityPoliciesApplicationService.restrictProcessEngineEventQuery(predicate,
                SecurityPolicy.READ);

        return pagedResourcesAssembler.toResource(pageable,eventsRepository.findAll(predicate,
                                                                           pageable),
                                                  eventResourceAssembler);
    }
}