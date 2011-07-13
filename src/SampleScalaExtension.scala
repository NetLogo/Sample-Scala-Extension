import org.nlogo.api._
import org.nlogo.api.Syntax._
import org.nlogo.api.ScalaConversions._

class SampleScalaExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("first-n-integers", new IntegerList)
    manager.addPrimitive("my-list", new MyList)
  }
}

class IntegerList extends DefaultReporter {
  override def getSyntax = reporterSyntax(Array(NumberType), ListType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val n = try args(0).getIntValue
    catch {
      case e: LogoException =>
        throw new ExtensionException(e.getMessage)
    }
    if (n < 0)
      throw new ExtensionException("input must be positive")
    (0 until n).toLogoList
  }
}

class MyList extends DefaultReporter {
  override def getSyntax = reporterSyntax(Array(WildcardType | RepeatableType), ListType, 2)
  def report(args: Array[Argument], context: Context) =
    args.map(_.get).toLogoList
}
