package org.broadinstitute.dsde.workbench.model

import org.broadinstitute.dsde.workbench.model.google.{GoogleProject, ServiceAccount, ServiceAccountSubjectId}

/**
  * Created by mbemis on 8/23/17.
  */

object WorkbenchIdentityJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val WorkbenchEmailFormat = ValueObjectFormat(WorkbenchEmail)
  implicit val WorkbenchUserIdFormat = ValueObjectFormat(WorkbenchUserId)
  implicit val WorkbenchUserFormat = jsonFormat2(WorkbenchUser)

  implicit val WorkbenchGroupNameFormat = ValueObjectFormat(WorkbenchGroupName)
}

sealed trait WorkbenchSubject

case class WorkbenchEmail(value: String) extends ValueObject

case class WorkbenchUser(id: WorkbenchUserId, email: WorkbenchEmail)
case class WorkbenchUserId(value: String) extends WorkbenchSubject with ValueObject

trait WorkbenchGroup { val id: WorkbenchGroupIdentity; val members: Set[WorkbenchSubject]; val email: WorkbenchEmail }
trait WorkbenchGroupIdentity extends WorkbenchSubject
case class WorkbenchGroupName(value: String) extends WorkbenchGroupIdentity with ValueObject

case class PetServiceAccountId(userId: WorkbenchUserId, project: GoogleProject) extends WorkbenchSubject
case class PetServiceAccount(id: PetServiceAccountId, serviceAccount: ServiceAccount)