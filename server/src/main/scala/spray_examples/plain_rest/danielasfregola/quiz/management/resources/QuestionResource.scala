package spray_examples.plain_rest.danielasfregola.quiz.management.resources

import shapeless.list
import spray.http.{HttpEntity, MediaTypes}
import spray.routing.{Directive1, Route}
import spray_examples.plain_rest.danielasfregola.quiz.management.entities.{Question, QuestionUpdate}
import spray_examples.plain_rest.danielasfregola.quiz.management.routing.MyHttpService
import spray_examples.plain_rest.danielasfregola.quiz.management.services.QuestionService
import spray_examples.simple.Page

trait QuestionResource extends MyHttpService {

  val questionService: QuestionService

  def questionRoutes: Route =
//    route
  route ~ routePicsa


  def routePicsa=
  pathPrefix("questions") {
    pathEnd {
      post {
        val q: Directive1[Question] =entity(as[Question])
        import scala.reflect.runtime.universe.reify
        println(reify(entity(as[Question])))
        println("itt wok")
        q { question =>
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

  def route: Route =
    get{
      pathSingleSlash{
        complete{
          HttpEntity(
            MediaTypes.`text/html`,

            Page.skeleton.render
          )
        }
      } ~
        getFromDirectory("./client/target/scala-2.11")
    } ~
    post{
        path("ajax" / "list"){
          entity(as[String]) { e =>
            complete {
              upickle.default.write(list(e))
            }
          }
        }
      }

}
