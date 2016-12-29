package spray_examples.plain_rest.danielasfregola.quiz.management

import spray_examples.plain_rest.danielasfregola.quiz.management.resources.QuestionResource
import spray_examples.plain_rest.danielasfregola.quiz.management.services.QuestionService
import spray.routing._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  def receive = runRoute(routes)

  val questionService = new QuestionService

  def routes: Route = questionRoutes


}

trait Resources extends QuestionResource
