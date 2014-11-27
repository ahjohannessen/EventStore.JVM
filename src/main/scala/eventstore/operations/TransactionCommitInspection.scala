package eventstore
package operations

import OperationError._
import Inspection.Decision._

private[eventstore] class TransactionCommitInspection(out: TransactionCommit)
    extends ErrorInspection[TransactionCommitCompleted, OperationError] {

  def decision(error: OperationError) = {
    error match {
      case PrepareTimeout       => Retry
      case CommitTimeout        => Retry
      case ForwardTimeout       => Retry
      case WrongExpectedVersion => Fail(wrongExpectedVersionException)
      case StreamDeleted        => Fail(streamDeletedException)
      case InvalidTransaction   => Fail(InvalidTransactionException)
      case AccessDenied         => Fail(new AccessDeniedException(s"Write access denied"))
    }
  }

  def transactionId = out.transactionId

  def wrongExpectedVersionException = {
    WrongExpectedVersionException(s"Transaction commit failed due to WrongExpectedVersion, transactionId: $transactionId")
  }

  def streamDeletedException = {
    new StreamDeletedException(s"Transaction commit due to stream has been deleted, transactionId: $transactionId")
  }
}