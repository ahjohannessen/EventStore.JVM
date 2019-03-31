package eventstore
package core
package operations

import ReadAllEventsError._
import Inspection.Decision._

private[eventstore] final case class ReadAllEventsInspection(out: ReadAllEvents)
    extends ErrorInspection[ReadAllEventsCompleted, ReadAllEventsError] {

  def decision(error: ReadAllEventsError) = {
    error match {
      case Error(msg)   => Fail(ServerErrorException(msg.orNull))
      case AccessDenied => Fail(AccessDeniedException(s"Read access denied for $streamId"))
    }
  }

  def streamId = EventStream.All
}