package eventstore
package core
package operations

import ReadEventError._
import Inspection.Decision._

private[eventstore] final case class ReadEventInspection(out: ReadEvent)
    extends ErrorInspection[ReadEventCompleted, ReadEventError] {

  def decision(error: ReadEventError) = {
    val result = error match {
      case EventNotFound  => EventNotFoundException(streamId, out.eventNumber)
      case StreamNotFound => StreamNotFoundException(streamId)
      case StreamDeleted  => StreamDeletedException(s"Read failed due to $streamId has been deleted")
      case Error(msg)     => ServerErrorException(msg.orNull)
      case AccessDenied   => AccessDeniedException(s"Read access denied for $streamId")
    }

    Fail(result)
  }

  def streamId = out.streamId
}