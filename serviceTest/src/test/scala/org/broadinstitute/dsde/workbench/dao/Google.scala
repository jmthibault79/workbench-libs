package org.broadinstitute.dsde.workbench.dao

import akka.actor.ActorSystem
import org.broadinstitute.dsde.workbench.config.Config
import org.broadinstitute.dsde.workbench.google.HttpGoogleIamDAO

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Google {
  val appName = "automation"
  val metricBaseName = appName
  lazy val system = ActorSystem()
  val ec: ExecutionContextExecutor = ExecutionContext.global

  lazy val googleIamDAO = new HttpGoogleIamDAO(Config.GCS.qaEmail, Config.GCS.pathToQAPem, appName, metricBaseName)(system, ec)
}