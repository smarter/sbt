package sbt
package std

import Def.{ Initialize, Setting }
import sbt.internal.util.Types.{ idFun, Id }
import sbt.internal.util.AList
import sbt.internal.util.appmacro.{ Convert, Converted, Instance, MixedBuilder, MonadInstance }

object InitializeInstance extends MonadInstance {
  type M[x] = Initialize[x]
  def app[K[L[x]], Z](in: K[Initialize], f: K[Id] => Z)(implicit a: AList[K]): Initialize[Z] = Def.app[K, Z](in)(f)(a)
  def map[S, T](in: Initialize[S], f: S => T): Initialize[T] = Def.map(in)(f)
  def flatten[T](in: Initialize[Initialize[T]]): Initialize[T] = Def.bind(in)(idFun[Initialize[T]])
  def pure[T](t: () => T): Initialize[T] = Def.pure(t)
}

import language.experimental.macros
import scala.reflect._
import reflect.macros._

object InitializeConvert extends Convert {
  def apply[T: c.WeakTypeTag](c: Context)(nme: String, in: c.Tree): Converted[c.type] =
    if (nme == InputWrapper.WrapInitName) {
      val i = c.Expr[Initialize[T]](in)
      val t = c.universe.reify(i.splice).tree
      Converted.Success(t)
    } else if (nme == InputWrapper.WrapTaskName || nme == InputWrapper.WrapInitTaskName)
      Converted.Failure(in.pos, "A setting cannot depend on a task")
    else if (nme == InputWrapper.WrapPreviousName)
      Converted.Failure(in.pos, "A setting cannot depend on a task's previous value.")
    else
      Converted.NotApplicable
}

object SettingMacro {
  def settingMacroImpl[T: c.WeakTypeTag](c: Context)(t: c.Expr[T]): c.Expr[Initialize[T]] =
    Instance.contImpl[T, Id](c, InitializeInstance, InitializeConvert, MixedBuilder)(Left(t), Instance.idTransform[c.type])

  def settingDynMacroImpl[T: c.WeakTypeTag](c: Context)(t: c.Expr[Initialize[T]]): c.Expr[Initialize[T]] =
    Instance.contImpl[T, Id](c, InitializeInstance, InitializeConvert, MixedBuilder)(Right(t), Instance.idTransform[c.type])
}
