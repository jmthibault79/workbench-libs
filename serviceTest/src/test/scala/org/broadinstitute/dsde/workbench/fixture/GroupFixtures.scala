package org.broadinstitute.dsde.workbench.fixture

import com.typesafe.scalalogging.LazyLogging
import org.broadinstitute.dsde.workbench.service.Orchestration.groups.GroupRole
import org.broadinstitute.dsde.workbench.auth.AuthToken
import org.broadinstitute.dsde.workbench.config._
import org.broadinstitute.dsde.workbench.service.Orchestration
import org.broadinstitute.dsde.workbench.service.util.{ExceptionHandling, Util}
import org.scalatest.TestSuite

/**
  * Fixtures for creating and cleaning up test groups.
  */
trait GroupFixtures extends ExceptionHandling with LazyLogging { self: TestSuite =>

  def groupNameToEmail(groupName: String): String = s"GROUP_$groupName@${Config.GCS.appsDomain}"

  def withGroup(namePrefix: String, memberEmails: List[String] = List())
               (testCode: (String) => Any)
               (implicit token: AuthToken): Unit = {
    val groupName = Util.appendUnderscore(namePrefix) + Util.makeUuid

    try {
      Orchestration.groups.create(groupName)
      memberEmails foreach { email =>
        Orchestration.groups.addUserToGroup(groupName, email, GroupRole.Member)
      }

      testCode(groupName)

    } finally {
      memberEmails foreach { email =>
        Orchestration.groups.removeUserFromGroup(groupName, email, GroupRole.Member)
      }
      Orchestration.groups.delete(groupName)
    }
  }
}
