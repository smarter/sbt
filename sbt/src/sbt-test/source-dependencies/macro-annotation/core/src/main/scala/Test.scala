@hello
case class Test(x: Int)

object Main extends dotty.runtime.LegacyApp {
  Test(3).hello
}