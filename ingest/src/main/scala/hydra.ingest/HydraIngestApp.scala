/*
 * Copyright (C) 2016 Pluralsight, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package hydra.ingest

import java.io.File

import com.typesafe.config.ConfigFactory
import hydra.core.app.HydraEntryPoint

/**
  * Just an example of how to bootstrap Hydra.
  *
  * Created by alexsilva on 2/18/16.
  */
// $COVERAGE-OFF$Disabling highlighting by default until a workaround for https://issues.scala-lang.org/browse/SI-8596 is found
object HydraIngestApp extends HydraEntryPoint with IngestionActors {

  val moduleName = "ingest"

  override val config = rootConfig.withFallback(ConfigFactory.parseFile(new File("/etc/hydra/hydra-ingest.conf")))

  buildContainer().start()
}
// $COVERAGE-ON