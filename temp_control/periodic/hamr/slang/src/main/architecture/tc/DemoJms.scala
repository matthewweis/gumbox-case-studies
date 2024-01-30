// #Sireum

package tc

import org.sireum._

object DemoJms extends App {

  def main(args: ISZ[String]): Z = {
    art.Art.run(
      DemoJmsArch.ad,
      DemoJmsArch.services,
      art.scheduling.legacy.Legacy(DemoJmsArch.ad.components)
    )
    return z"0"
  }


}
