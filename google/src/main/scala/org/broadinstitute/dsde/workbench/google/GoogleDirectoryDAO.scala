package org.broadinstitute.dsde.workbench.google

import com.google.api.services.admin.directory.model.Group
import org.broadinstitute.dsde.workbench.model._

import scala.concurrent.Future

/**
  * Created by mbemis on 8/17/17.
  */

trait GoogleDirectoryDAO {

  @deprecated(message = "use createGroup(String, WorkbenchEmail) instead", since = "0.9")
  def createGroup(groupName: WorkbenchGroupName, groupEmail: WorkbenchEmail): Future[Unit]
  def createGroup(displayName: String, groupEmail: WorkbenchEmail): Future[Unit]
  def deleteGroup(groupEmail: WorkbenchEmail): Future[Unit]
  def addMemberToGroup(groupEmail: WorkbenchEmail, memberEmail: WorkbenchEmail): Future[Unit]
  def removeMemberFromGroup(groupEmail: WorkbenchEmail, memberEmail: WorkbenchEmail): Future[Unit]
  def getGoogleGroup(groupEmail: WorkbenchEmail): Future[Option[Group]]
  def isGroupMember(groupEmail: WorkbenchEmail, memberEmail: WorkbenchEmail): Future[Boolean]
  def listGroupMembers(groupEmail: WorkbenchEmail): Future[Option[Seq[String]]]
}
