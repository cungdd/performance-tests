/**
 * Copyright © 2016-2025 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.tools.lwm2m.client.objects;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;

import javax.security.auth.Destroyable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class LwM2mBaseInstanceEnabler extends BaseInstanceEnabler  implements Destroyable {
//public class LwM2mBaseInstanceEnabler extends LwObjectEnabler2 {
    protected static final Random RANDOM = new Random();
    protected List<Integer> supportedResources;
    protected List<Integer> readableResourceIds = new ArrayList<>();

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        if (this.supportedResources == null) {
            this.model = this.model == null ? model : this.model;
            // By default we consider that all resources defined in the model are supported
            this.supportedResources = new ArrayList<>(model.resources.keySet());
            Collections.sort(this.supportedResources);
        }
        return this.supportedResources;
    }

    protected int getSupportedResource (int resourceId) {
        List<Integer> supportedResources = this.getSupportedResources();
        return supportedResources != null && supportedResources.contains(resourceId) ? resourceId : -1;
    }

    protected void updateSupportedResources (List<Integer> updateSupRes, boolean isAdd) {
        if (isAdd) {
            Set<Integer> supportedSet =  Set.copyOf(this.supportedResources);
            supportedSet.addAll(updateSupRes);
            this.supportedResources =  new ArrayList<>(supportedSet);
        }
        else {
            this.supportedResources.removeAll(updateSupRes);
        }
    }

    protected List<Integer> getReadableResourceIds() {
        return model != null ? readableResourceIds = model.resources.entrySet().stream()
                .filter(rez -> rez.getValue().operations.isReadable()).map(s -> s.getValue().id)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    private List<Integer> getSupportedResources () {
        return this.supportedResources != null ? this.supportedResources : this.getAvailableResourceIds(this.model);
    }
}
