import org.nlogo.{ agent, api, nvm }
import api.Syntax._
import api.ScalaConversions._  // implicits

class SampleScalaExtension extends api.DefaultClassManager {
  def load(manager: api.PrimitiveManager) {
    manager.addPrimitive("first-n-integers", IntegerList)
    manager.addPrimitive("my-list", MyList)
    manager.addPrimitive("create-red-turtles", CreateRedTurtles)
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
