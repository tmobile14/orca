/*
 * Copyright 2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.qos

import com.netflix.spinnaker.kork.transientconfig.TransientConfigService
import com.netflix.spinnaker.orca.qos.BufferState.ACTIVE
import com.netflix.spinnaker.orca.qos.BufferState.INACTIVE
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["qos.bufferingState.supplier"], havingValue = "killSwitch")
class KillSwitchBufferStateSupplier(
  private val transientConfigService: TransientConfigService
) : BufferStateSupplier {
  override fun get() =
    when (transientConfigService.isEnabled("qos.bufferingState.active", false)) {
      true  -> ACTIVE
      false -> INACTIVE
    }
}
