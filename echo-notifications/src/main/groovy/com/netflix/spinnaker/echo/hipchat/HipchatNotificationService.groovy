/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.echo.hipchat

import com.netflix.spinnaker.echo.notification.NotificationService
import com.netflix.spinnaker.echo.api.Notification
import com.netflix.spinnaker.echo.notification.NotificationTemplateEngine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty('hipchat.enabled')
class HipchatNotificationService implements NotificationService {
  private static Notification.Type TYPE = Notification.Type.HIPCHAT

  @Autowired
  HipchatService hipchat

  @Autowired
  NotificationTemplateEngine notificationTemplateEngine

  @Value('${hipchat.token}')
  String token

  @Override
  boolean supportsType(Notification.Type type) {
    return type == TYPE
  }

  @Override
  void handle(Notification notification) {
    def body = notificationTemplateEngine.build(notification, NotificationTemplateEngine.Type.BODY)
    notification.to.each {
      hipchat.sendMessage(token, it, new HipchatMessage(
          message: body,
          notify: notification.severity == Notification.Severity.HIGH
      ))
    }
  }
}
