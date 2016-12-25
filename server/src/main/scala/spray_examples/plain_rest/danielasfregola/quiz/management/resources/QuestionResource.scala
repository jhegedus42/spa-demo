package spray_examples.plain_rest.danielasfregola.quiz.management.resources

import spray_examples.plain_rest.danielasfregola.quiz.management.entities.{QuestionUpdate, Question}
import spray_examples.plain_rest.danielasfregola.quiz.management.routing.MyHttpService
import spray_examples.plain_rest.danielasfregola.quiz.management.services.QuestionService
import spray.routing._

trait QuestionResource extends MyHttpService {

  val questionService: QuestionService

  def questionRoutes: Route = pathPrefix("questions") {
    pathEnd {
      post {
        entity(as[Question]) { question =>
          completeWithLocationHeader(
            resourceId = questionService.createQuestion(question),
            ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
    } ~
    path(Segment) { id =>
      get {
        complete(questionService.getQuestion(id))
      } ~
      put {
        entity(as[QuestionUpdate]) { update =>
          complete(questionService.updateQuestion(id, update))
        }
      } ~
      delete {
        complete(204, questionService.deleteQuestion(id))
      }
    }
  }
}
