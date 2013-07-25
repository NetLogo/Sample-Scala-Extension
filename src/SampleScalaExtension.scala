import org.nlogo.{ agent, api, nvm }
import api.Syntax._
import api.ScalaConversions._  // implicits

class SampleScalaExtension extends api.DefaultClassManager {
  def load(manager: api.PrimitiveManager) {
    manager.addPrimitive("first-n-integers", IntegerList)
    manager.addPrimitive("my-list", MyList)
    manager.addPrimitive("create-red-turtles", CreateRedTurtles)
    manager.addPrimitive("runresult", Runresult)
    manager.addPrimitive("runresult-of", RunresultOf)
  }
}

object IntegerList extends api.DefaultReporter {
  override def getSyntax =
    reporterSyntax(Array(NumberType), ListType)
  def report(args: Array[api.Argument], context: api.Context): AnyRef = {
    val n = try args(0).getIntValue
    catch {
      case e: api.LogoException =>
        throw new api.ExtensionException(e.getMessage)
    }
    if (n < 0)
      throw new api.ExtensionException("input must be positive")
    (0 until n).toLogoList
  }
}

object MyList extends api.DefaultReporter {
  override def getSyntax =
    reporterSyntax(Array(WildcardType | RepeatableType), ListType, 2)
  def report(args: Array[api.Argument], context: api.Context) =
    args.map(_.get).toLogoList
}

object CreateRedTurtles extends api.DefaultCommand with nvm.CustomAssembled {
  override def getSyntax =
    commandSyntax(Array(NumberType, CommandBlockType | OptionalType))
  // the command itself is observer-only. inside the block is turtle code.
  override def getAgentClassString = "O:-T--"
  // only box this once
  private val red = Double.box(15)
  def perform(args: Array[api.Argument], context: api.Context) {
    // the api package have what we need, so we'll often
    // be dropping down to the agent and nvm packages
    val n = args(0).getIntValue
    val world = context.getAgent.world.asInstanceOf[agent.World]
    val eContext = context.asInstanceOf[nvm.ExtensionContext]
    val nvmContext = eContext.nvmContext
    val agents =
      new agent.ArrayAgentSet(
        classOf[agent.Turtle], n, false, world)
    for(_ <- 0 until n) {
      val turtle = world.createTurtle(world.turtles)
      turtle.colorDoubleUnchecked(red)
      agents.add(turtle)
      eContext.workspace.joinForeverButtons(turtle)
    }
    // if the optional command block wasn't supplied, then there's not
    // really any point in calling this, but it won't bomb, either
    nvmContext.runExclusiveJob(agents, nvmContext.ip + 1)
    // prim._extern will take care of leaving nvm.Context ip in the right place
  }
  def assemble(a: nvm.AssemblerAssistant) {
    a.block()
    a.done()
  }
}

// the calling agent runs the task
object Runresult extends api.DefaultReporter {
  override def getSyntax =
    reporterSyntax(Array(ReporterTaskType), WildcardType)
  def report(args: Array[api.Argument], context: api.Context) =
    args(0).getReporterTask.report(context, Array())
}

// the calling agent has some other agent run the task
object RunresultOf extends api.DefaultReporter {
  override def getSyntax =
    reporterSyntax(Array(AgentType, ReporterTaskType), WildcardType)
  def report(args: Array[api.Argument], context: api.Context) = {
    // the api package doesn't have what we need, so we'll often
    // be dropping down to the agent and nvm packages
    val nvmContext = context.asInstanceOf[nvm.ExtensionContext].nvmContext
    val inputAgent = args(0).getAgent.asInstanceOf[agent.Agent]
    val task = args(1).getReporterTask.asInstanceOf[nvm.ReporterTask]
    val callingAgent = nvmContext.agent
    // how robust is this, really? I don't know without writing a lot of test cases.  sometimes it's
    // easy to write down plausible-looking engine code like this but then you find out it only
    // works in the simplest cases. - ST 7/25/13
    // it would probably be more correct to swap out context.myself, too, so that if the task
    // uses `myself` it would refer to callingAgent and not to callingAgent's caller - ST 7/25/13
    nvmContext.agent = inputAgent
    try task.report(nvmContext, Array[AnyRef]())
    finally nvmContext.agent = callingAgent
  }
}
