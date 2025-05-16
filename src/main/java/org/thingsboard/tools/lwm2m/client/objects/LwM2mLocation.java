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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.leshan.client.servers.ServerIdentity;
import org.eclipse.leshan.core.response.ReadResponse;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LwM2mLocation extends LwM2mBaseInstanceEnabler {

    private float latitude;
    private float longitude;
    private float scaleFactor;
    private Date timestamp;

    public LwM2mLocation() {
        this(null, null, 1.0f);
    }

    public LwM2mLocation(Float latitude, Float longitude, float scaleFactor) {

        if (latitude != null) {
            this.latitude = latitude + 90f;
        } else {
            this.latitude = RANDOM.nextInt(180);
        }
        if (longitude != null) {
            this.longitude = longitude + 180f;
        } else {
            this.longitude = RANDOM.nextInt(360);
        }
        this.scaleFactor = scaleFactor;
        timestamp = new Date();
    }

    public LwM2mLocation(Float latitude, Float longitude, float scaleFactor, ScheduledExecutorService executorService, Integer id) {
        try {
            if (id != null) this.setId(id);
            if (latitude != null) {
                this.latitude = latitude + 90f;
            } else {
                this.latitude = RANDOM.nextInt(180);
            }
            if (longitude != null) {
                this.longitude = longitude + 180f;
            } else {
                this.longitude = RANDOM.nextInt(360);
            }
            this.scaleFactor = scaleFactor;
            timestamp = new Date();
            executorService.scheduleWithFixedDelay(() ->
                    fireResourcesChange(0, 1), 10000, 10000, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            log.error("[{}]Throwable", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public ReadResponse read(ServerIdentity identity, int resourceId) {
//        log.info("Read on Location resource /[{}]/[{}]/[{}]", getModel().id, getId(), resourceid);
        resourceId = getSupportedResource (resourceId);
        switch (resourceId) {
            case 0:
                return ReadResponse.success(resourceId, getLatitude());
            case 1:
                return ReadResponse.success(resourceId, getLongitude());
            case 5:
                return ReadResponse.success(resourceId, getTimestamp());
            default:
                return super.read(identity, resourceId);
        }
    }

    public void moveLocation(String nextMove) {
        switch (nextMove.charAt(0)) {
            case 'w':
                moveLatitude(1.0f);
//                log.info("Move to North [{}]/[{}]", getLatitude(), getLongitude());
                break;
            case 'a':
                moveLongitude(-1.0f);
//                log.info("Move to East [{}]/[{}]", getLatitude(), getLongitude());
                break;
            case 's':
                moveLatitude(-1.0f);
//                log.info("Move to South [{}]/[{}]", getLatitude(), getLongitude());
                break;
            case 'd':
                moveLongitude(1.0f);
//                log.info("Move to West [{}]/[{}]", getLatitude(), getLongitude());
                break;
        }
    }

    private void moveLatitude(float delta) {
        latitude = latitude + delta * scaleFactor;
        timestamp = new Date();
        fireResourcesChange(0, 5);
    }

    private void moveLongitude(float delta) {
        longitude = longitude + delta * scaleFactor;
        timestamp = new Date();
        fireResourcesChange(1, 5);
    }

    public float getLatitude() {
        return latitude - 90.0f;
    }

    public float getLongitude() {
        return longitude - 180.f;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
