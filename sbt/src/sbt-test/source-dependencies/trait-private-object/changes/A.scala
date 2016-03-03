trait A {
  val foo = 0 + X.a
  // Workaround https://github.com/lampepfl/dotty/issues/1140
  /*private*/ object X { val a = 1 }
}
