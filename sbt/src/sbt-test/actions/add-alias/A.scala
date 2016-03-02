object A extends dotty.runtime.LegacyApp {
  if(args(0).toBoolean) () else sys.error("Fail")
}
