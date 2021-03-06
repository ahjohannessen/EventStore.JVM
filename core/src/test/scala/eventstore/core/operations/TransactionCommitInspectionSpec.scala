package eventstore
package core
package operations

import scala.util.{ Failure, Success }
import org.specs2.mutable.Specification
import Inspection.Decision.{Retry, Stop, Fail}
import OperationError._
import TestData._

class TransactionCommitInspectionSpec extends Specification {
  val inspection = TransactionCommitInspection(transactionCommit).pf

  "TransactionCommitInspection" should {

    "handle TransactionCommitCompleted" in {
      inspection(Success(transactionCommitCompleted)) mustEqual Stop
    }

    "handle PrepareTimeout" in {
      inspection(Failure(PrepareTimeout)) mustEqual Retry
    }

    "handle CommitTimeout" in {
      inspection(Failure(CommitTimeout)) mustEqual Retry
    }

    "handle ForwardTimeout" in {
      inspection(Failure(ForwardTimeout)) mustEqual Retry
    }

    "handle WrongExpectedVersion" in {
      inspection(Failure(WrongExpectedVersion)) must beLike {
        case Fail(_: WrongExpectedVersionException) => ok
      }
    }

    "handle StreamDeleted" in {
      inspection(Failure(StreamDeleted)) must beLike {
        case Fail(_: StreamDeletedException) => ok
      }
    }

    "handle InvalidTransaction" in {
      inspection(Failure(InvalidTransaction)) mustEqual Fail(InvalidTransactionException)
    }

    "handle AccessDenied" in {
      inspection(Failure(AccessDenied)) must beLike {
        case Fail(_: AccessDeniedException) => ok
      }
    }
  }
}
